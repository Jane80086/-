package com.cemenghui.common;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class NormalUser extends User {

    public NormalUser() {
        this.userType = "NORMAL";
    }
}