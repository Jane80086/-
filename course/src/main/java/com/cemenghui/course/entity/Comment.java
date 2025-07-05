package com.cemenghui.course.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@TableName("course_comments")
@Data
public class Comment implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("course_id")
    private Long courseId;

    @TableField("user_id")
    private Long userId;

    @TableField("user_name")
    private String userName;

    @TableField("content")
    private String content;

    @TableField("rating")
    private Integer rating;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField("like_count")
    private Integer likeCount = 0;

    @TableField("status")
    private String status = "NORMAL"; // NORMAL/REVIEWED/REJECTED

    @TableLogic
    @TableField("deleted")
    private Integer deleted = 0;
} 