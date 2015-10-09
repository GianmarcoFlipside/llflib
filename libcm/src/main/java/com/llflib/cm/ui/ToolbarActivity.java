package com.llflib.cm.ui;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;

import com.llflib.cm.R;
import com.llflib.cm.util.Nets;

/**
 * Created by llf on 2015/7/10.
 */
public abstract class ToolbarActivity extends AppCompatActivity {
    private TypedValue mGround = new TypedValue();

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Resources.Theme theme = getTheme();
        theme.resolveAttribute(android.R.attr.colorBackground, mGround, true);
    }

    @Override public void setTheme(int resid) {
        super.setTheme(resid);
        Resources.Theme theme = getTheme();
        theme.resolveAttribute(android.R.attr.colorBackground, mGround, true);
    }

    @Override public void setContentView(final int layoutResID) {
        super.setContentView(layoutResID);
        FrameLayout fl = (FrameLayout) findViewById(android.R.id.content);
        if (fl != null) {
            fl.setForeground(new ColorDrawable(Color.RED));
            fl.setBackgroundColor(mGround.data);
        }
        setupActionBarIfExist();
        setupViews();
    }

    protected void setupActionBarIfExist() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
    }

    protected abstract void setupViews();

    protected <X> void onPreLoading(X x) {

    }

    protected <X> void onPostLoading(X x) {
    }

    public void showHit(String msg) {
        if (TextUtils.isEmpty(msg))
            return;
        Snackbar.make(findViewById(Window.ID_ANDROID_CONTENT), msg, Snackbar.LENGTH_SHORT).show();
    }

    public void showNetHit(){
        Snackbar bar = Snackbar.make(findViewById(Window.ID_ANDROID_CONTENT),getString(R.string.cm_net_hit), Snackbar.LENGTH_LONG);
        bar.setAction(R.string.cm_net_set, new View.OnClickListener() {
            @Override public void onClick(View v) {
                Nets.startNetworkSettings(ToolbarActivity.this);
            }
        });
        bar.show();
    }

    @Override public void startActivityForResult(Intent intent, int requestCode, Bundle options) {
        super.startActivityForResult(intent, requestCode, options);
        overridePendingTransition(0, 0);
    }

    @Override public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        overridePendingTransition(0, 0);
    }
}
