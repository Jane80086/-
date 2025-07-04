package com.cemenghui.system.dto;

import com.cemenghui.common.User;
import lombok.Data;

@Data
public class LoginResponseDTO {

    // 登录是否成功
    private boolean success;
    // 提示消息
    private String message;
    // 令牌（用于后续请求认证 ）
    private String token;
    // 用户信息
    private User user;

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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    /**
     * 构建登录成功响应
     * @param token    认证令牌
     * @param user 用户信息
     * @return 登录成功的响应DTO
     */
    public static LoginResponseDTO success(String token, User user) {
        LoginResponseDTO dto = new LoginResponseDTO();
        dto.setSuccess(true);
        dto.setMessage("登录成功");
        dto.setToken(token);
        dto.setUser(user);
        return dto;
    }

    /**
     * 构建登录失败响应
     * @param message 失败提示信息
     * @return 登录失败的响应DTO
     */
    public static LoginResponseDTO fail(String message) {
        LoginResponseDTO dto = new LoginResponseDTO();
        dto.setSuccess(false);
        dto.setMessage(message);
        // 失败时可按需决定是否设置 token、user，这里简单置空
        dto.setToken(null);
        dto.setUser(null);
        return dto;
    }
}