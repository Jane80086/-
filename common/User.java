package com.cemenghui.course.common;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户实体类，定义用户的基本属性和行为
 */
@TableName("users")
@Data
public class User {
    
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
    private String userType;
    
    @TableField("status")
    private Integer status = 1;
    
    @TableField("created_time")
    private LocalDateTime createdTime = LocalDateTime.now();
    
    @TableField("updated_time")
    private LocalDateTime updatedTime = LocalDateTime.now();
    
    /** 是否已登录 */
    private boolean loggedIn = false;

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
