package com.cemenghui.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("system_user_modify_history")
public class UserModifyHistory {
    @TableId("history_id")
    private String historyId;       // 历史记录ID
    @TableField("user_id")
    private Long userId;          // 用户ID
    @TableField("field_name")
    private String fieldName;       // 修改字段
    @TableField("old_value")
    private String oldValue;        // 旧值
    @TableField("new_value")
    private String newValue;        // 新值
    @TableField("operator_id")
    private Long operatorId;      // 操作人ID
    @TableField("modify_time")
    private LocalDateTime modifyTime; // 修改时间

    // 手动添加getter和setter方法
    public String getHistoryId() {
        return historyId;
    }

    public void setHistoryId(String historyId) {
        this.historyId = historyId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public LocalDateTime getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(LocalDateTime modifyTime) {
        this.modifyTime = modifyTime;
    }
}
