package com.cemenghui.course.controller;

import com.cemenghui.course.common.Result;
import com.cemenghui.course.service.CourseOptimizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/course-optimization")
public class CourseOptimizationController {

    @Autowired
    private CourseOptimizationService courseOptimizationService;

    /**
     * AI优化课程标题
     */
    @PostMapping("/optimize-title")
    public ResponseEntity<Result> optimizeTitle(
            @RequestParam String originalTitle,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String description) {
        try {
            String optimizedTitle = courseOptimizationService.optimizeTitle(originalTitle, category, description);
            return ResponseEntity.ok(Result.success("标题优化成功", Map.of("optimized_title", optimizedTitle)));
        } catch (Exception e) {
            return ResponseEntity.ok(Result.fail("标题优化失败: " + e.getMessage()));
        }
    }

    /**
     * AI优化课程简介
     */
    @PostMapping("/optimize-description")
    public ResponseEntity<Result> optimizeDescription(
            @RequestParam String originalDescription,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String title) {
        try {
            String optimizedDescription = courseOptimizationService.optimizeDescription(originalDescription, category, title);
            return ResponseEntity.ok(Result.success("简介优化成功", Map.of("optimized_description", optimizedDescription)));
        } catch (Exception e) {
            return ResponseEntity.ok(Result.fail("简介优化失败: " + e.getMessage()));
        }
    }

    /**
     * 批量优化课程信息（标题+简介）
     */
    @PostMapping("/optimize-course-info")
    public ResponseEntity<Result> optimizeCourseInfo(
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam(required = false) String category) {
        try {
            Map<String, String> optimizedInfo = courseOptimizationService.optimizeCourseInfo(title, description, category);
            return ResponseEntity.ok(Result.success("课程信息优化成功", optimizedInfo));
        } catch (Exception e) {
            return ResponseEntity.ok(Result.fail("课程信息优化失败: " + e.getMessage()));
        }
    }
} 