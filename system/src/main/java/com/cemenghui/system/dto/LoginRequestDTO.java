package com.cemenghui.system.dto;

import lombok.Data;

@Data
public class LoginRequestDTO {

    // 账号
    private String account;

    // 密码
    private String password;

    // 是否记住登录状态
    private boolean rememberMe;

    // 验证码
    private String verificationCode;

    // 第三方登录令牌（如微信登录的 token ）
    private String thirdPartyToken;

    // 动态验证码（超级管理员登录专用）
    private String dynamicCode;
}
