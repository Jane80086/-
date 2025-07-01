package com.cemenghui.course.entity;

import com.baomidou.mybatisplus.annotation.*;
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

    @TableField("price")
    private BigDecimal price = BigDecimal.ZERO;

    @TableField("duration")
    private Integer duration = 0;

    // @TableField("course_level")
    // private String courseLevel = "BEGINNER";

    @TableField("category")
    private String category;

    @TableField("status")
    private String status = "DRAFT";

    @TableField("cover_image")
    private String coverImage;

    @TableField("video_url")
    private String videoUrl;

    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;

    @TableField("like_count")
    private Integer likeCount = 0;

    @TableField("favorite_count")
    private Integer favoriteCount = 0;

    @TableLogic
    @TableField("deleted")
    private Integer deleted = 0;

    /**
     * 播放该课程内容
     * @throws Exception 可能抛出的异常
     */
    public void play() throws Exception {
        // 播放逻辑
    }

    /**
     * 编辑课程基本信息
     * @param title 标题
     * @param description 描述
     * @param coverImage 封面
     * @throws IllegalArgumentException 参数不合法时抛出
     */
    public void edit(String title, String description, String coverImage) throws IllegalArgumentException {
        if (title == null || title.isEmpty()) {
            throw new IllegalArgumentException("标题不能为空");
        }
        this.title = title;
        this.description = description;
        this.coverImage = coverImage;
        this.updatedTime = LocalDateTime.now();
        onCourseEdited(this.id);
    }

    /**
     * 提交课程以供管理员审核
     * @throws Exception 可能抛出的异常
     */
    public void submitForReview() throws Exception {
        this.status = "PENDING";
        this.updatedTime = LocalDateTime.now();
        onCourseSubmitted(this.id);
    }

    // 事件钩子
    /**
     * 课程创建成功事件
     * @param courseId 课程ID
     */
    public void onCourseCreated(Long courseId) {
        // 新课程入库，记录创建行为
    }

    /**
     * 课程编辑事件
     * @param courseId 课程ID
     */
    protected void onCourseEdited(Long courseId) {
        // 更新记录，触发通知
    }

    /**
     * 课程提交审核事件
     * @param courseId 课程ID
     */
    protected void onCourseSubmitted(Long courseId) {
        // 启动审核流程
    }

    // Getter/Setter 可根据需要生成
    public String getDescription() {
        return this.description;
    }
    public String getCoverImage() {
        return this.coverImage;
    }
    public String getTitle() {
        return this.title;
    }
} 