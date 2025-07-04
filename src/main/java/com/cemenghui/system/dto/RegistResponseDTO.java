package com.cemenghui.system.dto;

import lombok.Data;

@Data
public class RegistResponseDTO {

    // 注册是否成功
    private boolean success;

    // 提示消息
    private String message;

    // 用户 ID（注册成功后返回 ）
    private String userId;

    // 手动添加getter和setter方法
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}