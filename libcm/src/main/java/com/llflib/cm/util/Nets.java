package com.llflib.cm.util;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.llflib.cm.R;
import com.llflib.cm.net.AbstractNet;
import com.llflib.cm.ui.ToolbarActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import timber.log.Timber;

/**
 * Created by 908397 on 2015/2/4.
 * 网络相关
 */
public class Nets {
    /**
     * 获取当前移动设备网络连接类型
     **/
    public static int getNetConnectType(Context ctx) {
        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null || !ni.isAvailable() || !ni.isConnected()) {
            return -1;
        }
        return ni.getType();
    }

    /**
     * 判断当前网络是否为WIFI
     */
    public static boolean isWifiConnect(Context ctx) {
        return ConnectivityManager.TYPE_WIFI == getNetConnectType(ctx);
    }

    /**
     * 跳转到WIFI设置界面
     */
    public static void startNetworkSettings(Context ctx) {
        Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(intent);
    }

    /**
     * 处理错误提示
     */
    public static void processNetErrors(ToolbarActivity ctx, Throwable e) {
        if (e instanceof AbstractNet.CMNetException) {
            ctx.showHit(e.getMessage());
        } else if (e instanceof ConnectException) {
            ctx.showNetHit();
        } else {
            ctx.showHit(ctx.getString(R.string.cm_net_failed));
        }
    }

    /**
     * 处理错误提示,activity必须继承{@link com.llflib.cm.ui.ToolbarActivity}
     */
    public static void processNetErrors(Fragment fragment, Throwable e) {
        if (fragment.isDetached())
            return;
        if (!(fragment.getActivity() instanceof ToolbarActivity))
            throw new IllegalArgumentException("The fragment attach Activity isn't ToolbarActivity");
        processNetErrors((ToolbarActivity) fragment.getActivity(), e);
    }

    /**
     * 下载网络文件到指定位置
     *
     * @param url      网络地址
     * @param saveFile 要保存的文件位置
     * @return 若成功，返回ture 其它返回false
     **/
    public static boolean loadFile(String url, File saveFile) {
        if (TextUtils.isEmpty(url))
            return false;
        URL u = null;
        try {
            u = new URL(url);
        } catch (MalformedURLException e) {
            Timber.i("loadFile error,msg " + e.getMessage());
            return false;
        }

        try {
            InputStream is = u.openStream();
            FileOutputStream fos = new FileOutputStream(saveFile);
            int idx = 0;
            byte[] buf = new byte[1024];
            while ((idx = is.read(buf)) > 0) {
                fos.write(buf, 0, idx);
            }
            fos.flush();
            fos.close();
            is.close();
        } catch (IOException e) {
            Timber.i("loadFile save error,msg " + e.getMessage());
            return false;
        }
        Timber.v("load file sucess,path " + saveFile.getAbsolutePath());
        return true;
    }

    public static String appendArgs(String... args) {
        if (args == null)
            return null;
        boolean hasUrl = args[0].contains("http");
        int size = args.length;
        if (hasUrl ? size % 2 == 0 : size % 2 == 1)
            throw new IllegalArgumentException("the args isn't key-value");
        StringBuffer sb = new StringBuffer(args[0]);
        if (hasUrl) {
            if (!args[0].contains("?")) {
                sb.append("?");
            } else {
                if (!args[0].endsWith("?") && !args[0].endsWith("&"))
                    sb.append("&");
            }
        }
        int start = hasUrl ? 1 : 0;
        for (int i = start; i < size; i += 2) {
            if (i != start)
                sb.append("&");
            sb.append(args[i]).append("=");
            try {
                sb.append(URLEncoder.encode(args[i + 1], "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                //ignore
            }
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        appendArgs("sas", "asas", null);
    }
}
