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
@TableName("meeting_operation_logs")
public class MeetingOperationLog {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("user_id")
    private Long userId;
    
    @TableField("user_name")
    private String userName;
    
    @TableField("operation_type")
    private String operationType;
    
    @TableField("meeting_id")
    private Long meetingId;
    
    @TableField("meeting_name")
    private String meetingName;
    
    @TableField("operation_desc")
    private String operationDesc;
    
    @TableField("old_value")
    private String oldValue;
    
    @TableField("new_value")
    private String newValue;
    
    @TableField("ip_address")
    private String ipAddress;
    
    @TableField("operation_time")
    private LocalDateTime operationTime;
    
    @TableField("operation_result")
    private Integer operationResult = 1;
} 