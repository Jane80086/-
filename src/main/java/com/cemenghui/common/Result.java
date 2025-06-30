package com.cemenghui.common;

import lombok.Data;
import java.io.Serializable;

@Data
public class Result implements Serializable {
    private String code;
    private String msg;
    private Object data;
    public static Result success(Object data) {
        Result m=new Result();
        m.setCode("0");
        m.setData(data);
        m.setMsg("操作成功");
        return m;
    }
    public static Result success(String mess, Object data) {
        Result m=new Result();
        m.setCode("0");
        m.setMsg(mess);
        m.setData(data);
        return m;
    }
    public static Result fail(String mess) {
        Result m=new Result();
        m.setCode("-1");
        m.setMsg(mess);
        m.setCode("-1");
        return m;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
    public void setData(Object data) {
        this.data = data;
    }
} 