package com.cemenghui.system.dto;

import lombok.Data;

@Data
public class RegistResponseDTO {

    // 注册是否成功
    private boolean success;

    // 提示消息
    private String message;

    // 用户 ID（注册成功后返回）
    private String userId;
}
