package com.cemenghui.system.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserModifyHistory {
    private String historyId;       // 历史记录ID
    private String userId;          // 用户ID
    private String fieldName;       // 修改字段
    private String oldValue;        // 旧值
    private String newValue;        // 新值
    private String operatorId;      // 操作人ID
    private LocalDateTime modifyTime; // 修改时间

    // 手动添加getter和setter方法
    public String getHistoryId() {
        return historyId;
    }

    public void setHistoryId(String historyId) {
        this.historyId = historyId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
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

    public String getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    public LocalDateTime getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(LocalDateTime modifyTime) {
        this.modifyTime = modifyTime;
    }
}
