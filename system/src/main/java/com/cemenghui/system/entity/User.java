package com.cemenghui.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * 用户基础实体类，对应 users 表
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("users")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("username")
    @NotBlank(message = "用户名不能为空")
    private String username;
    @TableField("password")
    @NotBlank(message = "密码不能为空")
    private String password;
    @TableField("real_name")
    private String realName;
    @TableField("email")
    private String email;
    @TableField("phone")
    private String phone;
    @TableField("user_type")
    @NotBlank(message = "用户类型不能为空")
    private String userType;
    @TableField("status")
    private Integer status = 1;
    @TableField("department")
    private String department;
    @TableField("nickname")
    private String nickname;
    @TableField("avatar")
    private String avatar;
    @TableField("is_remembered")
    private Boolean isRemembered = false;
    @TableField("enterprise_id")
    private String enterpriseId;
    @TableField("dynamic_code")
    private String dynamicCode;
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    @TableField("deleted")
    private Integer deleted = 0;

    public String getAccount() {
        return this.username;
    }
}
