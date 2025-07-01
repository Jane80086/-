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
    private Long meetingId; // 对应 meeting_id
    
    @TableField("meeting_name")
    private String meetingName; // 对应 meeting_name
    
    @TableField("creator")
    private String creator; // 对应 creator
    
    @TableField("reviewer")
    private String reviewer; // 对应 reviewer
    
    @TableField("status")
    private Integer status; // 对应 status
    
    @TableField("review_comment")
    private String reviewComment; // 对应 review_comment
    
    @TableField("review_time")
    private LocalDateTime reviewTime; // 对应 review_time
} 