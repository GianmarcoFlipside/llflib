//package com.llflib.cm.net;
//
//import com.llflib.cm.db.Result;
//import com.llflib.cm.net.AbstractNet;
//import com.squareup.okhttp.Request;
//import com.squareup.okhttp.Response;
//
//import java.io.IOException;
//import java.lang.reflect.Type;
//
//import rx.Subscriber;
//
///**
// * Created by llf on 2015/9/28.
// */
//public class BaseNet extends AbstractNet{
//    <X>void processRequest(Subscriber<X> subscriber,Request req,Type type){
//        Response response;
//        try {
//            response = executeRequest(req);
//        } catch (IOException e) {
//            subscriber.onError(e);
//            return;
//        }
//        if (!response.isSuccessful()) {
//            subscriber.onError(new IOException());
//            return;
//        }
//        String rs;
//        try {
//            rs = response.body().string();
//        } catch (IOException e) {
//            subscriber.onError(e);
//            return;
//        }
//        Result<X> result = parse(rs,type);
//        if(!result.isSuccess()){
//            subscriber.onError(new CMNetException(result.getMessage()));
//        }else{
//            subscriber.onNext(result.getData());
//        }
//    }
//}
