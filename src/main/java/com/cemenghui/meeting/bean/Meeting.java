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
@TableName("meeting_info")
public class Meeting {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("meeting_name")
    private String meetingName; // 对应 meeting_name
    
    @TableField("start_time")
    private LocalDateTime startTime; // 对应 start_time
    
    @TableField("end_time")
    private LocalDateTime endTime; // 对应 end_time
    
    @TableField("creator")
    private String creator; // 对应 creator
    
    @TableField("meeting_content")
    private String meetingContent; // 对应 meeting_content
    
    @TableField("status")
    private Integer status; // 0: 待审核, 1: 已通过, 2: 已拒绝, 3: 已删除
    
    @TableField("reviewer")
    private String reviewer; // 审核人
    
    @TableField("review_time")
    private LocalDateTime reviewTime; // 审核时间，对应 review_time
    
    @TableField("review_comment")
    private String reviewComment; // 审核意见，对应 review_comment
    
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime; // 对应 create_time
    
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime; // 对应 update_time
    
    @TableField("image_url")
    private String imageUrl; // 会议图片URL
    
    @TableLogic
    @TableField("deleted")
    private Integer deleted; // 逻辑删除字段
} 