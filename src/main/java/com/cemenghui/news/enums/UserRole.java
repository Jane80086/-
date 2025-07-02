package com.cemenghui.news.enums;

import lombok.Getter;

@Getter
public enum UserRole {
    NORMAL("NORMAL", "普通用户"),
    ENTERPRISE("ENTERPRISE", "企业用户"),
    ADMIN("ADMIN", "管理员");

    private final String code;
    private final String desc;

    UserRole(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static UserRole getByCode(String code) {
        for (UserRole role : values()) {
            if (role.getCode().equals(code)) {
                return role;
            }
        }
        return NORMAL;
    }
}

