package com.llflib.cm.net;

import android.os.AsyncTask;

/**
 * Created by llf on 2015/7/17.
 */
public abstract class LifeTask<Progress, Result> extends AsyncTask<String, Progress, Result> implements
        ILifeListener {

    @Override public void onStart() {
        execute((String)null);
    }

    @Override public void onStop() {
    }

    @Override public void onDestroy() {
        cancel(true);
    }


}
