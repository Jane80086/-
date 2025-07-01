package com.cemenghui.course.entity;

import com.cemenghui.course.entity.ReviewStatus;
import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Enumerated;
import javax.persistence.EnumType;

/**
 * 课程审核实体
 */
@Entity
@Table(name = "review")
public class Review implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    private Long courseId;
    private Long reviewerId;
    @Enumerated(EnumType.STRING)
    private ReviewStatus status = ReviewStatus.PENDING;
    private String comment;
    private LocalDateTime reviewedAt;

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