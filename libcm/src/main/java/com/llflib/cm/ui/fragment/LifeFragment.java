package com.llflib.cm.ui.fragment;


import android.support.v4.app.Fragment;

import com.llflib.cm.net.ILifeListener;

import java.util.ArrayList;

/**
 * Created by llf on 2015/7/17.
 */
public abstract class LifeFragment extends Fragment {
    private boolean mStarted,mDestroyed;
    private ArrayList<ILifeListener> mLifeListener;

    public LifeFragment(){
        mLifeListener = new ArrayList<>();
    }

    protected void loadData(int page,boolean fore){
    }

    protected void onErrorShow(boolean full){

    }

    protected void onEmptyShow(boolean full){
    }

    public void addLifeListener(ILifeListener l){
        mLifeListener.add(l);
        if(mStarted){
            l.onStart();
        }else if(mDestroyed){
            l.onDestroy();
        }else{
            l.onStop();
        }
    }

    public void removeLifeListener(ILifeListener l){
        mLifeListener.remove(l);
    }

    @Override public void onStart() {
        super.onStart();
        mStarted = true;
        for(ILifeListener i:mLifeListener){
            i.onStart();
        }
    }

    @Override public void onStop() {
        super.onStop();
        mStarted = false;
        for(ILifeListener i:mLifeListener){
            i.onStop();
        }
    }

    @Override public void onDestroy() {
        super.onDestroy();
        mDestroyed = true;
        for(ILifeListener i:mLifeListener){
            i.onDestroy();
        }
    }
}
