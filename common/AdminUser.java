package com.cemenghui.course.common;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 管理员用户
 */
@Entity
@DiscriminatorValue("ADMIN")
@Data
public class AdminUser extends User {
    
    @Column(name = "admin_level")
    private String adminLevel = "ADMIN";
    
    @Column(name = "permissions", columnDefinition = "TEXT")
    private String permissions;
    
    @Column(name = "created_time")
    private LocalDateTime createdTime = LocalDateTime.now();
    
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