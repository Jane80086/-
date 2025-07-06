package com.cemenghui.course.service;

import com.cemenghui.course.entity.Course;
import java.util.List;

public interface CourseRecommendationService {
    List<Course> recommendCoursesForUser(Long userId, int limit);
} 