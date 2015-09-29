package com.llflib.cm.ui;

import android.app.Application;

import com.llflib.cm.util.FilePath;

import timber.log.Timber;

/**
 * Created by llf on 2015/4/22.
 */
public class BaseApp extends Application {
    static BaseApp INSTANCE;

    public static BaseApp getContext() {
        return INSTANCE;
    }

    @Override public void onCreate() {
        super.onCreate();
        INSTANCE = this;

        FilePath.get().init(this, "LLF");
        Timber.plant(new Timber.DebugTree());
        //        if(BuildConfig.DEBUG){
        //        }else{
        //            CrashHandler.install(this);
        //            Timber.plant(new ReleaseTree());
        //        }
    }
}
