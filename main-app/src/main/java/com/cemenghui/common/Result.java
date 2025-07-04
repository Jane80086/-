package com.cemenghui.common;

public class Result<T> {
    private int code;
    private String msg;
    private T data;

    public Result() {}
    public Result(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }
    public static <T> Result<T> success(String msg, T data) {
        return new Result<>(0, msg, data);
    }
    public static <T> Result<T> fail(String msg) {
        return new Result<>(-1, msg, null);
    }
    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }
    public String getMsg() { return msg; }
    public void setMsg(String msg) { this.msg = msg; }
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
} 