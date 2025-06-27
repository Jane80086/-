package com.cemenghui.course.common;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@DiscriminatorValue("ADMIN")
@Data
public class AdminUser extends User {
    private String adminLevel = "ADMIN";
    private String permissions;
} 