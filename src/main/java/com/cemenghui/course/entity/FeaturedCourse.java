package com.cemenghui.course.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 首页推荐课程实体
 */
@TableName("featured_course")
@Data
public class FeaturedCourse implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "featured_course_id", type = IdType.AUTO)
    private Long featuredCourseId;
    
    @TableField("course_id")
    private Long courseId;
    
    @TableField("promoted_by")
    private Long promotedBy;
    
    @TableField(value = "promoted_at", fill = FieldFill.INSERT)
    private LocalDateTime promotedAt;
    
    @TableField("priority")
    private int priority = 0;

    @TableLogic
    @TableField("deleted")
    private Integer deleted = 0;

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