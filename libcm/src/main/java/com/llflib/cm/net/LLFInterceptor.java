package com.llflib.cm.net;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

import timber.log.Timber;

/**
 * Created by llf on 2015/10/19.
 */
public class LLFInterceptor implements Interceptor {
    private boolean mShowInput, mShowOutput;

    public LLFInterceptor() {
        this(true, true);
    }

    public LLFInterceptor(boolean in, boolean out) {
        mShowInput = in;
        mShowOutput = out;
    }

    @Override public Response intercept(Chain chain) throws IOException {
        Request req = chain.request();
        if (mShowInput)
            showInput(req);
        Response res = chain.proceed(req);
        if (mShowOutput)
            showOutput(res);
        return res;
    }

    private void showInput(Request req) {
        Timber.i(req.url().toString());
        Timber.i("Method:" + req.method());
        Timber.i("Header:\r\n" + req.headers().toString());
    }

    private void showOutput(Response res) throws IOException {
        //        if(res.isSuccessful()){
        //            InputStream is = res.body().byteStream();
        //            byte[] bytes = res.body().bytes();
        //            Timber.i(new String(bytes,0,bytes.length,"UTF-8"));
        //        }
    }
}
