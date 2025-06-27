package com.cemenghui.course.service;

/**
 * 课程未找到异常
 */
public class NotFoundException extends Exception {
    public NotFoundException(String message) {
        super(message);
    }
} 