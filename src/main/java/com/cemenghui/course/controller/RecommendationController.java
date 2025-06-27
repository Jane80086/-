package com.cemenghui.course.controller;

import com.cemenghui.course.service.impl.CourseRecommendationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 课程推荐控制器
 * 提供课程热度分析和智能推荐功能
 */
@RestController
@RequestMapping("/api/recommendations")
@CrossOrigin(origins = "*")
public class RecommendationController {

    @Autowired
    private CourseRecommendationServiceImpl recommendationService;

    /**
     * 分析课程热度
     */
    @GetMapping("/heat-analysis")
    public ResponseEntity<List<Map<String, Object>>> analyzeCourseHeat(
            @RequestParam(required = false) Integer courseId,
            @RequestParam(defaultValue = "7d") String timeRange) {
        
        List<Map<String, Object>> result = recommendationService.analyzeCourseHeat(courseId, timeRange);
        return ResponseEntity.ok(result);
    }

    /**
     * 生成课程推荐
     */
    @GetMapping("/generate")
    public ResponseEntity<List<Map<String, Object>>> generateRecommendations(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "10") Integer limit) {
        
        List<Map<String, Object>> result = recommendationService.generateRecommendations(userId, category, limit);
        return ResponseEntity.ok(result);
    }

    /**
     * 获取热门课程
     */
    @GetMapping("/hot")
    public ResponseEntity<List<Map<String, Object>>> getHotCourses(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "10") Integer limit) {
        
        List<Map<String, Object>> result = recommendationService.getHotCourses(category, limit);
        return ResponseEntity.ok(result);
    }

    /**
     * 获取个性化推荐
     */
    @GetMapping("/personalized")
    public ResponseEntity<List<Map<String, Object>>> getPersonalizedRecommendations(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "10") Integer limit) {
        
        List<Map<String, Object>> result = recommendationService.getPersonalizedRecommendations(userId, limit);
        return ResponseEntity.ok(result);
    }

    /**
     * 获取趋势课程
     */
    @GetMapping("/trending")
    public ResponseEntity<List<Map<String, Object>>> getTrendingCourses(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "5") Integer limit) {
        
        List<Map<String, Object>> result = recommendationService.getTrendingCourses(category, limit);
        return ResponseEntity.ok(result);
    }

    /**
     * 更新推荐课程
     */
    @PostMapping("/update-featured")
    public ResponseEntity<Map<String, Object>> updateFeaturedCourses(
            @RequestParam(defaultValue = "true") Boolean autoUpdate,
            @RequestParam(required = false) List<Integer> manualCourseIds) {
        
        Map<String, Object> result = recommendationService.updateFeaturedCourses(autoUpdate, manualCourseIds);
        return ResponseEntity.ok(result);
    }

    /**
     * 检查MCP服务器状态
     */
    @GetMapping("/mcp-status")
    public ResponseEntity<Map<String, Object>> getMcpServerStatus() {
        boolean status = recommendationService.checkMcpServerStatus();
        return ResponseEntity.ok(Map.of("status", status, "message", status ? "MCP服务器运行正常" : "MCP服务器连接失败"));
    }

    /**
     * 获取推荐统计信息
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getRecommendationStats() {
        Map<String, Object> stats = recommendationService.getRecommendationStats();
        return ResponseEntity.ok(stats);
    }

    /**
     * 获取首页推荐课程（综合推荐）
     */
    @GetMapping("/homepage")
    public ResponseEntity<Map<String, Object>> getHomepageRecommendations(
            @RequestParam(required = false) Long userId,
            @RequestParam(defaultValue = "6") Integer limit) {
        
        try {
            // 获取热门课程
            List<Map<String, Object>> hotCourses = recommendationService.getHotCourses(null, limit / 2);
            
            // 获取趋势课程
            List<Map<String, Object>> trendingCourses = recommendationService.getTrendingCourses(null, limit / 2);
            
            // 如果指定了用户ID，添加个性化推荐
            List<Map<String, Object>> personalizedCourses = new java.util.ArrayList<>();
            if (userId != null) {
                personalizedCourses = recommendationService.getPersonalizedRecommendations(userId, limit / 3);
            }
            
            Map<String, Object> result = Map.of(
                "hot_courses", hotCourses,
                "trending_courses", trendingCourses,
                "personalized_courses", personalizedCourses,
                "mcp_server_status", recommendationService.checkMcpServerStatus()
            );
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                "error", e.getMessage(),
                "hot_courses", List.of(),
                "trending_courses", List.of(),
                "personalized_courses", List.of(),
                "mcp_server_status", false
            ));
        }
    }

    /**
     * 按分类获取推荐课程
     */
    @GetMapping("/by-category/{category}")
    public ResponseEntity<Map<String, Object>> getRecommendationsByCategory(
            @PathVariable String category,
            @RequestParam(defaultValue = "5") Integer limit) {
        
        try {
            List<Map<String, Object>> hotCourses = recommendationService.getHotCourses(category, limit);
            List<Map<String, Object>> trendingCourses = recommendationService.getTrendingCourses(category, limit);
            
            Map<String, Object> result = Map.of(
                "category", category,
                "hot_courses", hotCourses,
                "trending_courses", trendingCourses,
                "total_count", hotCourses.size() + trendingCourses.size()
            );
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                "category", category,
                "error", e.getMessage(),
                "hot_courses", List.of(),
                "trending_courses", List.of(),
                "total_count", 0
            ));
        }
    }
} 