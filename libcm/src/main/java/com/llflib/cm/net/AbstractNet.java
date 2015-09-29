package com.llflib.cm.net;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.llflib.cm.util.Files;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.ConnectException;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

/**
 * Created by llf on 2015/9/24.
 */
public class AbstractNet{
    public static final  OkHttpClient OKHTTPCLIENT = new OkHttpClient();
    static final  Callback DEFAULT_CALLBACK = new Callback() {
        @Override public void onFailure(Request request, IOException e) {
        }

        @Override public void onResponse(Response response) throws IOException {
        }
    };
    final static Gson GSON = new Gson();
    protected static final String CHARSET_DEFAULT = Charset.forName("UTF-8").name();

    static void setConnectTimeout(long timeout,TimeUnit unit){
        OKHTTPCLIENT.setConnectTimeout(timeout,unit);
    }

    static {
        setConnectTimeout(30, TimeUnit.SECONDS);
    }

    protected final Response executeRequest(Request request) throws IOException {
        return OKHTTPCLIENT.newCall(request).execute();
    }

    protected final void enqueueRequest(Request request) {
        enqueueRequest(request, DEFAULT_CALLBACK);
    }

    protected final void enqueueRequest(Request request, Callback callback) {
        OKHTTPCLIENT.newCall(request).enqueue(callback);
    }

    protected String readDiskCache(File file){
        return Files.stringFromFile(file,CHARSET_DEFAULT);
    }

    protected void writeDiskCache(String s,File file){
        Files.stringToFile(file,s,CHARSET_DEFAULT);
    }

    protected String processRequest(Request req) throws ConnectException,IOException {
        Response response = executeRequest(req);
        if (!response.isSuccessful()) {
            throw new ConnectException("Connect failed");
        }
        return response.body().string();
    }

    protected <T> T parse(String s,Type type){
        if(TextUtils.isEmpty(s)) return null;
        synchronized (GSON){
            return GSON.fromJson(s,type);
        }
    }


    public static class CMNetException extends RuntimeException{
        public CMNetException(String detailMessage) {
            super(detailMessage);
        }
    }
}
