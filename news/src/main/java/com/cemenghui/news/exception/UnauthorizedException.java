package com.cemenghui.news.exception;

public class UnauthorizedException extends NewsException {
    public UnauthorizedException(String operation) {
        super("UNAUTHORIZED", "无权限执行操作: " + operation);
    }
}