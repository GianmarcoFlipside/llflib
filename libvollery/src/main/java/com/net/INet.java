package com.net;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.bean.Page;

/**
 * Created by 908397 on 2015/1/12.
 * 数据请求基类
 */
public abstract class INet {
    public static interface OnResponse<T> {
        void onSucess(T o);

        void onFaild(String msg);
    }

    private static RequestQueue mQueue;

    protected <T> void execute(Request<T> request) {
        if (request == null)
            return;
       RequestManager.get().taskSync(request);
    }

    protected <T> void checkCallbackThrowException(OnResponse<T> l) {
        if (l == null)
            throw new NullPointerException("OnResponse can't be null");
    }

    protected <T> Page executeRequest(Page p, OnResponse<T> l) {
        checkCallbackThrowException(l);
        Request req = new JsonRequest(p, l);
        execute(req);
        return p;
    }

    /** 根据当前状态生成默认加载策略 **/
    protected String getDefaultPolicy(int page,boolean fore){
        if(page > 1) return Page.POLICY_NO_CACHE;
        if(fore) return Page.POLICY_NETWORK;
        return Page.POLICY_DEFAUL;
    }
}
