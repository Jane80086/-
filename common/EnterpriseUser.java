package com.cemenghui.course.common;

import com.cemenghui.course.entity.Course;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 企业用户
 */
@TableName("users")
@Data
public class EnterpriseUser {
    
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
    private String userType = "ENTERPRISE";
    
    @TableField("status")
    private Integer status = 1;
    
    @TableField("company_name")
    private String companyName;
    
    @TableField("business_license")
    private String businessLicense;
    
    @TableField("contact_person")
    private String contactPerson;
    
    @TableField("contact_phone")
    private String contactPhone;
    
    @TableField("company_address")
    private String companyAddress;
    
    @TableField("created_time")
    private LocalDateTime createdTime = LocalDateTime.now();
    
    @TableField("updated_time")
    private LocalDateTime updatedTime = LocalDateTime.now();
    
    public Course createCourse(String title, String desc) throws Exception {
        // 创建课程逻辑
        Course course = new Course();
        course.edit(title, desc, null);
        course.onCourseCreated(course.getId());
        return course;
    }

    public Course editCourse(Long courseId, String newTitle) throws Exception {
        // 编辑已有课程逻辑
        Course course = findCourseById(courseId);
        course.edit(newTitle, course.getDescription(), course.getCoverImage());
        onCourseEdited(courseId);
        return course;
    }

    public void submitCourseForReview(Long courseId) throws Exception {
        // 提交课程审核逻辑
        Course course = findCourseById(courseId);
        course.submitForReview();
        onCourseSubmitted(courseId);
    }

    protected void onCourseSubmitted(Long courseId) {
        // 启动课程审核流程
    }

    protected void onCourseEdited(Long courseId) {
        // 记录课程内容更新行为
    }

    // 假设有此方法用于查找课程
    private Course findCourseById(Long courseId) {
        // 实际应从数据库或集合中查找
        return new Course();
    }
} 