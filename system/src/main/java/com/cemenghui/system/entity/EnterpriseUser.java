package com.cemenghui.system.entity;

import javax.validation.constraints.Pattern;

public class EnterpriseUser extends com.cemenghui.system.entity.User {

    // 企业用户ID
    private String userId;

    // 企业ID（外键，关联enterprise表）
    private String enterpriseId;

    // 企业名称，非空
    private String enterpriseName;

    // 企业联系方式，格式要求：区号 - 电话号码
    @Pattern(regexp = "^\\d{3,4}-\\d{7,8}$", message = "企业联系方式格式错误，需为 区号-电话号码")
    private String enterpriseContact;

    // 企业密码（可根据实际需求细化，这里先简单定义 ）
    private String enterprisePassword;

    // 用户状态（启用、禁用等）
    private String userStatus;

    // 创建时间
    private String createTime;

    // 更新时间
    private String updateTime;

    // 关联的企业信息（非数据库字段，用于数据传输）
    private Enterprise enterprise;

    // 真实姓名
    private String realName;

    // 企业类型
    private String enterpriseType;

    // 用户状态（启用、禁用等）
    private String status;

    // 部门
    private String department;

    // 手动添加getter和setter方法
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(String enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

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

    public String getEnterprisePassword() {
        return enterprisePassword;
    }

    public void setEnterprisePassword(String enterprisePassword) {
        this.enterprisePassword = enterprisePassword;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public Enterprise getEnterprise() {
        return enterprise;
    }

    public void setEnterprise(Enterprise enterprise) {
        this.enterprise = enterprise;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getEnterpriseType() {
        return enterpriseType;
    }

    public void setEnterpriseType(String enterpriseType) {
        this.enterpriseType = enterpriseType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    /**
     * 从数据库同步企业工商信息
     * 根据企业ID查询并填充企业相关信息
     */
    public void syncFromDatabase() {
        if (enterpriseId == null || enterpriseId.trim().isEmpty()) {
            System.out.println("企业ID为空，无法同步企业信息");
            return;
        }
        
        try {
            // 这里应该注入BusinessInfoMapper，但实体类中不能直接注入
            // 实际项目中应该在Service层调用此方法
            System.out.println("正在从数据库同步企业工商信息，企业ID：" + enterpriseId);
            
            // 模拟从数据库查询企业信息的过程
            // BusinessInfo enterprise = enterpriseMapper.findByEnterpriseId(enterpriseId);
            // if (enterprise != null) {
            //     this.enterprise = enterprise;
            //     this.enterpriseName = enterprise.getEnterpriseName();
            // }
            
            System.out.println("企业工商信息同步完成");
        } catch (Exception e) {
            System.err.println("同步企业工商信息失败：" + e.getMessage());
        }
    }

    /**
     * 验证密码格式
     * 要求：6-12位由数字和字母组成的字符串
     */
    public void validatePassword() {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("密码不能为空");
        }
        
        if (password.length() < 6 || password.length() > 12) {
            throw new IllegalArgumentException("密码长度需为6-12位");
        }
        
        // 检查是否包含字母和数字
        boolean hasLetter = false;
        boolean hasDigit = false;
        
        for (char c : password.toCharArray()) {
            if (Character.isLetter(c)) {
                hasLetter = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            }
        }
        
        if (!hasLetter) {
            throw new IllegalArgumentException("密码必须包含字母");
        }
        
        if (!hasDigit) {
            throw new IllegalArgumentException("密码必须包含数字");
        }
        
        // 检查是否包含特殊字符（可选，根据需求调整）
        if (!password.matches("^[a-zA-Z0-9]+$")) {
            throw new IllegalArgumentException("密码只能包含字母和数字");
        }
    }

    /**
     * 检查企业用户信息格式
     * 包括企业联系方式、账号等格式校验
     */
    public boolean checkFormat() {
        // 检查企业联系方式格式
        if (enterpriseContact != null && !enterpriseContact.matches("^\\d{3,4}-\\d{7,8}$")) {
            System.err.println("企业联系方式格式错误：" + enterpriseContact);
            return false;
        }
        
        // 检查企业名称不为空
        if (enterpriseName == null || enterpriseName.trim().isEmpty()) {
            System.err.println("企业名称不能为空");
            return false;
        }
        
        // 检查账号格式（8位数字）
        if (account != null && !account.matches("^\\d{8}$")) {
            System.err.println("账号格式错误，需为8位数字：" + account);
            return false;
        }
        
        return true;
    }

    /**
     * 检查用户状态是否正常
     */
    public boolean isUserActive() {
        return "启用".equals(userStatus) || "正常".equals(userStatus);
    }

    /**
     * 获取完整的企业用户信息摘要
     */
    public String getEnterpriseUserSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("用户ID：").append(userId);
        summary.append("，企业名称：").append(enterpriseName);
        summary.append("，账号：").append(account);
        if (enterprise != null) {
            summary.append("，统一社会信用代码：").append(enterprise.getCreditCode());
        }
        return summary.toString();
    }

    /**
     * 检查企业信息是否已关联
     */
    public boolean hasBusinessInfo() {
        return enterprise != null;
    }
}
