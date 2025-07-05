package com.cemenghui.news.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("user_view_log")
public class UserViewLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("news_id")
    private Long newsId;

    @TableField("view_time")
    private LocalDateTime viewTime;

    @TableField("ip")
    private String ip;

    @TableField("user_agent")
    private String userAgent;
}