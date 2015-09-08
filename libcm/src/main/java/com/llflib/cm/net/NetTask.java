package com.llflib.cm.net;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.llflib.cm.util.Files;
import com.llflib.cm.util.ILog;
import com.llflib.cm.util.Nets;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by llf on 2015/7/17.
 */
public abstract class NetTask<Result> extends LifeTask<Result, Result> {
    static final String ENCODING = "utf-8";
    static final String TAG = "NetTask";

    ArrayList<Pair> mParams;
    ArrayList<Pair> mHeaders;
    boolean mFore, mHasCache;
    String mRootPath;
    String mUrl;

    public NetTask(String url) {
        mUrl = url;
    }

    public NetTask setForce(boolean fore) {
        mFore = fore;
        return this;
    }

    public NetTask setRooPath(String path) {
        mRootPath = path;
        return this;
    }

    public NetTask addParams(Pair p) {
        if (mParams == null)
            mParams = new ArrayList<>();
        mParams.add(p);
        return this;
    }

    public NetTask addHeader(Pair p) {
        if (mHeaders == null)
            mHeaders = new ArrayList<>();
        mHeaders.add(p);
        return this;
    }

    @Override public void onStart() {
        execute(Nets.format(mParams, ENCODING));
    }

    @Override protected Result doInBackground(String... params) {
        String key = mUrl;
        String rs = null;
        File cache = getCacheFile(key);
        if (!mFore && cache != null && cache.exists()) {
            rs = loadFromCache(cache);
        }
        if (!TextUtils.isEmpty(rs)) {
            Result result = parserData(rs, true);
            if (result == null) {
                cache.delete();
            } else {
                publishProgress(result);
            }
        }
        rs = loadFromNet(key);
        onLoadFromNet(key, rs);
        return parserData(rs, false);
    }

    protected Result parserData(String s, boolean cache) {
        if (TextUtils.isEmpty(s)) {
            return null;
        }
        Result rs = null;
        Gson gson = new Gson();
        Type t = foundResultType();
        try {
            rs = gson.fromJson(s, t);
        } catch (JsonSyntaxException e) {
            ILog.e(TAG, "loadFromNet parse error,", e);
        }
        onParseResult(rs, cache);
        return rs;
    }

    Type foundResultType() {
        final String key = NetTask.class.getName();
        Class clz = this.getClass();
        Class child = clz.getSuperclass();
        Type childType = child.getGenericSuperclass();
        while (child != null && childType != null && childType.toString().indexOf("Result") < 0) {
            ILog.i(" superClass  :" + clz.getGenericSuperclass() + ",child :" + clz);
            clz = child;
            child = clz.getSuperclass();
            childType = child.getGenericSuperclass();
        }
        Type type = clz.getGenericSuperclass();
        ILog.i("Class Found Type  :" + type);
        Type[] types = ((ParameterizedType) type).getActualTypeArguments();
        return types[0];
    }

    String loadFromCache(File cache) {
        String str = Files.stringFromFile(cache, ENCODING);
        if (TextUtils.isEmpty(str))
            return null;
        return str;
    }

    void cacheResult(String result, File cache) {
        if (cache == null || TextUtils.isEmpty(result))
            return;
        File parent = cache.getParentFile();
        if (!parent.exists() && !parent.mkdirs())
            return;
        Files.stringToFile(cache, result, ENCODING);
    }

    String loadFromNet(String url) {
        Request request = onCreateRequest(url, mHeaders);
        Response response;
        try {
            response = OkHttpUtil.execute(request);
        } catch (IOException e) {
            ILog.e(TAG, "loadFromNet request error," + e.getMessage());
            return null;
        }
        if (!response.isSuccessful()) {
            ILog.e(TAG, "loadFromNet request faild,your need check the network");
            return null;
        }
        onResponseHeader(response);
        try {
            return response.body().string();
        } catch (IOException e) {
            ILog.e(TAG, "loadFromNet response translate to string error ,", e);
        }
        return null;
    }

    File getCacheFile(String key) {
        if (TextUtils.isEmpty(mRootPath) || TextUtils.isEmpty(key))
            return null;
        return new File(mRootPath, Files.getNumName(key));
    }

    protected void setHeads(Request.Builder builder, List<Pair> headers) {
        if (headers == null || headers.isEmpty())
            return;
        for (int i = headers.size() - 1; i >= 0; i--) {
            Pair p = headers.get(i);
            builder.addHeader(p.name, p.value);
        }
    }

    protected Request onCreateRequest(String url, List<Pair> headers) {
        Request.Builder builder = new Request.Builder().url(mUrl);
        setHeads(builder, headers);
        return builder.build();
    }

    protected void onResponseHeader(Response response){
    }

    protected void onParseResult(Result rs, boolean cache) {
    }

    protected void onLoadFromNet(String key, String rs) {
        ILog.i("load string :::" + rs);
        if (!TextUtils.isEmpty(rs)) {
            File cache = getCacheFile(key);
            if (cache == null)
                return;
            cacheResult(rs, cache);
        }
    }

    @Override protected void onProgressUpdate(Result... values) {
        mHasCache = true;
        onPostResult(values[0]);
    }

    @Override protected void onPostExecute(Result result) {
        if (!mHasCache)
            onPostResult(result);
    }

    protected abstract void onPostResult(Result result);

    public static class Pair {
        public String name;
        public String value;

        public Pair(String n, String v) {
            name = n;
            value = v;
        }
    }


}
