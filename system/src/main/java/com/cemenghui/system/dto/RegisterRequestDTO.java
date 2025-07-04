package com.cemenghui.system.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class RegisterRequestDTO {

    // 企业名称，非空
    private String enterpriseName;

    // 企业联系方式，格式要求：区号 - 电话号码
    private String enterpriseContact;

    // 账号，8位数字
    @Pattern(regexp = "^\\d{8}$", message = "账号需为8位数字")
    private String account;

    // 密码，6-12位由数字和字母组成
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d]{6,12}$", message = "密码需为6-12位由数字和字母组成的字符串")
    private String password;

    // 确认密码，需和密码一致（可在业务层校验，也可在此加注解校验 ）
    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;

    // 企业类型（如 "科技企业"、"制造企业" 等 ）
    private String enterpriseType;

    // 验证码，非空
    @NotBlank(message = "验证码不能为空")
    private String verificationCode;

    // 真实姓名
    private String realName;

    // 手机号
    private String phone;

    // 邮箱
    private String email;

    // 企业ID
    private String enterpriseId;

    // 昵称
    private String nickname;

    // 手动添加getter和setter方法
    public String getEnterpriseName() {
        return enterpriseName;
    }

    public void setEnterpriseName(String enterpriseName) {
        this.enterpriseName = enterpriseName;
    }

    public String getEnterpriseContact() {
        return enterpriseContact;
    }

    public void setEnterpriseContact(String enterpriseContact) {
        this.enterpriseContact = enterpriseContact;
    }

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

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getEnterpriseType() {
        return enterpriseType;
    }

    public void setEnterpriseType(String enterpriseType) {
        this.enterpriseType = enterpriseType;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
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

    public String getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(String enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
