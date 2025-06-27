package com.cemenghui.course.common;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@DiscriminatorValue("ENTERPRISE")
@Data
public class EnterpriseUser extends User {
    private String companyName;
    private String businessLicense;
    private String contactPerson;
    private String contactPhone;
    private String companyAddress;
} 