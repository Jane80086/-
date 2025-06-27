package com.cemenghui.course.common;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户实体类，定义用户的基本属性和行为
 */
@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "user_type", discriminatorType = DiscriminatorType.STRING)
@Data
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    protected Long id;
    
    @Column(name = "username", nullable = false, unique = true)
    protected String username;
    
    @Column(name = "password", nullable = false)
    protected String password;
    
    @Column(name = "email", nullable = false, unique = true)
    protected String email;
    
    @Column(name = "phone")
    protected String phone;
    
    @Column(name = "status")
    protected Integer status = 1;
    
    @Column(name = "created_time")
    protected LocalDateTime createdTime = LocalDateTime.now();
    
    @Column(name = "updated_time")
    protected LocalDateTime updatedTime = LocalDateTime.now();
    
    /** 是否已登录 */
    @Transient
    protected boolean loggedIn = false;

    /**
     * 登录校验方法，校验通过则返回 true，否则抛出 Exception
     * @param password 密码
     * @return 是否登录成功
     * @throws Exception 登录校验失败时抛出
     */
    public boolean login(String password) throws Exception {
        // 登录逻辑（示例，实际应有密码校验）
        if (password == null || password.isEmpty()) {
            throw new Exception("密码不能为空");
        }
        this.loggedIn = true;
        onLoginSuccess(this.id);
        return true;
    }

    // 事件钩子
    /**
     * 登录成功事件
     * @param userId 用户ID
     */
    protected void onLoginSuccess(Long userId) {
        // 记录用户状态变更或登录日志
    }

    /**
     * 课程浏览事件
     * @param courseId 课程ID
     */
    protected void onCourseViewed(Long courseId) {
        // 记录用户浏览行为
    }

    /**
     * 课程搜索事件
     * @param keyword 搜索关键词
     */
    protected void onCourseSearch(String keyword) {
        // 用于关键词热度统计
    }
}
