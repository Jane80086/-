package com.cemenghui.course.controller;

import com.cemenghui.course.entity.Course;
import com.cemenghui.course.service.CourseRecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    @Autowired
    private CourseRecommendationService courseRecommendationService;

    /**
     * 综合推荐接口：兴趣+热门+最新
     */
    @GetMapping("/user")
    public ResponseEntity<List<Course>> recommendCoursesForUser(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "10") int limit) {
        List<Course> result = courseRecommendationService.recommendCoursesForUser(userId, limit);
        return ResponseEntity.ok(result);
    }
} 