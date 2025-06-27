package com.cemenghui.course.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

/**
 * 首页推荐课程实体
 */
@Entity
@Table(name = "featured_course")
public class FeaturedCourse implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long featuredCourseId;
    private Long courseId;
    private Long promotedBy;
    private LocalDateTime promotedAt = LocalDateTime.now();
    private int priority = 0;

    /**
     * 推送该课程至首页
     * @param courseId 课程ID
     * @throws Exception 可能抛出的异常
     */
    public void promote(Long courseId) throws Exception {
        this.courseId = courseId;
        this.promotedAt = LocalDateTime.now();
        onPromotedToHomepage(courseId);
    }

    /**
     * 从推荐中撤下课程
     * @param courseId 课程ID
     * @throws Exception 可能抛出的异常
     */
    public void removeFromFeatured(Long courseId) throws Exception {
        // 撤下推荐逻辑
    }

    /**
     * 推送至首页事件
     * @param courseId 课程ID
     */
    protected void onPromotedToHomepage(Long courseId) {
        // 增加曝光，提升用户点击率
    }

    // Getter/Setter 可根据需要生成
} 