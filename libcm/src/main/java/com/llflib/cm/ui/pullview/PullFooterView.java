package com.llflib.cm.ui.pullview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.llflib.cm.R;

/**
 * Created by llf on 2015/8/31.
 */
public class PullFooterView extends RelativeLayout implements IPull{
    private ProgressBar mBar;
    private TextView mHitTv;
    public PullFooterView(Context context) {
        this(context, null);
    }

    public PullFooterView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PullFooterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setupViews(context);
    }

    protected void setupViews(Context ctx) {
        TextView tv = new TextView(ctx, null, android.R.attr.textAppearanceSmall);
        tv.setId(android.R.id.text2);
        LayoutParams lp = new LayoutParams(-2, -2);
        lp.addRule(CENTER_IN_PARENT);
        addView(tv, lp);
        mHitTv = tv;

        ProgressBar progress = new ProgressBar(ctx, null, android.R.attr.progressBarStyle);
        progress.setIndeterminate(true);
        lp = new LayoutParams(-2, -2);
        lp.addRule(LEFT_OF, android.R.id.text2);
        addView(progress, lp);
        ViewGroup.LayoutParams pp = new ViewGroup.LayoutParams(-1, -2);
        setLayoutParams(pp);
        mBar = progress;
    }

    @Override public boolean setNormalHit() {
        mBar.setVisibility(INVISIBLE);
        mHitTv.setText(R.string.llf_hit_pull);
        return true;
    }

    @Override public boolean setReadyHit() {
        mBar.setVisibility(INVISIBLE);
        mHitTv.setText(R.string.llf_hit_release);
        return true;
    }

    @Override public boolean setLoadingHit() {
        mBar.setVisibility(VISIBLE);
        mHitTv.setText(R.string.llf_state_loading);
        return true;
    }

    @Override public boolean setErrorHit() {
        mBar.setVisibility(INVISIBLE);
        mHitTv.setText(R.string.llf_hit_failed);
        return true;
    }
}
