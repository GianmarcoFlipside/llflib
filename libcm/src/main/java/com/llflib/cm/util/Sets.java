package com.llflib.cm.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.llflib.cm.R;

import java.io.File;

/**
 * Created by llf on 2015/7/10.
 */
public class Sets {
    public static void clearCache(Context ctx){
        clearCache(ctx,new Handler(Looper.getMainLooper()));
    }

    public static void clearCache(final Context ctx,final Handler h){
        final ProgressDialog pd = new ProgressDialog(ctx);
        pd.setMessage(ctx.getString(R.string.llf_state_wait));
        pd.show();
        new Thread(){
            @Override public void run() {
                File cache = ctx.getCacheDir();
                Files.deleteFile(cache);
                h.post(new Runnable() {
                    @Override public void run() {
                        pd.dismiss();
                    }
                });
            }
        }.start();
    }

}
