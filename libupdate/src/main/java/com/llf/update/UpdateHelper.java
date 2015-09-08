package com.llf.update;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.List;

/**
 * Created by llf on 2015/8/10.
 */
public class UpdateHelper {

    /**
     * 检测升级
     *
     * @param ctx
     * @param url 检测升级的地址
     * @see {@link #checkUpdate(Context, String, boolean)}
     */
    public static void checkUpdate(Context ctx, String url) {
        checkUpdate(ctx, url, false);
    }

    /**
     * 检测升级
     *
     * @param ctx
     * @param url        检测升级的地址
     * @param showDialog 是否显示检测升级的提示
     * @see {@link #checkUpdate(Context, String)}
     */
    public static void checkUpdate(Context ctx, String url, boolean showDialog) {
        Intent intent = new Intent(ctx, DownloadService.class);
        intent.putExtra(DownloadService.EXTRA_URL, url);
        intent.putExtra(DownloadService.EXTRA_HIT_SHOW, showDialog);
        intent.putExtra(DownloadService.EXTRA_TASK,DownloadService.TASK_ID_CHECK);
        ctx.startService(intent);
    }

    /**
     * 下载更新
     */
    static void downloadUpdate(Context ctx, CharSequence url) {
        Intent intent = new Intent(ctx, DownloadService.class);
        intent.putExtra(DownloadService.EXTRA_URL, url);
        intent.putExtra(DownloadService.EXTRA_TASK,DownloadService.TASK_ID_DOWN);
        ctx.startService(intent);
    }

    /**
     * 显示检测升级提示
     */
    public static void showCheckHit(Context ctx) {
        Intent intent = new Intent(ctx, UpdateHitActivity.class);
        intent.putExtra(UpdateHitActivity.EXTRA_MODE_TYPE, UpdateHitActivity.MODE_CHECK_HIT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(intent);
    }

    /**
     * 显示升级内容
     */
    public static void showUpdateLog(Context ctx, String title, String message, String url) {
        Intent intent = new Intent(ctx, UpdateHitActivity.class);
        intent.putExtra(UpdateHitActivity.EXTRA_MODE_TYPE, UpdateHitActivity.MODE_UPDATE_LOG);
        intent.putExtra(UpdateHitActivity.EXTRA_HIT_TITLE, title);
        intent.putExtra(UpdateHitActivity.EXTRA_HIT_MESSAGE, message);
        intent.putExtra(DownloadService.EXTRA_URL, url);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(intent);
    }

    /**
     * 隐藏提示
     */
    public static void hideHitIfNeed(Context ctx) {
        ComponentName cn = new ComponentName(ctx,UpdateHitActivity.class);
        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
        if(!cn.equals(list.get(0).topActivity)) return;
        Intent intent = new Intent();
        intent.setComponent(cn);
        intent.putExtra(UpdateHitActivity.EXTRA_MODE_TYPE, UpdateHitActivity.MODE_FINISH);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(intent);
    }
    /**
     * 联网过程中的提示
     * */
    static void showCheckMessage(Context ctx, CharSequence sequence) {
        Toast.makeText(ctx, sequence, Toast.LENGTH_SHORT).show();
    }

    /**
     * 下载更新包完成后的安装操作
     * */
    static void finishLoad(Context ctx, File apk) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(apk), "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(intent);
    }

    static void updateDownloadState(Context ctx ,int total,int progress){

    }
}
