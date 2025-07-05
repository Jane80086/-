package com.cemenghui.course.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 课程审核实体
 * 保留原有功能，但映射到新的audit_records表
 */
@TableName("audit_records")
@Data
public class Review implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("resource_type")
    private String resourceType = "COURSE";
    
    @TableField("resource_id")
    private Long courseId;
    
    @TableField("resource_name")
    private String resourceName = "课程审核";
    
    @TableField("action")
    private String action = "APPROVE";
    
    @TableField("reviewer_id")
    private Long reviewerId;
    
    @TableField("reviewer_name")
    private String reviewerName;
    
    @TableField("status")
    private String status = "PENDING";
    
    @TableField("comment")
    private String comment;
    
    @TableField("audit_time")
    private LocalDateTime reviewedAt;
    
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 审核通过该课程
     * @throws Exception 可能抛出的异常
     */
    public void approve() throws Exception {
        this.status = "APPROVED";
        this.action = "APPROVE";
        this.reviewedAt = LocalDateTime.now();
        onCourseApproved(this.courseId);
    }

    /**
     * 驳回审核并附理由
     * @param reason 驳回原因
     * @throws Exception 可能抛出的异常
     */
    public void reject(String reason) throws Exception {
        this.status = "REJECTED";
        this.action = "REJECT";
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