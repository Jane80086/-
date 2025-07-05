package com.cemenghui.course.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

/**
 * 统一历史记录实体类
 * 对应新的history_records表结构
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("history_records")
public class HistoryRecord {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("resource_type")
    private String resourceType; // COURSE, USER, ENTERPRISE
    
    @TableField("resource_id")
    private Long resourceId;
    
    @TableField("action")
    private String action; // VIEW, MODIFY, DELETE
    
    @TableField("user_id")
    private Long userId;
    
    @TableField("old_value")
    private String oldValue;
    
    @TableField("new_value")
    private String newValue;
    
    @TableField("record_time")
    private LocalDateTime recordTime;
    
    // 资源类型枚举
    public enum ResourceType {
        COURSE("课程"),
        USER("用户"),
        ENTERPRISE("企业");
        
        private final String description;
        
        ResourceType(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    // 操作类型枚举
    public enum Action {
        VIEW("查看"),
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
    
    /**
     * 判断是否为课程历史记录
     */
    public boolean isCourseHistory() {
        return ResourceType.COURSE.name().equals(this.resourceType);
    }
    
    /**
     * 判断是否为用户历史记录
     */
    public boolean isUserHistory() {
        return ResourceType.USER.name().equals(this.resourceType);
    }
    
    /**
     * 判断是否为企业历史记录
     */
    public boolean isEnterpriseHistory() {
        return ResourceType.ENTERPRISE.name().equals(this.resourceType);
    }
    
    /**
     * 判断是否为查看操作
     */
    public boolean isViewAction() {
        return Action.VIEW.name().equals(this.action);
    }
    
    /**
     * 判断是否为修改操作
     */
    public boolean isModifyAction() {
        return Action.MODIFY.name().equals(this.action);
    }
    
    /**
     * 判断是否为删除操作
     */
    public boolean isDeleteAction() {
        return Action.DELETE.name().equals(this.action);
    }
} 