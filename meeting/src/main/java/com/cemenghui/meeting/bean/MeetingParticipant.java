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
@TableName("meeting_participants")
public class MeetingParticipant {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("meeting_id")
    private Long meetingId;
    
    @TableField("user_id")
    private Long userId;
    
    @TableField("user_name")
    private String userName;
    
    @TableField("join_time")
    private LocalDateTime joinTime;
    
    @TableField("leave_time")
    private LocalDateTime leaveTime;
    
    @TableField("status")
    private Integer status = 1; // 0: 未参加, 1: 已参加, 2: 已请假
    
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
} 