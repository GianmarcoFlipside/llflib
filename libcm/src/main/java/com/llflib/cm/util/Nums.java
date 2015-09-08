package com.llflib.cm.util;

import android.graphics.Color;

/**
 * Created by llf on 2015/5/20.
 */
public class Nums {


    public static int parseInt(String value) {
        return parseInt(value, 10, 0);
    }

    /**
     * 字符串转换整形
     *
     * @param value 字符串
     * @param radio 进制
     * @param def   转换失败后返回的值
     * @return 返回转换后的数值，若转换失败返回 def值
     *  注:无法解析8位的颜色值，因其最高位为符号位
     */
    public static int parseInt(String value, int radio, int def) {
        try {
            return Integer.parseInt(value, radio);
        } catch (NumberFormatException e) {
            return def;
        }
    }

    public static int parseColor(String color,int def){
        try{
            return Color.parseColor(color);
        }catch(IllegalArgumentException e){
            return def;
        }
    }


}
