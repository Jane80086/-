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
    private Integer status; // 0: 待审核, 1: 已发布, 2: 已拒绝

    @TableField("view_count")
    private Integer viewCount = 0;

    @TableLogic
    @TableField("is_deleted")
    private Integer isDeleted = 0;

    @TableField("audit_comment")
    private String auditComment;

    @TableField("audit_user_id")
    private Long auditUserId;

    @TableField("audit_time")
    private LocalDateTime auditTime;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    // 业务方法
    public boolean isOwner(Long currentUserId) {
        return this.userId != null && this.userId.equals(currentUserId);
    }

    public boolean canEdit() {
        // According to the new logic, only pending (0) or rejected (2) news can be edited.
        return this.status != null ;
    }

    public boolean canDelete() {
        // Can be deleted if not already deleted.
        return this.isDeleted == 0;
    }

    public void incrementViewCount() {
        this.viewCount = (this.viewCount == null ? 0 : this.viewCount) + 1;
    }

    public boolean isPublished() {
        return this.status != null && this.status == 1;
    }

    public boolean isPending() {
        return this.status != null && this.status == 0;
    }

    public boolean isRejected() {
        return this.status != null && this.status == 2;
    }
}