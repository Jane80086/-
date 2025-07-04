package com.cemenghui.system.entity;

import lombok.Data;

import java.util.Map;
import java.util.Set;

public class UserTemplate {
    private String templateId;      // 模板ID
    private String templateName;    // 模板名称
    private String enterpriseType;  // 适用企业类型
    private String role;            // 默认角色
    private Map<String, Object> defaultValues; // 默认属性值
    private Set<String> permissions;// 预设权限集合

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getEnterpriseType() {
        return enterpriseType;
    }

    public void setEnterpriseType(String enterpriseType) {
        this.enterpriseType = enterpriseType;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Map<String, Object> getDefaultValues() {
        return defaultValues;
    }

    public void setDefaultValues(Map<String, Object> defaultValues) {
        this.defaultValues = defaultValues;
    }

    public Set<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<String> permissions) {
        this.permissions = permissions;
    }
}
