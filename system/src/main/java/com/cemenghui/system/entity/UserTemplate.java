package com.cemenghui.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

@Data
@TableName("system_user_template")
public class UserTemplate {
    
    @TableId("template_id")
    private String templateId;
    
    @TableField("template_name")
    private String templateName;
    
    @TableField("enterprise_type")
    private String enterpriseType;
    
    @TableField("role")
    private String role;
    
    @TableField("default_values")
    private String defaultValues; // JSON格式存储默认属性值
    
    @TableField("permissions")
    private String permissions; // JSON格式存储预设权限集合
    
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

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
        // Implementation needed
        return null;
    }

    public void setDefaultValues(Map<String, Object> defaultValues) {
        // Implementation needed
    }

    public Set<String> getPermissions() {
        // Implementation needed
        return null;
    }

    public void setPermissions(Set<String> permissions) {
        // Implementation needed
    }
}
