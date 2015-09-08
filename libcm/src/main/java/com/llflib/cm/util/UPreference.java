package com.llflib.cm.util;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.llflib.cm.ui.BaseApp;

/**
 * Created by llf on 2015/7/15.
 */
public class UPreference {
    private static UPreference INSTANCE;
    public static UPreference get(){
        if(INSTANCE == null)
            INSTANCE = new UPreference();
        return INSTANCE;
    }

    SharedPreferences mPreference;
    UPreference(){
        mPreference = PreferenceManager.getDefaultSharedPreferences(BaseApp.getContext());
    }

    public void putBoolean(String key,boolean value){
        mPreference.edit().putBoolean(key,value).commit();
    }

    public boolean getBoolean(String key){
        return mPreference.getBoolean(key,false);
    }
}
