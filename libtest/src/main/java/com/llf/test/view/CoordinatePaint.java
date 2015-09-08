package com.llf.test.view;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.HashMap;

/**
 * Created by llf on 2015/9/6.
 */
public class CoordinatePaint{
    private static final HashMap<Object,Paint> mCacheMap = new HashMap<>();

    private CoordinatePaint(){
    }

    public static Paint getPaint(Object obj){
        Paint paint = mCacheMap.get(obj);
        if(paint == null){
            paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mCacheMap.put(obj,paint);
        }
        return paint;
    }

    public static void setPaintArgs(Object obj,int color,int size){
        Paint paint = getPaint(obj);
        paint.setTextSize(size);
        paint.setColor(color);
    }

    public static void drawGrid(Object obj,Canvas canvas,int width,int height,int sx,int sy,int deltaX,int deltaY){
        Paint paint = getPaint(obj);
        int  start = sx;
        while(start < width){
            canvas.drawLine(start,sy,start,height,paint);
            canvas.drawText(String.valueOf(start), start + 10, sy + 10, paint);
            start += deltaX;
        }

        start = sy;
        while(start < height){
            canvas.drawLine(sx,start,width,start,paint);
            canvas.drawText(String.valueOf(start), start + 10, sy + 10, paint);
            start += deltaY;
        }
    }


}
