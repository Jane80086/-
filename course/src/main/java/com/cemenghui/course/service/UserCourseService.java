package com.cemenghui.course.service;

import com.cemenghui.course.entity.UserCourse;
import java.util.List;

public interface UserCourseService {
    boolean purchaseCourse(Long userId, Long courseId);
    boolean deletePurchasedCourse(Long userId, Long courseId);
    List<UserCourse> getPurchasedCourses(Long userId);
} 