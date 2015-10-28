package com.llf.update;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.llf.update.util.Utils;

/**
 * Created by llf on 2015/8/10.
 */
public class UpdateHitActivity extends Activity implements View.OnClickListener {
    static final int MODE_CHECK_LOADING = 0x0;
    static final int MODE_UPDATE_LOG = 0x1;
    static final int MODE_NET_SETTING = 0x2;
    static final int MODE_NET_TYPE = 0x3;
    static final int MODE_DOWN_PROGRESS = 0x4;
    static final int MODE_FINISH = 0x5;

    static final String EXTRA_MODE_TYPE = "extra_mode_id";

    static final String EXTRA_HIT_PROGRESS = "extra_hit_progress";
    static final String EXTRA_HIT_TOTAL = "extra_hit_total";

    static final int REQUEST_NET_SETTING = 1;

    private int mShowMode;
    private CheckBean mBean;

    @Override public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setModeFromIntent(getIntent());
        Log.e("LLF", "UpdateHitActivity onCreate ");
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
        int mode = intent.getIntExtra(EXTRA_MODE_TYPE, -1);
        if (mode < 0 || mode >= MODE_DOWN_PROGRESS) {
            finish();
            return;
        }
        switch (mode) {
            case MODE_CHECK_LOADING:
                setupCheckHitViews(intent);
                break;
            case MODE_UPDATE_LOG:
                setupUpdateLogViews(intent);
                break;
            case MODE_DOWN_PROGRESS:
                setupProgressViews(intent);
                break;
            case MODE_NET_SETTING:
                setupNetSettings(intent);
                break;
            case MODE_NET_TYPE:
                setupNetType(intent);
                break;
            default:
                mShowMode = MODE_FINISH;
                finish();
                break;
        }
    }

    void setupCheckHitViews(Intent intent) {
        mShowMode = MODE_CHECK_LOADING;
        setContentView(R.layout.up_progress);
    }

    void setupUpdateLogViews(Intent intent) {
        mShowMode = MODE_UPDATE_LOG;
        setContentView(R.layout.up_log);
        mBean = intent.getParcelableExtra(DownloadService.EXTRA_URL);
        setDialogMessage(mBean.title, mBean.message);
    }

    void setupProgressViews(Intent intent) {
        mShowMode = MODE_DOWN_PROGRESS;
    }

    void setupNetSettings(Intent intent) {
        mShowMode = MODE_NET_SETTING;
        setContentView(R.layout.up_log);
        mBean = intent.getParcelableExtra(DownloadService.EXTRA_URL);
        setDialogMessage(getString(R.string.up_net_hit), getString(R.string.up_net_nothing));
        ((TextView) findViewById(android.R.id.button1)).setText(R.string.up_btn_setting);
    }

    void setupNetType(Intent intent) {
        mShowMode = MODE_NET_TYPE;
        setContentView(R.layout.up_log);
        mBean = intent.getParcelableExtra(DownloadService.EXTRA_URL);
        setDialogMessage(getString(R.string.up_net_hit), getString(R.string.up_net_mobile));
    }

    void setDialogMessage(String title, String msg) {
        TextView tv = (TextView) findViewById(android.R.id.title);
        tv.setText(title);
        tv = (TextView) findViewById(android.R.id.message);
        tv.setText(msg);
        findViewById(android.R.id.button1).setOnClickListener(this);
        findViewById(android.R.id.button2).setOnClickListener(this);
    }

    @Override public void onClick(View v) {
        if (v.getId() == android.R.id.button1) {
            switch (mShowMode) {
                case MODE_NET_TYPE:
                    mBean.downInMoblie = true;
                case MODE_UPDATE_LOG:
                    UpdateHelper.downloadUpdate(this, mBean);
                    finish();
                    break;
                case MODE_NET_SETTING:
                    Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivityForResult(intent, REQUEST_NET_SETTING);
                    break;
            }
        }
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Utils.i("Activity onActivityResult requestCode:" + requestCode + ",resultCode:" + resultCode);
        if (requestCode == REQUEST_NET_SETTING) {
            if (mBean != null && Utils.getNetConnectType(this) >= 0) {
                UpdateHelper.downloadUpdate(this, mBean);
                finish();
            }
        } else
            super.onActivityResult(requestCode, resultCode, data);
    }

    @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mShowMode == MODE_CHECK_LOADING) {
                UpdateHelper.cancelCheckUpdate(this);
            }
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
