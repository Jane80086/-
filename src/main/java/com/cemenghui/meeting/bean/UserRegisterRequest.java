package com.cemenghui.meeting.bean;

import lombok.Data;

@Data
public class UserRegisterRequest {
    private String username;
    private String password;
    private String realName;
    private String email;
    private String phone;
    private String userType; // ADMIN, ENTERPRISE, NORMAL
} 