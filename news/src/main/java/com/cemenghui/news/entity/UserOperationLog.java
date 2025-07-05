package com.cemenghui.news.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("user_operation_logs")
public class UserOperationLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("operation_type")
    private String operationType;

    @TableField("resource_type")
    private String resourceType;

    @TableField("resource_id")
    private Long resourceId;

    @TableField("resource_title")
    private String resourceTitle;

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
    private Integer operationResult; // 1 for success, 0 for fail
}