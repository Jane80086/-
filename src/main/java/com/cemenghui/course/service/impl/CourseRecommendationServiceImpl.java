package com.cemenghui.course.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * 课程推荐服务
 * 集成MCP服务器提供智能推荐功能
 */
@Service
public class CourseRecommendationServiceImpl {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${mcp.server.url:http://localhost:6277}")
    private String mcpServerUrl;

    /**
     * 获取课程热度分析
     */
    public List<Map<String, Object>> analyzeCourseHeat(Integer courseId, String timeRange) {
        try {
            String url = mcpServerUrl + "/tools/analyze_course_heat";
            
            Map<String, Object> request = new HashMap<>();
            if (courseId != null) {
                request.put("course_id", courseId);
            }
            request.put("time_range", timeRange != null ? timeRange : "7d");
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);
            
            ResponseEntity<List> response = restTemplate.postForEntity(url, entity, List.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            }
            
            return new ArrayList<>();
            
        } catch (Exception e) {
            System.err.println("分析课程热度失败: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * 生成课程推荐
     */
    public List<Map<String, Object>> generateRecommendations(Long userId, String category, Integer limit) {
        try {
            String url = mcpServerUrl + "/tools/generate_course_recommendations";
            
            Map<String, Object> request = new HashMap<>();
            if (userId != null) {
                request.put("user_id", userId);
            }
            if (category != null) {
                request.put("category", category);
            }
            request.put("limit", limit != null ? limit : 10);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);
            
            ResponseEntity<List> response = restTemplate.postForEntity(url, entity, List.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            }
            
            return new ArrayList<>();
            
        } catch (Exception e) {
            System.err.println("生成课程推荐失败: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * 更新推荐课程
     */
    public Map<String, Object> updateFeaturedCourses(Boolean autoUpdate, List<Integer> manualCourseIds) {
        try {
            String url = mcpServerUrl + "/tools/update_featured_courses";
            
            Map<String, Object> request = new HashMap<>();
            request.put("auto_update", autoUpdate != null ? autoUpdate : true);
            if (manualCourseIds != null && !manualCourseIds.isEmpty()) {
                request.put("manual_course_ids", manualCourseIds);
            }
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);
            
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            }
            
            return Map.of("success", false, "message", "更新失败");
            
        } catch (Exception e) {
            System.err.println("更新推荐课程失败: " + e.getMessage());
            return Map.of("success", false, "error", e.getMessage());
        }
    }

    /**
     * 获取热门课程
     */
    public List<Map<String, Object>> getHotCourses(String category, Integer limit) {
        return generateRecommendations(null, category, limit);
    }

    /**
     * 获取个性化推荐
     */
    public List<Map<String, Object>> getPersonalizedRecommendations(Long userId, Integer limit) {
        return generateRecommendations(userId, null, limit);
    }

    /**
     * 获取趋势课程
     */
    public List<Map<String, Object>> getTrendingCourses(String category, Integer limit) {
        // 获取1天内的热度分析，找出趋势上升的课程
        List<Map<String, Object>> heatAnalysis = analyzeCourseHeat(null, "1d");
        
        List<Map<String, Object>> trendingCourses = new ArrayList<>();
        for (Map<String, Object> analysis : heatAnalysis) {
            if ("上升".equals(analysis.get("trend"))) {
                trendingCourses.add(analysis);
            }
        }
        
        // 按热度分数排序
        trendingCourses.sort((a, b) -> {
            Double scoreA = (Double) a.get("heat_score");
            Double scoreB = (Double) b.get("heat_score");
            return scoreB.compareTo(scoreA);
        });
        
        return trendingCourses.subList(0, Math.min(limit != null ? limit : 5, trendingCourses.size()));
    }

    /**
     * 检查MCP服务器状态
     */
    public boolean checkMcpServerStatus() {
        try {
            String url = mcpServerUrl + "/health";
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取推荐统计信息
     */
    public Map<String, Object> getRecommendationStats() {
        try {
            // 获取各种推荐数据
            List<Map<String, Object>> hotCourses = getHotCourses(null, 10);
            List<Map<String, Object>> trendingCourses = getTrendingCourses(null, 5);
            List<Map<String, Object>> heatAnalysis = analyzeCourseHeat(null, "7d");
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("hot_courses_count", hotCourses.size());
            stats.put("trending_courses_count", trendingCourses.size());
            stats.put("total_courses_analyzed", heatAnalysis.size());
            stats.put("mcp_server_status", checkMcpServerStatus());
            
            // 计算平均热度分数
            if (!heatAnalysis.isEmpty()) {
                double avgHeatScore = heatAnalysis.stream()
                    .mapToDouble(a -> (Double) a.get("heat_score"))
                    .average()
                    .orElse(0.0);
                stats.put("average_heat_score", avgHeatScore);
            }
            
            return stats;
            
        } catch (Exception e) {
            return Map.of("error", e.getMessage());
        }
    }
} 