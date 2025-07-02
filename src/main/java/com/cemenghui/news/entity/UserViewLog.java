package com.cemenghui.news.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("user_view_log")
public class UserViewLog {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("news_id")
    private Long newsId;

    @TableField("ip_address")
    private String ipAddress;

    @TableField("user_agent")
    private String userAgent;

    @TableField("session_id")
    private String sessionId;

    @TableField(value = "view_time", fill = FieldFill.INSERT)
    private LocalDateTime viewTime;
}