package com.system.entity;

import lombok.Data;

/**
 * 企业工商信息实体类
 */

public class Enterprise {

    // 企业ID（主键）
    private String enterpriseId;

    // 企业名称
    private String enterpriseName;

    // 统一社会信用代码
    private String creditCode;

    // 企业状态（存续、吊销、注销等）
    private String enterpriseStatus;

    // 企业类型
    private String enterpriseType;

    // 成立日期
    private String establishmentDate;

    // 法定代表人
    private String legalRepresentative;

    // 注册地址
    private String registerAddress;

    // 注册资本
    private String registeredCapital;

    // 注册机关
    private String registrationAuthority;

    // 注册日期
    private String registrationDate;

    // 批准日期
    private String approvalDate;

    // 经营范围
    private String businessScope;

    // 营业期限
    private String businessTerm;

    // 创建时间
    private String createTime;

    // 更新时间
    private String updateTime;

    /**
     * 检查企业状态是否正常
     */
    public boolean isEnterpriseActive() {
        return "存续".equals(enterpriseStatus) || "在业".equals(enterpriseStatus);
    }

    /**
     * 获取企业信息摘要
     */
    public String getEnterpriseSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("企业ID：").append(enterpriseId);
        summary.append("，企业名称：").append(enterpriseName);
        if (creditCode != null) {
            summary.append("，统一社会信用代码：").append(creditCode);
        }
        if (legalRepresentative != null) {
            summary.append("，法定代表人：").append(legalRepresentative);
        }
        return summary.toString();
    }

    /**
     * 验证统一社会信用代码格式
     */
    public boolean isValidSocialCreditCode() {
        if (creditCode == null || creditCode.trim().isEmpty()) {
            return false;
        }
        return creditCode.matches("^[0-9A-HJ-NPQRTUWXY]{2}\\d{6}[0-9A-HJ-NPQRTUWXY]{10}$");
    }

    // 手动添加getter和setter方法
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

    public String getCreditCode() {
        return creditCode;
    }

    public void setCreditCode(String creditCode) {
        this.creditCode = creditCode;
    }

    public String getEnterpriseStatus() {
        return enterpriseStatus;
    }

    public void setEnterpriseStatus(String enterpriseStatus) {
        this.enterpriseStatus = enterpriseStatus;
    }

    public String getEnterpriseType() {
        return enterpriseType;
    }

    public void setEnterpriseType(String enterpriseType) {
        this.enterpriseType = enterpriseType;
    }

    public String getEstablishmentDate() {
        return establishmentDate;
    }

    public void setEstablishmentDate(String establishmentDate) {
        this.establishmentDate = establishmentDate;
    }

    public String getLegalRepresentative() {
        return legalRepresentative;
    }

    public void setLegalRepresentative(String legalRepresentative) {
        this.legalRepresentative = legalRepresentative;
    }

    public String getRegisterAddress() {
        return registerAddress;
    }

    public void setRegisterAddress(String registerAddress) {
        this.registerAddress = registerAddress;
    }

    public String getRegisteredCapital() {
        return registeredCapital;
    }

    public void setRegisteredCapital(String registeredCapital) {
        this.registeredCapital = registeredCapital;
    }

    public String getRegistrationAuthority() {
        return registrationAuthority;
    }

    public void setRegistrationAuthority(String registrationAuthority) {
        this.registrationAuthority = registrationAuthority;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getApprovalDate() {
        return approvalDate;
    }

    public void setApprovalDate(String approvalDate) {
        this.approvalDate = approvalDate;
    }

    public String getBusinessScope() {
        return businessScope;
    }

    public void setBusinessScope(String businessScope) {
        this.businessScope = businessScope;
    }

    public String getBusinessTerm() {
        return businessTerm;
    }

    public void setBusinessTerm(String businessTerm) {
        this.businessTerm = businessTerm;
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
} 