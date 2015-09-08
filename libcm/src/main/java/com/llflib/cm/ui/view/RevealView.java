package com.llflib.cm.ui.view;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Region;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;

import java.util.Arrays;

/**
 * Created by llf on 2015/7/13.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB) public class RevealView extends View implements Animator.AnimatorListener{

    private int mRadius, mMaxRadius;
    private int[] mCricleCoordinate;
    private boolean mAnimatorIn;
    private Paint mPaint;
    private Path mPath;

    private Runnable mCallback;
    public RevealView(Context context) {
        this(context, null);
    }

    public RevealView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RevealView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.BLUE);
        mPath = new Path();
        mCricleCoordinate = new int[2];
        Arrays.fill(mCricleCoordinate,-1);
    }

    public void setCricleCoordinate(int x, int y){
        mCricleCoordinate[0] = x;
        mCricleCoordinate[1] = y;

        int w = Math.max(x,getMeasuredWidth() - x);
        int h = Math.max(y,getMeasuredHeight() - y);
        mMaxRadius = (int) Math.sqrt(Math.pow(w,2)+Math.pow(h,2));
    }

    public void setRadius(float r){
        mRadius = Math.round(r* mMaxRadius);
        invalidate();
    }

    public void setPaintColor(int color){
        mPaint.setColor(color);
    }

    public void auto(Runnable callback,boolean in){
        ViewTreeObserver vto = getViewTreeObserver();
        mCallback = callback;
        mAnimatorIn = in;
        ViewTreeObserver.OnPreDrawListener pd = new ViewTreeObserver.OnPreDrawListener() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB) @Override public boolean onPreDraw() {
                getViewTreeObserver().removeOnPreDrawListener(this);
                createAnimator(mAnimatorIn ?0f:1f, mAnimatorIn ?1f:0f).start();
                return true;
            }
        };
        vto.addOnPreDrawListener(pd);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB) private Animator createAnimator(float start,float end){
        ObjectAnimator animator = ObjectAnimator.ofFloat(RevealView.this,"radiu",start,end);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.addListener(RevealView.this);
        animator.setDuration(mAnimatorIn ?500:5000);
        return animator;
    }

    private void autoDismiss(){
        ViewGroup vg = (ViewGroup) getParent();
        if(vg != null) {
            vg.removeView(this);
            if(mCallback != null){
                mCallback.run();
            }
        }
    }

    @Override public void onAnimationStart(Animator animation) {

    }

    @Override public void onAnimationEnd(Animator animation) {
        autoDismiss();
    }

    @Override public void onAnimationCancel(Animator animation) {

    }

    @Override public void onAnimationRepeat(Animator animation) {

    }

    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(mAnimatorIn) {
            canvas.drawCircle(mCricleCoordinate[0], mCricleCoordinate[1], mRadius, mPaint);
        }else{
            mPath.reset();
            mPath.addCircle(mCricleCoordinate[0], mCricleCoordinate[1], mRadius, Path.Direction.CCW);
            canvas.save();
            canvas.clipPath(mPath, Region.Op.REPLACE);
            canvas.drawColor(Color.BLUE);
            canvas.restore();
        }
    }

    @Override protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if(mCricleCoordinate[0] == -1|| mCricleCoordinate[1] == -1){
            setCricleCoordinate(w / 2, h / 2);
        }else{
            setCricleCoordinate(mCricleCoordinate[0], mCricleCoordinate[1]);
        }
    }
}
