package com.cemenghui.news.dto;

import javax.validation.constraints.NotBlank; // 修改为javax.validation.constraints.NotBlank
import lombok.Data;

/**
 * 登录请求DTO，用于接收前端的用户名和密码。
 */
@Data
public class LoginRequest {
    @NotBlank(message = "用户名不能为空")
    private String username;
    @NotBlank(message = "密码不能为空")
    private String password;
}
