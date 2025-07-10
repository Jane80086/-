package com.system.vo;

import lombok.Data;

@Data
public class ResultVO<T> {
    private int code;      // 状态码，200=成功，401=未授权，500=服务器错误等
    private String message;// 提示信息
    private T data;        // 返回数据

    // 手动添加getter和setter方法
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    // 成功
    public static <T> ResultVO<T> success(T data) {
        ResultVO<T> vo = new ResultVO<>();
        vo.setCode(200);
        vo.setMessage("success");
        vo.setData(data);
        return vo;
    }

    // 未授权
    public static <T> ResultVO<T> unauthorized(String message) {
        ResultVO<T> vo = new ResultVO<>();
        vo.setCode(401);
        vo.setMessage(message);
        vo.setData(null);
        return vo;
    }

    // 失败
    public static <T> ResultVO<T> fail(String message) {
        ResultVO<T> vo = new ResultVO<>();
        vo.setCode(500);
        vo.setMessage(message);
        vo.setData(null);
        return vo;
    }
} 