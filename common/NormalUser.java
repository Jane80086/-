package com.cemenghui.course.common;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 普通用户
 */
@Entity
@DiscriminatorValue("NORMAL")
@Data
public class NormalUser extends User {
    
    @Column(name = "real_name")
    private String realName;
    
    @Column(name = "avatar")
    private String avatar;
    
    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;
} 