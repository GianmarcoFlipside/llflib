package com.llf.update;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by llf on 2015/8/10.
 */
public class UpdateHitActivity extends Activity implements View.OnClickListener{
    static final int MODE_CHECK_HIT = 0x0;
    static final int MODE_UPDATE_LOG = 0x1;
    static final int MODE_DOWN_PROGRESS = 0x2;
    static final int MODE_FINISH = 0x3;

    static final String EXTRA_MODE_TYPE = "extra_mode_id";

    static final String EXTRA_HIT_TITLE = "extra_hit_title";
    static final String EXTRA_HIT_MESSAGE = "extra_hit_message";
    static final String EXTRA_HIT_PROGRESS = "extra_hit_progress";
    static final String EXTRA_HIT_TOTAL = "extra_hit_total";

    private int mShowMode;
    private CharSequence mUrl;
    @Override public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setModeFromIntent(getIntent());
        Log.e("LLF","UpdateHitActivity onCreate ");
    }

    @Override protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setModeFromIntent(intent);
    }

    void setModeFromIntent(Intent intent) {
        if (intent == null) {
            finish();
            return;
        }
        int mode = intent.getIntExtra(EXTRA_MODE_TYPE,-1);
        if(mode < 0 || mode >= MODE_DOWN_PROGRESS){
            finish();
            return;
        }
        switch(mode){
            case MODE_CHECK_HIT:
                setupCheckHitViews(intent);
                break;
            case MODE_UPDATE_LOG:
                setupUpdateLogViews(intent);
                break;
            case MODE_DOWN_PROGRESS:
                setupProgressViews(intent);
                break;
            default:
                mShowMode = MODE_FINISH;
                finish();
                break;
        }
    }

    void setupCheckHitViews(Intent intent){
        mShowMode = MODE_CHECK_HIT;
        ProgressBar bar = new ProgressBar(this);
        bar.setIndeterminate(true);
        setContentView(bar);
    }

    void setupUpdateLogViews(Intent intent){
        mShowMode = MODE_UPDATE_LOG;
        mUrl = intent.getStringExtra(DownloadService.EXTRA_URL);
        setContentView(R.layout.up_log);
        TextView tv = (TextView) findViewById(android.R.id.title);
        tv.setText(intent.getStringExtra(EXTRA_HIT_TITLE));
        tv = (TextView) findViewById(android.R.id.message);
        tv.setText(intent.getStringExtra(EXTRA_HIT_MESSAGE));
        findViewById(android.R.id.button1).setOnClickListener(this);
        findViewById(android.R.id.button2).setOnClickListener(this);
    }

    void setupProgressViews(Intent intent){
        mShowMode = MODE_DOWN_PROGRESS;

    }

    @Override public void onClick(View v) {
        if(v.getId() == android.R.id.button1){
            UpdateHelper.downloadUpdate(this, mUrl);
        }
        finish();
    }
}
