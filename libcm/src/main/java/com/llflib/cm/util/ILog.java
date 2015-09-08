package com.llflib.cm.util;

import android.util.Log;

import com.llflib.cm.BuildConfig;

/**
 * Created by llf on 2015/4/8.
 * @deprecated class
 * used {@link timber.log.Timber} instead
 */
@Deprecated
public class ILog {

    private static final String TAG = ILog.class.getSimpleName();
    private static final boolean DEBUG = BuildConfig.DEBUG;

    public static void v(String msg) {
        v(TAG, msg);
    }

    public static void v(String tag, String msg) {
        if(!DEBUG) return;
        println(Log.VERBOSE, tag, msg);
    }

    public static void v(String tag, String msg, Throwable e) {
        if(!DEBUG) return;
        println(Log.VERBOSE, tag, msg + "\n" + Log.getStackTraceString(e));
    }

    public static void i(String msg) {
        i(TAG, msg);
    }

    public static void i(String tag, String msg) {
        println(Log.INFO, tag, msg);
    }

    public static void i(String tag, String msg, Throwable e) {
        println(Log.INFO, tag, msg + "\n" + Log.getStackTraceString(e));
    }

    public static void w(String msg) {
        w(TAG, msg);
    }

    public static void w(String tag, String msg) {
        println(Log.WARN, tag, msg);
    }

    public static void w(String tag, String msg, Throwable e) {
        println(Log.WARN, tag, msg + "\n" + Log.getStackTraceString(e));
    }

    public static void e(String msg) {
        e(TAG, msg);
    }

    public static void e(String tag, String msg) {
        println(Log.ERROR, tag, msg);
    }

    public static void e(String tag, String msg, Throwable e) {
        println(Log.ERROR, tag, msg + "\n" + Log.getStackTraceString(e));
    }

    static void println(int level, String tag, String msg) {
        Log.println(level, tag, msg);
    }
}
