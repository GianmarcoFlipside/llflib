package com.llflib.cm.ui.pullview;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.llflib.cm.R;
import com.llflib.cm.util.Views;

/**
 * Created by llf on 2015/8/31.
 */
public class PullHeaderView extends RelativeLayout implements IPull{
    private TextView mHitTv,mTimeTv;
    private ImageView mHitIv;
    private ProgressBar mBar;
    public PullHeaderView(Context context) {
        this(context, null);
    }

    public PullHeaderView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public PullHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setupViews(context);
    }

    private void setupViews(Context ctx){
        TextView tv = new TextView(ctx, null, android.R.attr.textAppearanceMedium);
        tv.setId(android.R.id.text1);
        LayoutParams lp = new LayoutParams(-2, -2);
        lp.addRule(CENTER_HORIZONTAL);
        lp.addRule(ALIGN_PARENT_TOP);
        lp.topMargin = Views.getDimenPx(ctx, 12);
        addView(tv, lp);
        mHitTv = tv;

        tv = new TextView(ctx, null, android.R.attr.textAppearanceSmall);
        lp = new LayoutParams(-2, -2);
        lp.addRule(ALIGN_LEFT, android.R.id.text1);
        lp.addRule(BELOW, android.R.id.text1);
        lp.topMargin = Views.getDimenPx(ctx, 8);
        lp.bottomMargin = Views.getDimenPx(ctx, 12);
        addView(tv, lp);
        mTimeTv = tv;

        ImageView iv = new ImageView(ctx);
        iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
        iv.setBackgroundResource(0);
        lp = new LayoutParams(-2, -2);
        lp.leftMargin = Views.getDimenPx(ctx,12);
        lp.addRule(ALIGN_PARENT_LEFT);
        lp.addRule(CENTER_VERTICAL);
        addView(iv, lp);
        mHitIv = iv;

        ProgressBar bar = new ProgressBar(ctx);
        bar.setIndeterminate(true);
        lp = new LayoutParams(-2, -2);
        lp.leftMargin = Views.getDimenPx(ctx,12);
        lp.addRule(ALIGN_PARENT_LEFT);
        lp.addRule(CENTER_VERTICAL);
        addView(bar, lp);
        mBar = bar;

        ViewGroup.LayoutParams vp = new ViewGroup.LayoutParams(-1,-2);
        setLayoutParams(vp);
    }

    @Override public boolean setNormalHit() {
        mBar.setVisibility(View.INVISIBLE);
        mHitIv.setVisibility(View.VISIBLE);
        mHitIv.setImageResource(R.drawable.cm_pull_down);
        ViewCompat.animate(mHitIv).rotation(0).start();
        mHitTv.setText(R.string.cm_hit_pull);
        mTimeTv.setText("Long long ago");
        return true;
    }

    @Override public boolean setReadyHit() {
        mBar.setVisibility(View.INVISIBLE);
        mHitIv.setVisibility(View.VISIBLE);
        ViewCompat.animate(mHitIv).rotation(180).start();
        mHitTv.setText(R.string.cm_hit_release);
        return true;
    }

    @Override public boolean setLoadingHit() {
        mBar.setVisibility(View.VISIBLE);
        mHitIv.setVisibility(View.INVISIBLE);
        mHitTv.setText(R.string.cm_state_loading);
        return true;
    }

    @Override public boolean setErrorHit() {
        mBar.setVisibility(View.INVISIBLE);
        mHitIv.setVisibility(View.INVISIBLE);
        mHitTv.setText(R.string.cm_net_failed);
        return true;
    }
}
