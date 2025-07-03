package com.cemenghui.course.common;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

/**
 * 企业用户实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class EnterpriseUser extends User {
    
    @TableField("company_name")
    private String companyName;
    
    @TableField("business_license")
    private String businessLicense;
    
    @TableField("contact_person")
    private String contactPerson;
    
    @TableField("contact_phone")
    private String contactPhone;
    
    @TableField("company_address")
    private String companyAddress;
    
    public EnterpriseUser() {
        this.setUserType("ENTERPRISE");
    }
} 