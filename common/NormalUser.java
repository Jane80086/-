package com.cemenghui.course.common;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 普通用户
 */
@TableName("users")
@Data
public class NormalUser {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("username")
    private String username;
    
    @TableField("password")
    private String password;
    
    @TableField("email")
    private String email;
    
    @TableField("phone")
    private String phone;
    
    @TableField("user_type")
    private String userType = "NORMAL";
    
    @TableField("status")
    private Integer status = 1;
    
    @TableField("real_name")
    private String realName;
    
    @TableField("avatar")
    private String avatar;
    
    @TableField("bio")
    private String bio;
    
    @TableField("created_time")
    private LocalDateTime createdTime = LocalDateTime.now();
    
    @TableField("updated_time")
    private LocalDateTime updatedTime = LocalDateTime.now();
} 