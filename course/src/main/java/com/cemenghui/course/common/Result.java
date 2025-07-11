package com.cemenghui.course.common;

import lombok.Data;
import java.io.Serializable;

@Data
public class Result implements Serializable {
    private int code;
    private String message;
    private Object data;

    public static Result success(Object data) {
        Result m = new Result();
        m.setCode(200);
        m.setData(data);
        m.setMessage("操作成功");
        return m;
    }
    public static Result success(String mess, Object data) {
        Result m = new Result();
        m.setCode(200);
        m.setMessage(mess);
        m.setData(data);
        return m;
    }
    public static Result fail(String mess) {
        Result m = new Result();
        m.setCode(500);
        m.setMessage(mess);
        return m;
    }
} 