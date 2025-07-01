package com.cemenghui.common;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class EnterpriseUser extends User {

    public EnterpriseUser() {
        this.userType = "ENTERPRISE";
    }
}