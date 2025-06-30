package com.cemenghui.news.enums;

import lombok.Getter;

@Getter
public enum NewsStatus {
    PENDING(0, "待审核"),
    APPROVED(1, "已通过"),
    REJECTED(2, "已拒绝"),
    DRAFT(3, "草稿");

    private final Integer code;
    private final String desc;

    NewsStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static NewsStatus getByCode(Integer code) {
        for (NewsStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return PENDING;
    }
}