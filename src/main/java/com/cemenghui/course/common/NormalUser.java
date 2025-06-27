package com.cemenghui.course.common;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@DiscriminatorValue("NORMAL")
@Data
public class NormalUser extends User {
    private String realName;
    private String avatar;
    private String bio;
} 