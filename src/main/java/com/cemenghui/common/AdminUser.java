package com.cemenghui.common;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AdminUser extends User {

    public AdminUser() {
        this.userType = "ADMIN";
        this.adminLevel = "ADMIN";
    }
}