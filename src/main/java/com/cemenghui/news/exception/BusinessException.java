package com.cemenghui.news.exception;

public class BusinessException extends NewsException {
    public BusinessException(String message) {
        super("BUSINESS_ERROR", message);
    }
}