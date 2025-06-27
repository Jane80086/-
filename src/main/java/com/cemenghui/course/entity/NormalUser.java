package com.cemenghui.course.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 普通用户
 */
@Entity
@Table(name = "normal_users")
@Data
public class NormalUser {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "real_name")
    private String realName;
    
    @Column(name = "avatar")
    private String avatar;
    
    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;
    
    @Column(name = "created_time")
    private LocalDateTime createdTime = LocalDateTime.now();
} 