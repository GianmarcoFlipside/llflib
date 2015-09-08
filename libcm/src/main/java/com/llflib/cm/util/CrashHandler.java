package com.llflib.cm.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.*;
import android.os.Process;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;

import timber.log.Timber;

/**
 * Created by 908397 on 2015/1/30.
 * 错误信息收集类
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {
    final static String TAG = "CrashHandler";

    public static void install(Context ctx) {
        if (ctx == null)
            throw new NullPointerException("the ctx can't be null!");
        Thread.UncaughtExceptionHandler defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        if (defaultHandler != null && defaultHandler.getClass().getName().equals(CrashHandler.class.getName())) {
            Timber.w("Crash handler already setup");
            return;
        }
        Thread.setDefaultUncaughtExceptionHandler(new CrashHandler(ctx));
    }

    private Context mCtx;
    private Thread.UncaughtExceptionHandler mDefaultHandler;

    public CrashHandler(Context ctx) {
        mCtx = ctx.getApplicationContext();
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
    }

    @Override public void uncaughtException(Thread thread, final Throwable ex) {
        //        new Thread(){
        //            @Override
        //            public void run() {
        uiErrorHit();
        StringBuffer sb = new StringBuffer();
        collectPhoneInfo(sb, mCtx);
        collectSoftInfo(sb, mCtx);
        collectException(sb, ex);
        dealErrorInfo(sb.toString());
        Timber.d("Crash finish");
        killCurrentProcess();
        //            }
        //        }.start();

        //        if(mDefaultHandler != null)
        //            mDefaultHandler.uncaughtException(thread,ex);
    }

    //收集手机信息
    private void collectPhoneInfo(StringBuffer sb, Context ctx) {
        //设备模型
        sb.append("\n\nModel:").append(Build.MODEL);
        //设备品牌
        sb.append("\nBrand:").append(Build.BRAND);
        //产品信息
        sb.append("\nProduct:").append(Build.PRODUCT);
        //系统版本
        sb.append("\nSystem Version:").append(Build.VERSION.RELEASE);
        //编译版本
        sb.append("\nSystem Build Version:").append(Build.FINGERPRINT);
    }

    //收集软件相关信息
    private void collectSoftInfo(StringBuffer sb, Context ctx) {
        sb.append("\n\n");
        PackageManager pm = ctx.getPackageManager();
        sb.append("Application:").append(pm.getApplicationLabel(ctx.getApplicationInfo()));
        try {
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), 0);
            sb.append("\nversionName:").append(pi.versionName);
            sb.append("\nversionCode:").append(pi.versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            Timber.e(e, "collectSoftInfo");
        }
        sb.append("\n\n");
    }

    //收集错误信息
    private void collectException(StringBuffer sb, Throwable e) {
        sb.append("\n\n");
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        sb.append(sw.getBuffer());
        sb.append("\n\n");
    }

    //错误提示
    private void uiErrorHit() {
        Timber.i("current thread %s", Thread.currentThread().getName());
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override public void run() {
                Toast.makeText(mCtx, "Application Crash", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //错误信息处理
    private void dealErrorInfo(String error) {
        File parent = FilePath.get().getSubPath("log/crash");
        if (parent == null) {
            Timber.w("crash log can't saved,because of not found sdcard");
            return;
        }
        File log = new File(parent, System.currentTimeMillis() + ".log");
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(log), "UTF-8"));
            bw.write(error);
            bw.close();
        } catch (Exception e) {
            Timber.e(e, "dealErrorInfo");
        }
    }

    private void killCurrentProcess() {
        android.os.Process.killProcess(Process.myPid());
        System.exit(10);
    }
}
