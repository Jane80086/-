package com.cemenghui.course.service;

import java.util.Map;

public interface CourseOptimizationService {
    /**
     * AI优化课程标题
     */
    String optimizeTitle(String originalTitle, String category, String description);
    
    /**
     * AI优化课程简介
     */
    String optimizeDescription(String originalDescription, String category, String title);
    
    /**
     * 批量优化课程信息
     */
    Map<String, String> optimizeCourseInfo(String title, String description, String category);
} 