package com.cemenghui.course.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.cemenghui.course.entity.CourseStatus;
import jakarta.validation.constraints.NotBlank;
/**
 * 课程实体类
 */
@Entity
@Table(name = "courses")
@Data
public class Course implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    @NotBlank(message = "课程标题不能为空")
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "instructor_id")
    private Long instructorId;

    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price = BigDecimal.ZERO;

    @Column(name = "duration")
    private Integer duration = 0;

    // @Column(name = "course_level")
    // private String courseLevel = "BEGINNER";

    @Column(name = "category")
    private String category;

    @Column(name = "status")
    private String status = "DRAFT";

    @Column(name = "cover_image")
    private String coverImage;

    @Column(name = "video_url")
    private String videoUrl;

    @Column(name = "created_time")
    private LocalDateTime createdTime = LocalDateTime.now();

    @Column(name = "updated_time")
    private LocalDateTime updatedTime = LocalDateTime.now();

    @Column(name = "like_count")
    private Integer likeCount = 0;

    @Column(name = "favorite_count")
    private Integer favoriteCount = 0;

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