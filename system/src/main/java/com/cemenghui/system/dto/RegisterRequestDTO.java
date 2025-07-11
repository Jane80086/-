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

    // 部门（管理员注册时可用）
    private String department;

    // 验证码UUID
    private String uuid;

    public String getUuid() {
        return uuid;
    }
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
