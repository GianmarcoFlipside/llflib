package com.llf.update;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

import com.llf.update.progress.IProgress;
import com.llf.update.progress.NotifyProgress;
import com.llf.update.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by llf on 2015/8/10.
 */
public class DownloadService extends Service {
    public static final String TAG = "DownloadService";
    public static final int TASK_ID_CHECK = 0x0;
    public static final int TASK_ID_DOWN = 0x1;
    public static final int TASK_ID_CANCEL = 0x2;

    public static final String EXTRA_TASK = "extral_task";
    public static final String EXTRA_URL = "extral_url";
    public static final String EXTRA_HIT_SHOW = "extra_hit_show";

    private AtomicBoolean mCancelAtomic;
    private volatile ServiceHandler mServiceHandler;
    private Handler mMainHandler;
    private Runnable mPreCancel;

    private IProgress mNotify;

    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override public void handleMessage(Message msg) {
            DownloadService.this.handleMessage(msg);
        }
    }

    public DownloadService() {
        super();
    }

    @Override public void onCreate() {
        super.onCreate();
        HandlerThread thread = new HandlerThread("LLFUpdateService");
        thread.start();

        mCancelAtomic = new AtomicBoolean(false);
        mServiceHandler = new ServiceHandler(thread.getLooper());
        mMainHandler = new Handler();
    }

    @Override public void onStart(Intent intent, int startId) {
        mServiceHandler.obtainMessage(startId, intent).sendToTarget();
    }

    @Override public int onStartCommand(Intent intent, int flags, int startId) {
        int taskId = intent.getIntExtra(EXTRA_TASK, 0);
        if (taskId == TASK_ID_CANCEL) {
            mCancelAtomic.set(true);
            stopTask(startId, 0);
        } else {
            mServiceHandler.obtainMessage(startId, intent).sendToTarget();
        }
        return START_REDELIVER_INTENT;
    }

    @Override public void onDestroy() {
        super.onDestroy();
        mServiceHandler.getLooper().quit();
        mNotify = null;
    }

    @Override public IBinder onBind(Intent intent) {
        return null;
    }

    private void stopTask(final int taskId, int delay) {
        if (mPreCancel != null) {
            mMainHandler.removeCallbacks(mPreCancel);
            mPreCancel = null;
        }
        mPreCancel = new Runnable() {
            @Override public void run() {
                stopSelf(taskId);
            }
        };
        mMainHandler.postDelayed(mPreCancel, delay);
    }

    //toast
    private void showHit(final String msg) {
        mMainHandler.post(new Runnable() {
            @Override public void run() {
                UpdateHelper.showCheckMessage(DownloadService.this, msg);
                UpdateHelper.hideHitIfNeed(DownloadService.this);
            }
        });
    }

    private void showNetSetting(final CheckBean bean) {
        mMainHandler.post(new Runnable() {
            @Override public void run() {
                UpdateHelper.showNoNet(DownloadService.this, bean);
            }
        });
    }

    private void showNetType(final CheckBean bean) {
        mMainHandler.post(new Runnable() {
            @Override public void run() {
                UpdateHelper.showNetType(DownloadService.this, bean);
            }
        });
    }

    //安装apk
    private void installApk(final File apk) {
        mMainHandler.post(new Runnable() {
            @Override public void run() {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(apk), "application/vnd.android.package-archive");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mNotify.updateFinish(apk);
                UpdateHelper.finishLoad(DownloadService.this, apk);
            }
        });
    }

    //下载保存路径
    private File getDownloadDir() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            return new File(Environment.getExternalStorageDirectory(), "Download");
        }
        return getDir("app", MODE_WORLD_READABLE);
    }

    //检查是否己下载过
    private boolean checkExist(CheckBean bean, File apk) {
        if (!apk.exists())
            return false;
        PackageManager pm = getPackageManager();
        Utils.i("apk path " + apk.getPath());
        PackageInfo info = pm.getPackageArchiveInfo(apk.getPath(), PackageManager.GET_CONFIGURATIONS);
        if (info == null)
            return false;
        Utils.i("packageName:" + info.packageName + ",code:" + info.versionCode + ",name:" + info.versionName);
        boolean exist = info.versionName.equals(bean.versionName) && info.versionCode == bean.versionCode;
        if(!exist) apk.delete();
        return exist;
    }

    protected void handleMessage(Message msg) {
        int taskId = msg.what;
        Intent intent = (Intent) msg.obj;
        if (intent == null) {
            stopTask(taskId, 2000);
            return;
        }
        int task = intent.getIntExtra(EXTRA_TASK, -1);
        Utils.i("handleMessage task :" + task + " ,id:" + msg.what);
        if (task < 0) {
            //            stopSelf(taskId);
            //            return;
            throw new IllegalArgumentException("the DownloadService start intent must indicate the task id!!");
        }
        int netType = Utils.getNetConnectType(this);

        if (task == TASK_ID_CHECK) {
            String url = intent.getStringExtra(EXTRA_URL);
            checkVersion(url, intent.getBooleanExtra(EXTRA_HIT_SHOW, false));
        } else {
            mCancelAtomic.set(false);
            final CheckBean bean = intent.getParcelableExtra(EXTRA_URL);
            if (netType < 0) {
                showNetSetting(bean);
                stopTask(taskId, 5000);
                return;
            }
            if (netType != ConnectivityManager.TYPE_WIFI && !bean.downInMoblie) {
                showNetType(bean);
            } else {
                File download = getDownloadDir();
                if (!download.exists())
                    download.mkdirs();
                if (download == null) {
                    showHit(getString(R.string.up_down_store));
                    return;
                }
                File apk = new File(download, Utils.getNumName(bean.loadUrl) + ".apk");
                if (checkExist(bean, apk) || download(bean, apk))
                    installApk(apk);
            }
        }
        stopTask(taskId, 2000);
    }

    //检测升级
    void checkVersion(String url, boolean showHit) {
        if (showHit)
            UpdateHelper.showCheckHit(this);
        int verCode;
        try {
            PackageInfo pi = getPackageManager().getPackageInfo(getPackageName(), 0);
            verCode = pi.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            verCode = 0;
        }
        if (url.contains("?")) {
            if (!url.endsWith("&"))
                url += "&";
        } else {
            url += "?";
        }
        url += "packageName=" + getPackageName() + "&versionCode=" + verCode;
        URL u;
        try {
            u = new URL(url);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("checkVersion faild,URL invalid " + url);
        }
        HttpURLConnection conn;
        try {
            conn = (HttpURLConnection) u.openConnection();
        } catch (IOException e) {
            Log.e(TAG, "checkVersion openConnection error " + e.getMessage());
            if (showHit && !mCancelAtomic.get()) {
                showHit(getString(R.string.up_check_error));
            }
            return;
        }
        conn.setConnectTimeout(10000);
        if (mCancelAtomic.get())
            return;
        String rs;
        try {
            //FIXME 判断是否请求成功
            if (conn.getResponseCode() != 200) {
                conn.disconnect();
                if (mCancelAtomic.get())
                    return;
                if (showHit) {
                    showHit(getString(R.string.up_check_error));
                }
                return;
            }
            String encoding = conn.getContentEncoding();
            if (encoding == null)
                encoding = "utf-8";
            rs = Utils.isToStr(conn.getInputStream(), encoding);
        } catch (IOException e) {
            Log.e(TAG, "checkVersion read the response error ", e);
            if (showHit && !mCancelAtomic.get()) {
                showHit(getString(R.string.up_check_error));
            }
            return;
        }
        if (mCancelAtomic.get())
            return;
        final CheckBean cb = parseFromString(rs);
        if (cb != null && cb.isNeedUpdate()) {
            mMainHandler.post(new Runnable() {
                @Override public void run() {
                    UpdateHelper.showUpdateLog(DownloadService.this, cb);
                }
            });
        } else if (showHit) {
            showHit(getString(R.string.up_check_news));
        }
    }

    //下载
    boolean download(CheckBean bean, File apk) {
        URL u;
        try {
            u = new URL(bean.loadUrl);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("checkVersion faild,URL invalid " + bean.loadUrl);
        }
        HttpURLConnection conn;
        try {
            conn = (HttpURLConnection) u.openConnection();
        } catch (IOException e) {
            Log.e(TAG, "checkVersion openConnection error " + e.getMessage());
            showHit(getString(R.string.up_check_error));
            return false;
        }
        String rs;
        try {
            //FIXME 判断是否请求成功
            mNotify = new NotifyProgress(this,getLogoId());
            mNotify.show();
            long pos = 0;
            RandomAccessFile file = new RandomAccessFile(apk,"rwd");
            if(apk.exists()){
                pos = apk.length();
            }
            if(pos > 0){
                file.seek(pos);
                conn.setRequestProperty("Range", pos + "-");
                Utils.i("Range "+pos);
            }
            if (conn.getResponseCode() < 200 || conn.getResponseCode() >= 300) {
                conn.disconnect();
                mNotify.dismiss();
                mNotify.updateRetry(bean);
//                showHit(getString(R.string.up_down_error));
                return false;
            }
            int len = conn.getContentLength();
            InputStream is = conn.getInputStream();
            byte[] buffers = new byte[1024 * 5];
            int idx, count = (int) pos;
            long start = SystemClock.elapsedRealtime();
            long current;
            while ((idx = is.read(buffers)) != -1) {
                file.write(buffers, 0, idx);
                count += idx;
                current = SystemClock.elapsedRealtime();
                if (current - start > 500) {
                    start = current;
                    mNotify.updateProgress(count * 100 / len);
                }
            }
            Utils.close(is);
            Utils.close(file);
        } catch (IOException e) {
            Log.i(TAG, "checkVersion read the response error " + e.getMessage());
            mNotify.updateRetry(bean);
            return false;
        }
        return true;
    }

    int getLogoId(){
        String[] arrays = {"logo","ic_launcher"};
        String pkg = getPackageName();
        for(String s:arrays) {
            int id = getResources().getIdentifier(s, "drawable", pkg);
            if(id > 0)
                return id;
        }
        return android.R.drawable.stat_sys_download;
    }

    CheckBean parseFromString(String rs) {
        JSONObject json;
        try {
            json = new JSONObject(rs);
        } catch (JSONException e) {
            Log.e(TAG, "checkVersion parser json error ", e);
            return null;
        }
        return new CheckBean(json);
    }
}
