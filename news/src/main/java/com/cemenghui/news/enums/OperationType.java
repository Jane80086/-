package com.cemenghui.news.enums;

import lombok.Getter;

@Getter
public enum OperationType {
    PUBLISH("PUBLISH", "发布"),
    EDIT("EDIT", "编辑"),
    DELETE("DELETE", "删除"),
    AUDIT("AUDIT", "审核"),
    VIEW("VIEW", "查看");

    private final String code;
    private final String desc;

    OperationType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
