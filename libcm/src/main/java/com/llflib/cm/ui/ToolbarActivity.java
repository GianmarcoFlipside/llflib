package com.llflib.cm.ui;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.widget.FrameLayout;

import com.llflib.cm.R;

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

    @Override public void startActivityForResult(Intent intent, int requestCode, Bundle options) {
        super.startActivityForResult(intent, requestCode, options);
        overridePendingTransition(0, 0);
    }

    @Override public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        overridePendingTransition(0, 0);
    }
}
