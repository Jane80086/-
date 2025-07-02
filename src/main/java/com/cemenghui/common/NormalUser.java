package com.cemenghui.common;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

@TableName("users")
@Data
@EqualsAndHashCode(callSuper = false)
public class NormalUser extends User {
    
    @TableField("nickname")
    private String nickname;//对应real_name
    
    @TableField("avatar")
    private String avatar;
    
    @TableField("learning_level")
    private String learningLevel = "BEGINNER";

    @TableField("bio")
    protected String bio;//新增字段
    
    public NormalUser() {
        this.setUserType("NORMAL");
    }
} 