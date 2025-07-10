package com.system.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * 管理员用户实体类，继承自 User 类
 */
@EqualsAndHashCode(callSuper = true)
public class AdminUser extends User {

    // 管理员专属属性
    private String userId;                  // 管理员唯一标识
    private String realName;                 // 真实姓名
    private String department;               // 所属部门
    private Boolean isSuperAdmin;            // 是否超级管理员（默认 false）

    // 管理员权限集合（通过角色关联获取，也可直接存储）
    private Set<String> permissions;

    /**
     * 发送动态验证码（重写父类方法或扩展功能）
     * @param phone 接收验证码的手机号
     * @return 验证码字符串
     */
    public String sendDynamicCode(String phone) {
        String dynamicCode = generateRandomCode(6); // 生成6位动态码
        System.out.println("已为管理员 " + realName + " 发送动态验证码：" + dynamicCode + " 到手机号：" + phone);
        // 实际项目中调用短信接口或消息推送服务
        return dynamicCode;
    }

    /**
     * 生成随机动态验证码
     * @param length 验证码长度
     * @return 随机生成的数字验证码
     */
    private String generateRandomCode(int length) {
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < length; i++) {
            code.append((int) (Math.random() * 10));
        }
        return code.toString();
    }

    /**
     * 校验管理员权限
     * @param permissionCode 权限码
     * @return 是否拥有该权限
     */
    public boolean hasPermission(String permissionCode) {
        return permissions != null && permissions.contains(permissionCode);
    }

    private User registerThirdPartyUser(String platform, String openId) {
        // 实现自动注册逻辑
        return null; // Placeholder return, actual implementation needed
    }

    @Override
    public String getUserId() {
        return userId;
    }

    @Override
    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String getRealName() {
        return realName;
    }

    @Override
    public void setRealName(String realName) {
        this.realName = realName;
    }

    @Override
    public String getDepartment() {
        return department;
    }

    @Override
    public void setDepartment(String department) {
        this.department = department;
    }

    public Boolean getSuperAdmin() {
        return isSuperAdmin;
    }

    public void setSuperAdmin(Boolean superAdmin) {
        isSuperAdmin = superAdmin;
    }

}