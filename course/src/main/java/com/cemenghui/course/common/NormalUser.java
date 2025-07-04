package com.cemenghui.course.common;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

/**
 * 普通用户实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class NormalUser extends User {
    
    @TableField("real_name")
    private String realName;
    
    @TableField("nickname")
    private String nickname;
    
    @TableField("avatar")
    private String avatar;
    
    @TableField("bio")
    private String bio;
    
    @TableField("learning_level")
    private String learningLevel = "BEGINNER";
    
    public NormalUser() {
        this.setUserType("NORMAL");
    }
} 