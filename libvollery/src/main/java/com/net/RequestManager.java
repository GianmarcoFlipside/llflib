package com.net;

import android.content.Context;

import com.BaseApp;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
/**
 * @author llfer 2015/3/26
 * http请求管理类，不支持文件传输相关
 */
public class RequestManager {
    private static RequestManager ourInstance;
    public static RequestManager get() {
        if (ourInstance == null) {
            ourInstance = new RequestManager();
        }
        return ourInstance;
    }

    private RequestQueue mMainQueue;
    private ImageLoader mImageLoader;
    private RequestManager() {
    }

    private void ensureInit(){
        if(mMainQueue == null){
            Context ctx = BaseApp.getContext();
            mMainQueue = Volley.newRequestQueue(ctx,new CustomCache());
            mImageLoader = new ImageLoader(mMainQueue,new BitmapCache());
            mMainQueue.start();
        }
    }

    public <T> Request<T> taskSync(Request<T> task){
        ensureInit();
        return mMainQueue.add(task);
    }

    public void loadImage(NetworkImageView iv,String url){
        if(iv == null) return;
        ensureInit();
        iv.setImageUrl(url,mImageLoader);
    }

    public ImageLoader getImageLoader(){
        ensureInit();
        return mImageLoader;
    }

}
