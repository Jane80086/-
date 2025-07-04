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
    private String image; // 之前是 imagePath，现在是 image

    @TableField("content")
    private String content;

    @TableField("summary")
    private String summary;

    @TableField("author")
    private String author;

    @TableField("user_id")
    private Long userId;

    @TableField("status")
    private Integer status = 0; // 0:待审核, 1:审核通过, 2:审核拒绝, (可能还有 3:已下架/禁用)

    @TableField("view_count")
    private Integer viewCount = 0;

    @TableField("is_deleted")
    @TableLogic
    private Integer isDeleted = 0;

    @TableField("audit_comment")
    private String auditComment;

    @TableField("audit_user_id")
    private Long auditUserId;

    @TableField("audit_time")
    private LocalDateTime auditTime;

    @TableField("deleted_time")
    private LocalDateTime deletedTime;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    // 业务方法
    public boolean isOwner(Long userId) {
        return this.userId != null && this.userId.equals(userId);
    }

    public boolean canEdit() {
        // 根据你提供的逻辑：0:待审核, 2:审核拒绝, 3:已下架/禁用 状态允许编辑
        return this.status != null && (this.status == 0 || this.status == 2 || this.status == 3);
    }

    public boolean canDelete() {
        // 未删除状态即可删除
        return this.isDeleted == 0;
    }

    public void incrementViewCount() {
        this.viewCount = (this.viewCount == null ? 0 : this.viewCount) + 1;
    }

    public boolean isApproved() {
        return this.status != null && this.status == 1; // 1代表审核通过
    }
}
