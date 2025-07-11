package com.cemenghui.news.exception;

public class NewsNotFoundException extends NewsException {
    public NewsNotFoundException(Long newsId) {
        super("NEWS_NOT_FOUND", "动态不存在，ID: " + newsId);
    }
}