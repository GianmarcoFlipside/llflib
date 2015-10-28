package com.llf.update.progress;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.SystemClock;
import android.widget.RemoteViews;

import com.llf.update.CheckBean;
import com.llf.update.DownloadService;
import com.llf.update.R;

import java.io.File;

/**
 * Created by llf on 2015/10/28.
 *
 * @email llfer2006@gmail.com
 */
public class NotifyProgress implements IProgress {
    static final int PENDING_REQUEST_CANCEL = 0x1;
    static final int PENDING_REQUEST_RETRY = 0x2;
    static final int PENDING_REQUEST_INSTALL = 0x3;
    private int mId;
    private Context mCtx;

    private Notification mNotification;

    public NotifyProgress(Context ctx, int rid) {
        mCtx = ctx;
        mId = rid;
    }

    @Override public void show() {
        if (mNotification != null)
            throw new IllegalStateException("This method is called once");
        CharSequence cs = mCtx.getString(R.string.up_down_trick);
        mNotification = new Notification(android.R.drawable.stat_sys_download, cs, SystemClock.elapsedRealtime());
        //XXX cacel?
        mNotification.contentIntent = PendingIntent.getBroadcast(mCtx, PENDING_REQUEST_CANCEL, new Intent(
                Intent.ACTION_VIEW), PendingIntent.FLAG_UPDATE_CURRENT);
        mNotification.contentView = new RemoteViews(mCtx.getPackageName(), R.layout.up_notify_progress);
        mNotification.contentView.setTextViewText(android.R.id.title, cs);
        mNotification.contentView.setImageViewResource(android.R.id.icon, mId);
        mNotification.contentView.setProgressBar(android.R.id.progress, 100, 30, true);
        mNotification.flags = Notification.FLAG_AUTO_CANCEL;
        notify(mNotification);
    }

    @Override public void updateProgress(int progress) {
        mNotification.contentView.setProgressBar(android.R.id.progress, 100, progress, progress <= 0);
        mNotification.flags = Notification.FLAG_ONGOING_EVENT;
        notify(mNotification);
    }

    @Override public void updateRetry(CheckBean bean) {
        mNotification.contentView.setTextViewText(android.R.id.title, mCtx.getString(R.string.up_down_retry));
        Intent intent = new Intent(mCtx, DownloadService.class);
        intent.putExtra(DownloadService.EXTRA_TASK, DownloadService.TASK_ID_DOWN);
        intent.putExtra(DownloadService.EXTRA_URL, bean);
        mNotification.contentIntent = PendingIntent.getService(mCtx, PENDING_REQUEST_RETRY, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        mNotification.flags = Notification.FLAG_AUTO_CANCEL;
        notify(mNotification);
    }

    @Override public void updateFinish(File apk) {
        mNotification.contentView.setProgressBar(android.R.id.progress, 100, 100, false);
        mNotification.contentView.setTextViewText(android.R.id.title, mCtx.getString(R.string.up_down_finish));
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(apk), "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mNotification.contentIntent = PendingIntent.getActivity(mCtx, PENDING_REQUEST_INSTALL, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        mNotification.flags = Notification.FLAG_AUTO_CANCEL;
        notify(mNotification);
    }

    @Override public void dismiss() {
        if (mNotification == null)
            throw new IllegalStateException("This method is called once");
        ((NotificationManager) mCtx.getSystemService(Context.NOTIFICATION_SERVICE)).cancel(
                android.R.drawable.stat_sys_download);

    }

    private void notify(Notification n) {
        ((NotificationManager) mCtx.getSystemService(Context.NOTIFICATION_SERVICE)).notify(
                android.R.drawable.stat_sys_download, n);
    }
}
