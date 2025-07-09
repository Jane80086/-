package com.cemenghui.system.entity;

import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
public class User {

    // 账号，非空
    @NotBlank(message = "账号不能为空")
    protected String account;

    // 密码
    protected String password;

    // 角色（可用于区分企业用户、管理员等角色 ）
    protected String role;

    // 是否记住登录状态
    protected boolean isRemembered;

    // 用户 ID
    private String userId;

    // 昵称
    protected String nickname;

    // 电话
    protected String phone;

    // 邮箱
    protected String email;

    // 新增
    private String realName;

    // 已有
    private String enterpriseId;

    // 已有
    private String department;

    protected String dynamicCode;
    
    // 手动添加getter和setter方法
    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isRemembered() {
        return isRemembered;
    }

    public void setRemembered(boolean remembered) {
        isRemembered = remembered;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(String enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDynamicCode() { return dynamicCode; }
    public void setDynamicCode(String code) { this.dynamicCode = code; }
    public String sendDynamicCode(String phone) {
        String code = String.valueOf((int)((Math.random()*9+1)*100000)); // 6位验证码
        setDynamicCode(code);
        // 实际发送逻辑（此处模拟，可对接短信/邮件服务）
        System.out.println("已为用户 " + getAccount() + " 发送动态验证码：" + code + " 到手机号：" + phone);
        return code;
    }

}
