package com.cemenghui.news.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("news")
public class News {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("title")
    private String title;

    @TableField("image")
    private String image;

    @TableField("content")
    private String content;

    @TableField("summary")
    private String summary;

    @TableField("author")
    private String author;

    @TableField("user_id")
    private Long userId;

    @TableField("status")
    private Integer status = 0;

    @TableField("view_count")
    private Integer viewCount = 0;

    @TableField("is_deleted")
    private Integer isDeleted = 0;

    @TableField("audit_comment")
    private String auditComment;

    @TableField("audit_user_id")
    private Long auditUserId;

    @TableField("audit_time")
    private LocalDateTime auditTime;

    @TableField("deleted_time")
    private LocalDateTime deletedTime;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;

    // 业务方法
    public boolean isOwner(Long userId) {
        return this.userId != null && this.userId.equals(userId);
    }

    public boolean canEdit() {
        return this.status != null && (this.status == 0 || this.status == 2 || this.status == 3);
    }

    public boolean canDelete() {
        return this.isDeleted == 0;
    }

    public void incrementViewCount() {
        this.viewCount = (this.viewCount == null ? 0 : this.viewCount) + 1;
    }

    public boolean isApproved() {
        return this.status != null && this.status == 1;
    }
}
