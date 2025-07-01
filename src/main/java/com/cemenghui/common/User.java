package com.cemenghui.common;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("users")
public class User {
    @TableId(value = "id", type = IdType.AUTO)
    protected Long id;

    @TableField("username")
    protected String username;

    @TableField("password")
    protected String password;

    @TableField("email")
    protected String email;

    @TableField("phone")
    protected String phone;

    @TableField("status")
    protected Integer status = 1;

    @TableField(value = "user_type")
    protected String userType;

    @TableField(value = "created_time", fill = FieldFill.INSERT)
    protected LocalDateTime createdTime;

    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    protected LocalDateTime updatedTime;

    // 普通用户字段
    @TableField(value = "real_name", condition = SqlCondition.LIKE)
    protected String realName;

    @TableField("avatar")
    protected String avatar;

    @TableField("bio")
    protected String bio;

    // 企业用户字段
    @TableField("company_name")
    protected String companyName;

    @TableField("business_license")
    protected String businessLicense;

    @TableField("contact_person")
    protected String contactPerson;

    @TableField("contact_phone")
    protected String contactPhone;

    @TableField("company_address")
    protected String companyAddress;

    // 管理员用户字段
    @TableField("admin_level")
    protected String adminLevel;

    @TableField("permissions")
    protected String permissions;
}