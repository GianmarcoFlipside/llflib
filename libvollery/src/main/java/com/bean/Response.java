package com.bean;

/**
 * @author llfer 2015/3/18
 * 请求返回实体类
 */
public class Response {
    public int result;
    public String messge;

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public boolean isSucess(){
        return result == 1;
    }

    public String getMessge() {
        return messge;
    }

    public void setMessge(String messge) {
        this.messge = messge;
    }
}
