package com.llflib.cm.ui.view;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.animation.AccelerateInterpolator;

import java.util.Arrays;

/**
 * Created by llf on 2015/7/13.
 */
public class RevealDrawable extends Drawable implements Drawable.Callback,Animatable,Animator.AnimatorListener {
    private int mRadius, mMaxRadius;
    private int[] mCircleCoordinate;
    private boolean mAnimatorIn;
    private Animator mAnimator;
    private Paint mPaint;
    private Path mPath;

    private Runnable mCallback;

    public RevealDrawable() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.BLUE);
        mPath = new Path();
        mCircleCoordinate = new int[2];
        Arrays.fill(mCircleCoordinate, -1);
    }

    public void setCircleCoordinate(int x, int y) {
        mCircleCoordinate[0] = x;
        mCircleCoordinate[1] = y;

        int w = Math.max(x, getBounds().right - x);
        int h = Math.max(y, getBounds().bottom - y);
        mMaxRadius = (int) Math.sqrt(Math.pow(w, 2) + Math.pow(h, 2));
    }

    public void setRadius(float r) {
        mRadius = Math.round(r * mMaxRadius);
        invalidateSelf();
    }

    public void setPaintColor(int color) {
        mPaint.setColor(color);
        invalidateSelf();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB) private Animator createAnimator(float start, float end) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(RevealDrawable.this, "radiu", start, end);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.setDuration(mAnimatorIn ? 500 : 5000);
        animator.addListener(this);
        return animator;
    }

    @Override public void draw(Canvas canvas) {
        if (mAnimatorIn) {
            canvas.drawCircle(mCircleCoordinate[0], mCircleCoordinate[1], mRadius, mPaint);
        } else {
            mPath.reset();
            mPath.addCircle(mCircleCoordinate[0], mCircleCoordinate[1], mRadius, Path.Direction.CCW);
            canvas.save();
            canvas.clipPath(mPath, Region.Op.REPLACE);
            canvas.drawColor(Color.BLUE);
            canvas.restore();
        }
    }

    @Override protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        if (mCircleCoordinate[0] == -1 || mCircleCoordinate[1] == -1) {
            setCircleCoordinate(bounds.width() / 2, bounds.height() / 2);
        } else {
            setCircleCoordinate(mCircleCoordinate[0], mCircleCoordinate[1]);
        }
    }

    @Override public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override public void setColorFilter(ColorFilter cf) {
        mPaint.setColorFilter(cf);
    }

    @Override public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override public void invalidateDrawable(Drawable who) {
        invalidateSelf();
    }

    @Override public void scheduleDrawable(Drawable who, Runnable what, long when) {
        scheduleSelf(what, when);
    }

    @Override public void unscheduleDrawable(Drawable who, Runnable what) {
        unscheduleSelf(what);
    }

    @Override public void start() {
        createAnimator(0f,1f).start();
    }

    @Override public void stop() {
        mAnimator.end();
    }

    @Override public boolean isRunning() {
        return mAnimator != null;
    }

    @Override public void onAnimationStart(Animator animation) {
        mAnimator = animation;
    }

    @Override public void onAnimationEnd(Animator animation) {
        mAnimator = null;
    }

    @Override public void onAnimationCancel(Animator animation) {

    }

    @Override public void onAnimationRepeat(Animator animation) {

    }
}
