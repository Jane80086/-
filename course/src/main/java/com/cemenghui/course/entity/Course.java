package com.cemenghui.course.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.cemenghui.course.entity.CourseStatus;
import javax.validation.constraints.NotBlank;
/**
 * 课程实体类
 */
@TableName("courses")
@Data
public class Course implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("title")
    @NotBlank(message = "课程标题不能为空")
    private String title;

    @TableField("description")
    private String description;

    @TableField("instructor_id")
    private Long instructorId;

    @TableField("instructor_name")
    private String instructorName;

    @TableField("price")
    private BigDecimal price = BigDecimal.ZERO;

    @TableField("duration")
    private Integer duration = 0;

    @TableField("level")
    private String level = "BEGINNER";

    @TableField("category")
    private String category;

    @TableField("status")
    private Integer status = 0;

    @TableField("image_url")
    private String imageUrl;

    @TableField("video_url")
    private String videoUrl;

    @TableField("view_count")
    private Integer viewCount = 0;

    @TableField("rating")
    private BigDecimal rating = BigDecimal.ZERO;

    @TableField("like_count")
    private Integer likeCount = 0;

    @TableField("favorite_count")
    private Integer favoriteCount = 0;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    @TableField("deleted")
    private Integer deleted = 0;

    // 无参构造函数
    public Course() {}

    /**
     * 播放该课程内容
     * @throws Exception 可能抛出的异常
     */
    @JsonIgnore
    public void play() throws Exception {
        // 播放逻辑
    }

    /**
     * 编辑课程基本信息
     * @param title 标题
     * @param description 描述
     * @param imageUrl 图片URL
     * @throws IllegalArgumentException 参数不合法时抛出
     */
    @JsonIgnore
    public void edit(String title, String description, String imageUrl) throws IllegalArgumentException {
        if (title == null || title.isEmpty()) {
            throw new IllegalArgumentException("标题不能为空");
        }
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.updateTime = LocalDateTime.now();
        onCourseEdited(this.id);
    }

    /**
     * 提交课程以供管理员审核
     * @throws Exception 可能抛出的异常
     */
    @JsonIgnore
    public void submitForReview() throws Exception {
        this.status = 0; // 待审核
        this.updateTime = LocalDateTime.now();
        onCourseSubmitted(this.id);
    }

    // 事件钩子
    /**
     * 课程创建成功事件
     * @param courseId 课程ID
     */
    @JsonIgnore
    public void onCourseCreated(Long courseId) {
        // 新课程入库，记录创建行为
    }

    /**
     * 课程编辑事件
     * @param courseId 课程ID
     */
    @JsonIgnore
    protected void onCourseEdited(Long courseId) {
        // 更新记录，触发通知
    }

    /**
     * 课程提交审核事件
     * @param courseId 课程ID
     */
    @JsonIgnore
    protected void onCourseSubmitted(Long courseId) {
        // 启动审核流程
    }

    // Getter/Setter 可根据需要生成
    public String getDescription() {
        return this.description;
    }
    public String getImageUrl() {
        return this.imageUrl;
    }
    public String getTitle() {
        return this.title;
    }
} 