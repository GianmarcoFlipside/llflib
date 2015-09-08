package com;

import android.app.Application;

import com.util.FilePath;

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
        FilePath.get().init(this,"kanxue");
    }
}
