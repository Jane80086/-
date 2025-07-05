package com.cemenghui.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * 统一用户实体类
 * 对应新的users表结构
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
    private String userType; // ADMIN, ENTERPRISE, NORMAL, SYSTEM
    
    @TableField("status")
    private Integer status = 1; // 0: 禁用, 1: 启用
    
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
    
    // 用户类型枚举
    public enum UserType {
        ADMIN("管理员"),
        ENTERPRISE("企业用户"),
        NORMAL("普通用户"),
        SYSTEM("系统用户");
        
        private final String description;
        
        UserType(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    // 状态枚举
    public enum Status {
        DISABLED(0, "禁用"),
        ENABLED(1, "启用");
        
        private final Integer code;
        private final String description;
        
        Status(Integer code, String description) {
            this.code = code;
            this.description = description;
        }
        
        public Integer getCode() {
            return code;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    /**
     * 判断是否为管理员
     */
    public boolean isAdmin() {
        return UserType.ADMIN.name().equals(this.userType);
    }
    
    /**
     * 判断是否为企业用户
     */
    public boolean isEnterprise() {
        return UserType.ENTERPRISE.name().equals(this.userType);
    }
    
    /**
     * 判断是否为普通用户
     */
    public boolean isNormal() {
        return UserType.NORMAL.name().equals(this.userType);
    }
    
    /**
     * 判断是否为系统用户
     */
    public boolean isSystem() {
        return UserType.SYSTEM.name().equals(this.userType);
    }
    
    /**
     * 判断用户是否启用
     */
    public boolean isEnabled() {
        return Status.ENABLED.getCode().equals(this.status);
    }
    
    /**
     * 判断用户是否禁用
     */
    public boolean isDisabled() {
        return Status.DISABLED.getCode().equals(this.status);
    }
} 