package com.cemenghui.course.common;

import com.cemenghui.course.entity.Course;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 企业用户
 */
@Entity
@DiscriminatorValue("ENTERPRISE")
@Data
public class EnterpriseUser extends User {
    
    @Column(name = "company_name", nullable = false)
    private String companyName;
    
    @Column(name = "business_license")
    private String businessLicense;
    
    @Column(name = "contact_person")
    private String contactPerson;
    
    @Column(name = "contact_phone")
    private String contactPhone;
    
    @Column(name = "company_address", columnDefinition = "TEXT")
    private String companyAddress;
    
    @Column(name = "created_time")
    private LocalDateTime createdTime = LocalDateTime.now();
    
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