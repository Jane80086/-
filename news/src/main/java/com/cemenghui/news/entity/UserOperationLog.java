package com.cemenghui.news.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("user_operation_log")
public class UserOperationLog {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId; // 关联main-app users表

    @TableField("operation_type")
    private String operationType;

    @TableField("resource_id")
    private Long resourceId;

    @TableField("operation_desc")
    private String operationDesc;

    @TableField("operation_time")
    private LocalDateTime operationTime;

    @TableField("ip")
    private String ip;
}