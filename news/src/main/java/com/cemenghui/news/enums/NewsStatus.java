package com.cemenghui.news.enums;

import lombok.Getter;

@Getter
public enum NewsStatus {
    PENDING(0, "待审核"),
    PUBLISHED(1, "已发布"),
    REJECTED(2, "已拒绝");

    private final Integer code;
    private final String desc;

    NewsStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static NewsStatus getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (NewsStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null; // Return null if no match found
    }
}