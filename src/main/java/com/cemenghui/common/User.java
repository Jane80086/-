package com.cemenghui.common;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@TableName("users")
@Data
public class User {
    @TableId(value = "id",type = IdType.AUTO)
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
    private String userType;
    
    @TableField("status")
    private Integer status = 1;
    
    @TableField("created_time")
    private LocalDateTime createdTime = LocalDateTime.now();
    
    @TableField("updated_time")
    private LocalDateTime updatedTime = LocalDateTime.now();
} 