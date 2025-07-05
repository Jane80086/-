package com.cemenghui.news.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("user_view_logs")
public class UserViewLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("resource_type")
    private String resourceType;

    @TableField("resource_id")
    private Long newsId; // Mapped from resource_id for NEWS context

    @TableField("resource_title")
    private String resourceTitle;

    @TableField("view_time")
    private LocalDateTime viewTime;

    @TableField("ip_address")
    private String ipAddress;

    @TableField("user_agent")
    private String userAgent;

    @TableField("session_id")
    private String sessionId;
}