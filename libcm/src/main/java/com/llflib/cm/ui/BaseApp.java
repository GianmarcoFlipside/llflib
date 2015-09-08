package com.llflib.cm.ui;

import android.app.Application;

import com.llflib.cm.BuildConfig;
import com.llflib.cm.util.CrashHandler;
import com.llflib.cm.util.FilePath;

import java.sql.Time;

import timber.log.ReleaseTree;
import timber.log.Timber;

/**
 * Created by llf on 2015/4/22.
 */
public class BaseApp extends Application{
    static BaseApp INSTANCE;
    public static BaseApp getContext(){
        return INSTANCE;
    }

    @Override public void onCreate() {
        super.onCreate();
        INSTANCE = this;

        FilePath.get().init(this,"LLF");
        if(BuildConfig.DEBUG){
            Timber.plant(new Timber.DebugTree());
        }else{
//            CrashHandler.install(this);
            Timber.plant(new ReleaseTree());
        }
    }
}
