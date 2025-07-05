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

    @TableField("content")
    private String content;

    @TableField("author_id")
    private Long authorId;

    @TableField("author_name")
    private String authorName;

    @TableField("category")
    private String category;

    @TableField("tags")
    private String tags;

    @TableField("image_url")
    private String imageUrl;

    @TableField("status")
    private Integer status = 0; // 0:待审核, 1:已发布, 2:已拒绝, 3:已删除

    @TableField("view_count")
    private Integer viewCount = 0;

    @TableField("publish_time")
    private LocalDateTime publishTime;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    @TableField("deleted")
    private Integer deleted = 0;

    // 业务方法
    public boolean isOwner(Long userId) {
        return this.authorId != null && this.authorId.equals(userId);
    }

    public boolean canEdit() {
        // 根据逻辑：0:待审核, 2:已拒绝 状态允许编辑
        return this.status != null && (this.status == 0 || this.status == 2);
    }

    public boolean canDelete() {
        // 未删除状态即可删除
        return this.deleted == 0;
    }

    public void incrementViewCount() {
        this.viewCount = (this.viewCount == null ? 0 : this.viewCount) + 1;
    }

    public boolean isApproved() {
        return this.status != null && this.status == 1; // 1代表已发布
    }
    
    public boolean isPending() {
        return this.status != null && this.status == 0; // 0代表待审核
    }
    
    public boolean isRejected() {
        return this.status != null && this.status == 2; // 2代表已拒绝
    }
}
