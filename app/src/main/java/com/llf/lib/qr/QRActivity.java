package com.llf.lib.qr;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.zxing.client.android.CaptureActivity;
import com.llf.lib.R;
import com.llflib.cm.ui.ToolbarActivity;

public class QRActivity extends ToolbarActivity {

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);
    }

    @Override protected void setupViews() {
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                startActivity(new Intent(QRActivity.this,CaptureActivity.class));
            }
        });
    }
}
