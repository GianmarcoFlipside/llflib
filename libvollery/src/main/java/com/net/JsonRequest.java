package com.net;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.bean.Page;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.util.ILog;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by 908397 on 2015/1/13.
 * json数据请求类
 */
public class JsonRequest<T> extends Request<String> {
    private INet.OnResponse<T> mListener;
    private String mPostParams;
    private Page mPage;

    public JsonRequest(Page page, final INet.OnResponse<T> l) {
        super(Method.GET, page.toString(), new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                l.onFaild(error.getMessage());
            }
        });
        setShouldCache(page.page < 0);
        mListener = l;
        mPage = page;
    }

    public JsonRequest(Page page, String postParam, final INet.OnResponse<T> l) {
        super(Method.POST, page.toString(), new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                l.onFaild(error.getMessage());
            }
        });
        setShouldCache(page.page < 0);
        mPostParams = postParam;
        mListener = l;
        mPage = page;
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse networkResponse) {
        String result;
        try {
            result = new String(networkResponse.data, HttpHeaderParser.parseCharset(networkResponse.headers));
        } catch (UnsupportedEncodingException e) {
            result = new String(networkResponse.data);
        }
        return Response.success(result, HttpHeaderParser.parseCacheHeaders(networkResponse));
    }

    @Override
    public String getCacheKey() {
        String key = super.getCacheKey();
        if(key != null){
            key = key +mPage.getPolicy();
        }
        return key;
    }

    @Override
    protected void deliverResponse(String s) {
        ILog.i("JsonRequest", "deliverResponse " + s);
        Type type = getListenerType();
        if (type == null) {
            mListener.onFaild("can't get the data type!!!");
            return;
        }
        ILog.v("deliverResponse getType " + type);
        try {
            Gson gson = new Gson();
            T p = gson.fromJson(s, type);
            //设置分页信息
            if (p instanceof IPage) {
                ((IPage) p).setP(mPage);
            }
            mListener.onSucess(p);
        } catch (JsonSyntaxException e) {
            ILog.w("JsonRequest", "parse json string error,",e);
            mListener.onFaild(e.getMessage());
        }
    }

    private Type getListenerType() {
        Type[] types = mListener.getClass().getGenericInterfaces();
        for (Type t : types) {
            ILog.i("type :"+t.toString());
            if (!(t instanceof ParameterizedType))
                continue;
            ParameterizedType pt = (ParameterizedType) t;
            if (pt.toString().indexOf(INet.OnResponse.class.getSimpleName()) < 0)
                continue;
            ILog.i("" + pt.getActualTypeArguments()[0]);
            return pt.getActualTypeArguments()[0];
        }
        return null;
    }
    @Override
    public byte[] getBody() throws AuthFailureError {
//        return super.getBody();
        if (mPostParams != null) {
            try {
                return mPostParams.getBytes(getParamsEncoding());
            } catch (UnsupportedEncodingException e) {
                return mPostParams.getBytes();
            }
        }
        return null;
    }
}
