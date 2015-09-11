package com.llflib.cm.ui;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.llflib.cm.R;
import com.llflib.cm.ui.view.RevealView;
import com.llflib.cm.util.Views;

import java.util.ArrayList;

/**
 * Created by llf on 2015/7/10.
 */
public abstract class ToolbarActivity extends AppCompatActivity{
    protected boolean mFirstIn;
    private TypedValue mGround = new TypedValue();
    private RevealView mRevealView;
    private ArrayList<View> mHitViews;

    public ToolbarActivity(){
        mHitViews = new ArrayList<>();
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Resources.Theme theme = getTheme();
        theme.resolveAttribute(android.R.attr.colorBackground,mGround,true);
    }

    @Override public void setTheme(int resid) {
        super.setTheme(resid);
        Resources.Theme theme = getTheme();
        theme.resolveAttribute(android.R.attr.colorBackground,mGround,true);
    }

    @Override public void setContentView(final int layoutResID) {
        if(mFirstIn){
            mFirstIn = false;
            FrameLayout fl = (FrameLayout) findViewById(android.R.id.content);
            RevealView rv = new RevealView(this);
//            rv.setPaintColor(mGround.data);
            fl.addView(rv,-1);
            rv.auto(new Runnable() {
                @Override public void run() {
                    superContentView(layoutResID);
                }
            },true);
            mRevealView = rv;
        }else{
            superContentView(layoutResID);
        }
    }

    private void superContentView(int layoutResID){
        super.setContentView(layoutResID);
        View v = findViewById(android.R.id.content);
        if(v!= null) v.setBackgroundColor(mGround.data);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if(toolbar != null){
            setSupportActionBar(toolbar);
        }
        setupViews();
        Views.setApplyWindowInserts(this);
    }

    protected abstract void setupViews();
    protected void close(){
        if(mRevealView == null){
            finish();
            return;
        }
        ViewGroup vg = (ViewGroup) findViewById(android.R.id.content);
        if(vg == null){
            finish();
            return;
        }
        vg.addView(mRevealView);
        mRevealView.auto(new Runnable() {
            @Override public void run() {
               finish();
            }
        },false);
    }

    protected View showHitView(@IdRes int id,@LayoutRes int layoutid){
        return showHitView((ViewGroup) findViewById(id),layoutid);
    }

    protected View showHitView(@IdRes int id,View hitView){
        return showHitView((ViewGroup) findViewById(id),hitView);
    }

    protected View showHitView(ViewGroup parent,@LayoutRes int layoutid){
        View view = LayoutInflater.from(this).inflate(layoutid,parent,false);
        return showHitView(parent,view);
    }

    protected View showHitView(ViewGroup parent,View hitView){
        mHitViews.add(hitView);
        parent.addView(hitView);
        return hitView;
    }

    protected void hideHitView(){
        while(!mHitViews.isEmpty()){
            View view = mHitViews.get(0);
            ViewGroup parent = (ViewGroup) view.getParent();
            if(parent != null)
                parent.removeView(view);
            mHitViews.remove(0);
        }
    }

    @Override public void startActivityForResult(Intent intent, int requestCode, Bundle options) {
        super.startActivityForResult(intent, requestCode, options);
        overridePendingTransition(0,0);
    }

    @Override public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        overridePendingTransition(0,0);
    }
}
