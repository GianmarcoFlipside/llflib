package com.llf.update;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.llflib.cm.util.FilePath;
import com.llflib.cm.util.Files;
import com.llflib.cm.util.ILog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by llf on 2015/8/10.
 */
public class DownloadService extends Service {
    static final String TAG = "DownloadService";
    static final int TASK_ID_CHECK = 0x0;
    static final int TASK_ID_DOWN = 0x1;

    static final String EXTRA_TASK = "extral_task";
    static final String EXTRA_URL = "extral_url";
    static final String EXTRA_HIT_SHOW = "extra_hit_show";

    private volatile ServiceHandler mServiceHandler;

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

        mServiceHandler = new ServiceHandler(thread.getLooper());
        ILog.i("Services onCreate.....");
    }

    @Override public void onStart(Intent intent, int startId) {
        mServiceHandler.obtainMessage(startId, intent).sendToTarget();
    }

    @Override public int onStartCommand(Intent intent, int flags, int startId) {
        mServiceHandler.obtainMessage(startId, intent).sendToTarget();
        return START_REDELIVER_INTENT;
    }

    @Override public void onDestroy() {
        super.onDestroy();
        mServiceHandler.getLooper().quit();
    }

    @Override public IBinder onBind(Intent intent) {
        return null;
    }

    protected void handleMessage(Message msg) {
        int taskId = msg.what;
        Intent intent = (Intent) msg.obj;
        if (intent == null) {
            stopSelf(taskId);
            return;
        }
        int task = intent.getIntExtra(EXTRA_TASK, -1);
        String url = intent.getStringExtra(EXTRA_URL);
        if (task < 0 || TextUtils.isEmpty(url)) {
            //            stopSelf(taskId);
            //            return;
            throw new IllegalArgumentException("the DownloadService start intent must indicate the task id!!");
        }

        if (task == TASK_ID_CHECK) {
            checkVersion(url, intent.getBooleanExtra(EXTRA_HIT_SHOW, false));
        } else {
            download(url);
        }
        stopSelf(taskId);
    }

    //检测升级
    void checkVersion(String url, boolean showHit) {
        if (showHit)
            UpdateHelper.showCheckHit(this);
        int verCode;
        try {
            PackageInfo pi = getPackageManager().getPackageInfo(getPackageName(), -0);
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
            if (showHit) {
                UpdateHelper.showCheckMessage(this, getString(R.string.up_check_error));
                UpdateHelper.hideHitIfNeed(this);
            }
            return;
        }
        String rs;
        try {
            //FIXME 判断是否请求成功
            if (conn.getResponseCode() != 200) {
                conn.disconnect();
                if (showHit) {
                    UpdateHelper.showCheckMessage(this, getString(R.string.up_check_error));
                    UpdateHelper.hideHitIfNeed(this);
                }
                return;
            }
            String encoding = conn.getContentEncoding();
            if (encoding == null)
                encoding = "utf-8";
            rs = Files.isToStr(conn.getInputStream(), encoding);
        } catch (IOException e) {
            Log.e(TAG, "checkVersion read the response error ", e);
            if (showHit) {
                UpdateHelper.showCheckMessage(this, getString(R.string.up_check_error));
                UpdateHelper.hideHitIfNeed(this);
            }
            return;
        }
        CheckBean cb = parseFromString(rs);
        if (cb.isNeedUpdate()) {
            UpdateHelper.showUpdateLog(this, cb.title, cb.message, cb.loadUrl);
        } else if (showHit) {
            UpdateHelper.showCheckMessage(this, getString(R.string.up_check_news));
        }
    }

    //下载
    void download(String url) {
        //FIXME 判断是否有地方存储
        File download = FilePath.get().getDownload();
        if (download == null) {
            UpdateHelper.showCheckMessage(this, getString(R.string.up_down_store));
            return;
        }
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
            UpdateHelper.showCheckMessage(this, getString(R.string.up_down_error));
            return;
        }
        String rs;
        try {
            //FIXME 判断是否请求成功
            notifyProgress(this, -1);
            if (conn.getResponseCode() != 200) {
                conn.disconnect();
                cancelNotification(this);
                UpdateHelper.showCheckMessage(this, getString(R.string.up_down_error));
                return;
            }
            int len = conn.getContentLength();
            InputStream is = conn.getInputStream();
            File apk = new File(download, Files.getNumName(url));
            //FIXME 判断是否己经下载过或部分
            FileOutputStream fos = new FileOutputStream(apk);
            byte[] buffers = new byte[1024 * 5];
            int idx, count = 0;
            long start = SystemClock.elapsedRealtime();
            long current = start;
            while ((idx = is.read(buffers)) != -1) {
                fos.write(buffers, 0, idx);
                count += idx;
                current = SystemClock.elapsedRealtime();
                if (current - start > 500) {
                    start = current;
                    notifyProgress(this, idx * 100 / len);
                }
            }
            is.close();
            fos.flush();
            fos.close();

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(apk), "application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            updateNotificationFinish(this, intent);
            UpdateHelper.finishLoad(this, apk);
        } catch (IOException e) {
            Log.i(TAG, "checkVersion read the response error " + e.getMessage());
            cancelNotification(this);
            UpdateHelper.showCheckMessage(this, getString(R.string.up_down_error));
            return;
        }
    }

    Notification mNotification;

    void notifyProgress(Context ctx, int progress) {
        if (mNotification == null) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx);
            builder.setTicker(getString(R.string.up_down_trick)).setContentTitle(getString(R.string.up_down_trick))
                    .setContentIntent(PendingIntent.getBroadcast(ctx, 1, new Intent(Intent.ACTION_VIEW),
                            PendingIntent.FLAG_UPDATE_CURRENT));
            builder.setProgress(100, 35, progress >= 0).setSmallIcon(android.R.drawable.stat_sys_download);
            mNotification = builder.build();
        } else {
            mNotification.contentView.setProgressBar(android.R.id.progress, 100, progress, progress >= 0);
        }

        mNotification.flags = Notification.FLAG_ONGOING_EVENT;
        ((NotificationManager) ctx.getSystemService(NOTIFICATION_SERVICE)).notify(android.R.drawable.stat_sys_download,
                mNotification);
    }

    void updateNotificationFinish(Context ctx, Intent intent) {
        if (mNotification == null)
            return;
        mNotification.flags = Notification.FLAG_AUTO_CANCEL;
        mNotification.contentView.setProgressBar(android.R.id.progress, 100, 100, false);
        mNotification.contentView.setTextViewText(android.R.id.title, ctx.getString(R.string.up_down_finish));
        mNotification.contentIntent = PendingIntent.getActivity(ctx, 11, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        ((NotificationManager) ctx.getSystemService(NOTIFICATION_SERVICE)).notify(R.drawable.logo, mNotification);
    }

    void cancelNotification(Context ctx) {
        if (mNotification != null) {
            ((NotificationManager) ctx.getSystemService(NOTIFICATION_SERVICE)).cancel(R.drawable.logo);
            mNotification = null;
        }
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


    class CheckBean {
        String title;
        String message;
        String loadUrl;
        int versionCode;
        String versionName;

        public CheckBean(JSONObject json) {
            parse(json);
        }

        public boolean isNeedUpdate() {
            return !TextUtils.isEmpty(loadUrl);
        }

        void parse(JSONObject json) {
            title = json.optString("title");
            message = json.optString("msg");
            loadUrl = json.optString("url");
            versionCode = json.optInt("versionCode");
            versionName = json.optString("versionName");
        }
    }
}
