package com.cemenghui.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

/**
 * 管理员用户实体类，对应 users 表
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("users")
public class AdminUser {
    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("username")
    private String username;
    @TableField("password")
    private String password;
    @TableField("real_name")
    private String realName;
    @TableField("email")
    private String email;
    @TableField("phone")
    private String phone;
    @TableField("user_type")
    private String userType;
    @TableField("status")
    private Integer status;
    @TableField("department")
    private String department;
    @TableField("nickname")
    private String nickname;
    @TableField("avatar")
    private String avatar;
    @TableField("is_remembered")
    private Boolean isRemembered;
    @TableField("enterprise_id")
    private String enterpriseId;
    @TableField("dynamic_code")
    private String dynamicCode;
    @TableField("role")
    private String role;
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    @TableField("deleted")
    private Integer deleted;
    // 管理员专属属性
    @TableField(exist = false)
    private Boolean isSuperAdmin;
    
    // 添加兼容方法
    public String getAccount() {
        return this.username;
    }
    
    public String getRole() {
        return this.role;
    }
}