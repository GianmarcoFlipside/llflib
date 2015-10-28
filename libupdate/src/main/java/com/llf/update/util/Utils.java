package com.llf.update.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by llf on 2015/10/27.
 *
 * @email llfer2006@gmail.com
 */
public class Utils {
    static final String TAG = "ULOG";

    public static void i(String msg) {
        i(TAG, msg);
    }

    public static void i(String TAG, String msg) {
        Log.i(TAG, msg);
    }

    public static void w(String TAG, String msg) {
        Log.w(TAG, msg);
    }

    public static void w(String TAG, String msg, Throwable e) {
        Log.w(TAG, msg, e);
    }

    public static int getNetConnectType(Context ctx) {
        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null || !ni.isAvailable() || !ni.isConnected()) {
            return -1;
        }
        return ni.getType();
    }

    public static String getNumName(String str) {
        if (str == null || str.length() == 0)
            return str;
        return Integer.toHexString(str.hashCode());
    }

    public static String isToStr(InputStream is, String encoding) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024 * 4];
        int idx;
        while ((idx = is.read(buffer)) > 0) {
            bos.write(buffer, 0, idx);
        }
        is.close();
        bos.flush();
        buffer = bos.toByteArray();
        bos.close();

        return new String(buffer, encoding);
    }

    public static void close(Closeable cb){
        if(cb != null)
            try {
                cb.close();
            } catch (IOException e) {
            }
    }
}
