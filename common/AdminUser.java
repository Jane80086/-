package com.cemenghui.course.common;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 管理员用户
 */
@TableName("users")
@Data
public class AdminUser {
    
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
    private String userType = "ADMIN";
    
    @TableField("status")
    private Integer status = 1;
    
    @TableField("admin_level")
    private String adminLevel = "ADMIN";
    
    @TableField("permissions")
    private String permissions;
    
    @TableField("created_time")
    private LocalDateTime createdTime = LocalDateTime.now();
    
    @TableField("updated_time")
    private LocalDateTime updatedTime = LocalDateTime.now();
    
    public void approveCourse(Long courseId) throws Exception {
        // 审核并通过课程逻辑
        onCourseApproved(courseId);
    }

    public void rejectCourse(Long courseId, String reason) throws Exception {
        // 审核并驳回课程逻辑
        onCourseRejected(courseId, reason);
    }

    public void promoteToFeatured(Long courseId) throws Exception {
        // 推荐课程至首页逻辑
        onPromoteToFeatured(courseId);
    }

    /**
     * 课程审核通过事件
     * @param courseId 课程ID
     */
    protected void onCourseApproved(Long courseId) {
        // 将课程设为公开发布状态
    }

    /**
     * 课程审核驳回事件
     * @param courseId 课程ID
     * @param reason 驳回原因
     */
    protected void onCourseRejected(Long courseId, String reason) {
        // 通知企业用户审核失败原因
    }

    /**
     * 课程推荐至首页事件
     * @param courseId 课程ID
     */
    protected void onPromoteToFeatured(Long courseId) {
        // 推送至平台首页，提升曝光量
    }
} 