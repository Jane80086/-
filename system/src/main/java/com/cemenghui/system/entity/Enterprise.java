package com.cemenghui.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 企业工商信息实体类，对应 enterprise 表
 */
@Data
@TableName("enterprise")
public class Enterprise {
    @TableId("enterprise_id")
    private String enterpriseId;
    @TableField("enterprise_name")
    private String enterpriseName;
    @TableField("credit_code")
    private String creditCode;
    @TableField("register_address")
    private String registerAddress;
    @TableField("legal_representative")
    private String legalRepresentative;
    @TableField("registration_date")
    private LocalDateTime registrationDate;
    @TableField("enterprise_type")
    private String enterpriseType;
    @TableField("registered_capital")
    private String registeredCapital;
    @TableField("business_scope")
    private String businessScope;
    @TableField("establishment_date")
    private LocalDateTime establishmentDate;
    @TableField("business_term")
    private String businessTerm;
    @TableField("registration_authority")
    private String registrationAuthority;
    @TableField("approval_date")
    private LocalDateTime approvalDate;
    @TableField("enterprise_status")
    private String enterpriseStatus;
    @TableField("create_time")
    private LocalDateTime createTime;
    @TableField("update_time")
    private LocalDateTime updateTime;
} 