package com.llflib.cm.ui.view;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

/**
 * Created by llf on 2015/7/9.
 */
public class BorderColorDrawable extends Drawable {
    final static int STROKE = 3;
    private BorderState mState;
    private Paint mPaint,mBorderPaint;
    public BorderColorDrawable(@NonNull BorderState state) {
        mState = new BorderState(state);
        initPaint();
    }

    public BorderColorDrawable(int color,int borderColor){
        this(color,borderColor,3);
    }

    public BorderColorDrawable(int color,int borderColor,int borderMode) {
        mState = new BorderState();
        mState.mColor = color;
        mState.mBorderColor = borderColor;
        mState.mBorderMode = borderMode;
        initPaint();
    }

    void initPaint(){
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBorderPaint.setStyle(Paint.Style.FILL);
        mBorderPaint.setStrokeWidth(STROKE);
        mPaint.setColor(mState.mColor);
        mBorderPaint.setColor(mState.mBorderColor);
    }

    @Override public void draw(Canvas canvas) {
        Rect bound = getBounds();
        int top = bound.top;
        int bottom = bound.bottom;
        if((mState.mBorderMode&0x1 ) != 0){
            top -= STROKE;
        }
        if((mState.mBorderMode & 0x2) != 0){
            bottom -= STROKE;
        }
        canvas.drawRect(bound.left,top,bound.right,bottom,mPaint);
        if((mState.mBorderMode&0x1 ) != 0) {
            canvas.drawLine(bound.left, bound.top, bound.right, bound.top, mBorderPaint);
        }
        if((mState.mBorderMode & 0x2) != 0) {
            canvas.drawLine(bound.left, bound.bottom, bound.right, bound.bottom, mBorderPaint);
        }
    }

    public void setColor(int color){
        mState.mColor = color;
        invalidateSelf();
    }

    public void setBorderColor(int color){
        mState.mBorderColor = color;
        invalidateSelf();
    }

    @Override public void setAlpha(int alpha) {
        mState.mColor |= ((alpha & 0xFF)<<24);
        invalidateSelf();
    }

    @Override public void setColorFilter(ColorFilter cf) {

    }

    @Override public int getOpacity() {
        switch(mState.mColor>>24){
            case 0:
                return PixelFormat.TRANSPARENT;
            case 255:
                return PixelFormat.OPAQUE;
            default:
                return PixelFormat.TRANSLUCENT;
        }
    }

    final static class BorderState extends ConstantState{
        int mColor;
        int mBorderColor;
        int mBorderMode;
        int mConfigurations;
        public BorderState() {
        }

        BorderState(BorderState s) {
            this.mColor = s.mColor;
            this.mBorderColor = s.mBorderColor;
            this.mConfigurations = s.mConfigurations;
            this.mBorderMode = 3;
        }

        @Override public Drawable newDrawable(Resources res) {
            return new BorderColorDrawable(this);
        }
        @Override public boolean canApplyTheme() {
            return super.canApplyTheme();
        }

        @Override public Drawable newDrawable() {
            return new BorderColorDrawable(this);
        }

        @Override public int getChangingConfigurations() {
            return mConfigurations;
        }
    }
}

