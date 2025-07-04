package com.cemenghui.course.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("course_review_history")
public class CourseReviewHistory {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long courseId;
    private Long adminId;
    private String action; // APPROVED/REJECTED
    private String reason; // 拒绝理由
    private LocalDateTime reviewTime;
} 