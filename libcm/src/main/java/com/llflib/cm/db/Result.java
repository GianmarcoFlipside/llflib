package com.llflib.cm.db;

/**
 * Created by llf on 2015/9/28.
 */
public class Result<T> {
    String result;
    String message;
    String code;

    T data;
    public boolean isSuccess() {
        return "true".equalsIgnoreCase(result);
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}

