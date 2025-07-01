package com.cemenghui.course.entity;

import com.cemenghui.course.entity.ReviewStatus;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 课程审核实体
 */
@TableName("review")
@Data
public class Review implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "review_id", type = IdType.AUTO)
    private Long reviewId;

    @TableField("course_id")
    private Long courseId;
    
    @TableField("reviewer_id")
    private Long reviewerId;
    
    @TableField("status")
    private ReviewStatus status = ReviewStatus.PENDING;
    
    @TableField("comment")
    private String comment;
    
    @TableField(value = "reviewed_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime reviewedAt;

    @TableLogic
    @TableField("deleted")
    private Integer deleted = 0;

    /**
     * 审核通过该课程
     * @throws Exception 可能抛出的异常
     */
    public void approve() throws Exception {
        this.status = ReviewStatus.APPROVED;
        this.reviewedAt = LocalDateTime.now();
        onCourseApproved(this.courseId);
    }

    /**
     * 驳回审核并附理由
     * @param reason 驳回原因
     * @throws Exception 可能抛出的异常
     */
    public void reject(String reason) throws Exception {
        this.status = ReviewStatus.REJECTED;
        this.comment = reason;
        this.reviewedAt = LocalDateTime.now();
        onCourseRejected(this.courseId, reason);
    }

    /**
     * 审核通过事件
     * @param courseId 课程ID
     */
    protected void onCourseApproved(Long courseId) {
        // 设置状态为已发布，通知企业用户
    }

    /**
     * 审核驳回事件
     * @param courseId 课程ID
     * @param reason 驳回原因
     */
    protected void onCourseRejected(Long courseId, String reason) {
        // 通知企业用户驳回原因
    }

    // Getter/Setter 可根据需要生成
} 