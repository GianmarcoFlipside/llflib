/*
 * Copyright 2015 llfer2006@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.llflib.cm.ui.drawable;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

/**
 * Created by llf on 2015/7/23.
 */
public class CircleDrawable extends Drawable{
    private Bitmap mBitmap;
    private RectF mCircleBounds;
    private Paint mClearPaint,mMaskPaint;
    public CircleDrawable(Bitmap bt){
        mBitmap = bt;
        mCircleBounds = new RectF();
        mClearPaint = new Paint();
        mClearPaint.setAntiAlias(true);
        mClearPaint.setColor(Color.WHITE);
        mMaskPaint = new Paint();
        mMaskPaint.setAntiAlias(true);
        mMaskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
    }

    @Override protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        mCircleBounds.set(bounds.left,bounds.top,bounds.right,bounds.bottom);
    }

    @Override public void draw(Canvas canvas) {
        if(mBitmap == null) return;
        float radius = Math.round(mCircleBounds.width()/2.0);
        canvas.saveLayer(mCircleBounds, mClearPaint, Canvas.ALL_SAVE_FLAG);
        canvas.drawRoundRect(mCircleBounds,radius,radius,mClearPaint);
        canvas.saveLayer(mCircleBounds,mMaskPaint, Canvas.ALL_SAVE_FLAG);
        canvas.drawBitmap(mBitmap,null,getBounds(),mClearPaint);
        canvas.restore();
    }

    @Override public void setAlpha(int alpha) {
        mClearPaint.setAlpha(alpha);
    }

    @Override public void setColorFilter(ColorFilter cf) {
        mClearPaint.setColorFilter(cf);
    }

    @Override public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override public int getIntrinsicWidth() {
        return mBitmap.getWidth();
    }

    @Override public int getIntrinsicHeight() {
        return mBitmap.getHeight();
    }

}
