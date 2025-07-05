package com.cemenghui.course.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 课程审核历史实体类
 * 对应数据库表：course_review_history
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("course_review_history")
public class CourseReviewHistory {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 课程ID
     */
    private Long courseId;

    /**
     * 管理员ID
     */
    private Long adminId;

    /**
     * 审核动作（APPROVE/REJECT等）
     */
    private String action;

    /**
     * 审核原因
     */
    private String reason;

    /**
     * 审核时间
     */
    private LocalDateTime reviewTime;
} 