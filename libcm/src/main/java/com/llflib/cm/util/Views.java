package com.llflib.cm.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;

/**
 * Created by llf on 2015/8/31.
 */
public class Views {
    public static int getDimenPx(Context ctx,int value){
        return getDimenPx(ctx,value,TypedValue.COMPLEX_UNIT_DIP);
    }

    public static int getDimenPx(Context ctx,int value, int unit){
        DisplayMetrics metrics = ctx.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(unit,value,metrics);
    }

}
