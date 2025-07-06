package com.cemenghui.course.entity;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class UserCourse {
    private Long id;
    private Long userId;
    private Long courseId;
    private LocalDateTime purchaseTime;
} 