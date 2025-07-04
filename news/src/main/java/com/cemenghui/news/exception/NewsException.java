package com.cemenghui.news.exception;

import lombok.Getter;

@Getter
public class NewsException extends RuntimeException {
    private String code;
    private String message;

    public NewsException(String code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public NewsException(String message) {
        super(message);
        this.code = "-1";
        this.message = message;
    }
}
