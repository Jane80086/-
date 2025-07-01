package com.cemenghui.common;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

@TableName("users")
@Data
@EqualsAndHashCode(callSuper = false)
public class AdminUser extends User {
    
    @TableField("admin_level")
    private String adminLevel = "ADMIN";
    
    @TableField("permissions")
    private String permissions;
    
    public AdminUser() {
        this.setUserType("ADMIN");
    }
} 