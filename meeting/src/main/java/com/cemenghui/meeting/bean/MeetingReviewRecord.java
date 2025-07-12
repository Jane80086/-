package com.cemenghui.meeting.bean;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("meeting_review_record")
public class MeetingReviewRecord {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("meeting_id")
    private Long meetingId;
    
    @TableField("meeting_name")
    private String meetingName;
    
    @TableField("creator_id")
    private Long creatorId;
    
    @TableField("creator_name")
    private String creatorName;
    
    @TableField("reviewer_id")
    private Long reviewerId;
    
    @TableField("reviewer_name")
    private String reviewerName;
    
    @TableField("status")
    private Integer status;
    
    @TableField("review_comment")
    private String reviewComment;
    
    @TableField("review_time")
    private LocalDateTime reviewTime;
    
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
} 