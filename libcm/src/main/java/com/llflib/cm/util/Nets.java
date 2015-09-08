package com.llflib.cm.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

import com.llflib.cm.net.LifeTask;
import com.llflib.cm.net.NetTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

/**
 * Created by 908397 on 2015/2/4.
 * 网络相关
 */
public class Nets {
    /**获取当前移动设备网络连接类型**/
    public static int getNetConnectType(Context ctx){
        ConnectivityManager cm  = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if(ni == null || !ni.isAvailable() || !ni.isConnected()){
            return -1;
        }
        return ni.getType();
    }

    public static boolean isWifiConnect(Context ctx){
        return ConnectivityManager.TYPE_WIFI == getNetConnectType(ctx);
    }

    /**
     * 下载网络文件到指定位置
     * @param url 网络地址
     * @param saveFile 要保存的文件位置
     *
     * @return 若成功，返回ture 其它返回false
     * **/
    public static boolean loadFile(String url,File saveFile){
        if(TextUtils.isEmpty(url))
            return false;
        URL u = null;
        try {
            u = new URL(url);
        } catch (MalformedURLException e) {
            ILog.i("loadFile error,msg " + e.getMessage());
            return false;
        }

        try {
            InputStream is = u.openStream();
            FileOutputStream fos = new FileOutputStream(saveFile);
            int idx = 0;
            byte[]buf = new byte[1024];
            while((idx = is.read(buf)) >0){
                fos.write(buf,0,idx);
            }
            fos.flush();
            fos.close();
            is.close();
        } catch (IOException e) {
            ILog.i("loadFile save error,msg "+e.getMessage());
            return false;
        }
        ILog.v("load file sucess,path "+saveFile.getAbsolutePath());
        return true;
    }

    public static String format(List<NetTask.Pair> params,String encode){
        if(params == null || params.isEmpty())
            return null;
        StringBuffer sb = new StringBuffer();
        for(NetTask.Pair p:params){
            if(sb.length() > 0){
                sb.append("&");
            }
            try {
                sb.append(URLEncoder.encode(p.name,encode));
            } catch (UnsupportedEncodingException e) {
                sb.append(URLEncoder.encode(p.name));
            }
            sb.append("=");
            try {
                sb.append(URLEncoder.encode(p.value,encode));
            } catch (UnsupportedEncodingException e) {
                sb.append(URLEncoder.encode(p.value));
            }
        }
        return sb.toString();
    }


}
