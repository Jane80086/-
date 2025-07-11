package com.cemenghui.course.service;

/**
 * AI服务相关异常
 */
public class AIException extends Exception {
    
    public AIException(String message) {
        super(message);
    }
    
    public AIException(String message, Throwable cause) {
        super(message, cause);
    }
}
