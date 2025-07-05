package com.cemenghui.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 企业工商信息实体类
 */
@Data
@TableName("system_enterprise")
public class Enterprise {

    @TableId("enterprise_id")
    private String enterpriseId;

    @TableField("enterprise_name")
    private String enterpriseName;

    @TableField("credit_code")
    private String creditCode;

    @TableField("enterprise_status")
    private String enterpriseStatus;

    @TableField("enterprise_type")
    private String enterpriseType;

    @TableField("establishment_date")
    private String establishmentDate;

    @TableField("legal_representative")
    private String legalRepresentative;

    @TableField("register_address")
    private String registerAddress;

    @TableField("registered_capital")
    private String registeredCapital;

    @TableField("registration_authority")
    private String registrationAuthority;

    @TableField("registration_date")
    private String registrationDate;

    @TableField("approval_date")
    private String approvalDate;

    @TableField("business_scope")
    private String businessScope;

    @TableField("business_term")
    private String businessTerm;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

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

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
} 