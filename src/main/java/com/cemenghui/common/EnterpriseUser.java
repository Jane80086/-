package com.cemenghui.common;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

@TableName("users")
@Data
@EqualsAndHashCode(callSuper = false)
public class EnterpriseUser extends User {
    
    @TableField("company_name")
    private String companyName;
    
    @TableField("business_license")
    private String businessLicense;
    
    @TableField("contact_person")
    private String contactPerson;
    
    public EnterpriseUser() {
        this.setUserType("ENTERPRISE");
    }
} 