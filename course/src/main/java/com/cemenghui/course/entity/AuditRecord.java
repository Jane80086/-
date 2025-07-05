package com.cemenghui.course.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

/**
 * 统一审核记录实体类
 * 对应新的audit_records表结构
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("audit_records")
public class AuditRecord {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("resource_type")
    private String resourceType; // COURSE, MEETING, NEWS, USER
    
    @TableField("resource_id")
    private Long resourceId;
    
    @TableField("resource_name")
    private String resourceName;
    
    @TableField("action")
    private String action; // APPROVE, REJECT, MODIFY, DELETE
    
    @TableField("reviewer_id")
    private Long reviewerId;
    
    @TableField("reviewer_name")
    private String reviewerName;
    
    @TableField("status")
    private String status; // PENDING, APPROVED, REJECTED
    
    @TableField("comment")
    private String comment;
    
    @TableField("old_value")
    private String oldValue;
    
    @TableField("new_value")
    private String newValue;
    
    @TableField("audit_time")
    private LocalDateTime auditTime;
    
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    // 资源类型枚举
    public enum ResourceType {
        COURSE("课程"),
        MEETING("会议"),
        NEWS("新闻"),
        USER("用户");
        
        private final String description;
        
        ResourceType(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    // 审核动作枚举
    public enum Action {
        APPROVE("通过"),
        REJECT("拒绝"),
        MODIFY("修改"),
        DELETE("删除");
        
        private final String description;
        
        Action(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    // 审核状态枚举
    public enum Status {
        PENDING("待审核"),
        APPROVED("已通过"),
        REJECTED("已拒绝");
        
        private final String description;
        
        Status(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    /**
     * 判断是否为课程审核
     */
    public boolean isCourseAudit() {
        return ResourceType.COURSE.name().equals(this.resourceType);
    }
    
    /**
     * 判断是否为会议审核
     */
    public boolean isMeetingAudit() {
        return ResourceType.MEETING.name().equals(this.resourceType);
    }
    
    /**
     * 判断是否为新闻审核
     */
    public boolean isNewsAudit() {
        return ResourceType.NEWS.name().equals(this.resourceType);
    }
    
    /**
     * 判断是否为用户审核
     */
    public boolean isUserAudit() {
        return ResourceType.USER.name().equals(this.resourceType);
    }
    
    /**
     * 判断是否为通过操作
     */
    public boolean isApproveAction() {
        return Action.APPROVE.name().equals(this.action);
    }
    
    /**
     * 判断是否为拒绝操作
     */
    public boolean isRejectAction() {
        return Action.REJECT.name().equals(this.action);
    }
    
    /**
     * 判断是否为待审核状态
     */
    public boolean isPendingStatus() {
        return Status.PENDING.name().equals(this.status);
    }
    
    /**
     * 判断是否为已通过状态
     */
    public boolean isApprovedStatus() {
        return Status.APPROVED.name().equals(this.status);
    }
    
    /**
     * 判断是否为已拒绝状态
     */
    public boolean isRejectedStatus() {
        return Status.REJECTED.name().equals(this.status);
    }
} 