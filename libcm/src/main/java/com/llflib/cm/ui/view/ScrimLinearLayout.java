package com.llflib.cm.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.WindowInsetsCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import timber.log.Timber;

/**
 * Created by llf on 2015/9/9.
 */
public class ScrimLinearLayout extends FrameLayout {
    private WindowInsetsCompat mLastInserts;
    private Paint mPaint;

    public ScrimLinearLayout(Context context) {
        this(context, null);
    }

    public ScrimLinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScrimLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (Build.VERSION.SDK_INT > 21) {
            Timber.i("setSystemInsert init Insert");
            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mPaint.setColor(Color.RED);
            if (ViewCompat.getFitsSystemWindows(this)) {
                Timber.i("setSystemInsert init getFitsSystemWindows");
                ViewCompat.setOnApplyWindowInsetsListener(this, new ScrimApplyWindowInsertsListener());
                //                setSystemUiVisibility(SYSTEM_UI_FLAG_LOW_PROFILE);
            }
        }
//        setSystemUiVisibility(SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }

    void setSystemInsert(WindowInsetsCompat insert) {
        Timber.i("setSystemInsert " + insert);
        if (mLastInserts != insert) {
            mLastInserts = insert;
            setWillNotDraw(true);
        }
    }

    @Override protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (mLastInserts == null || mLastInserts.getSystemWindowInsetTop() <= 0)
            return;
        Timber.i("setSystemInsert top :" + mLastInserts.getSystemWindowInsetTop() + ",bottom:" +
                mLastInserts.getSystemWindowInsetBottom() + ",left:" + mLastInserts.getSystemWindowInsetLeft() +
                ",right:" + mLastInserts.getSystemWindowInsetRight());
        canvas.drawRect(0, 0, getWidth(), mLastInserts.getSystemWindowInsetTop(), mPaint);
    }

    class ScrimApplyWindowInsertsListener implements android.support.v4.view.OnApplyWindowInsetsListener {

        @Override public WindowInsetsCompat onApplyWindowInsets(View view, WindowInsetsCompat windowInsetsCompat) {
            ScrimLinearLayout.this.setSystemInsert(windowInsetsCompat);
            return windowInsetsCompat.consumeSystemWindowInsets();
        }
    }

}
