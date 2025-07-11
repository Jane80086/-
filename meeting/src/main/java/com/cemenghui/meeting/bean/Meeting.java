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
@TableName("meetings")
public class Meeting {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("meeting_name")
    private String meetingName;
    
    @TableField("start_time")
    private LocalDateTime startTime;
    
    @TableField("end_time")
    private LocalDateTime endTime;
    
    @TableField("creator_id")
    private Long creatorId;
    
    @TableField("creator_name")
    private String creatorName;
    
    @TableField("meeting_content")
    private String meetingContent;
    
    @TableField("status")
    private Integer status = 0; // 0: 待审核, 1: 已通过, 2: 已拒绝, 3: 已删除
    
    @TableField("reviewer_id")
    private Long reviewerId;
    
    @TableField("reviewer_name")
    private String reviewerName;
    
    @TableField("review_time")
    private LocalDateTime reviewTime;
    
    @TableField("review_comment")
    private String reviewComment;
    
    @TableField("image_url")
    private String imageUrl;
    
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    @TableField("deleted")
    private Integer deleted = 0;
} 