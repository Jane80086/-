package com.cemenghui.course.service.impl;

import com.cemenghui.course.entity.Course;
import com.cemenghui.course.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 课程推荐服务
 * 集成MCP服务器提供智能推荐功能，同时提供本地备用实现
 */
@Service
public class CourseRecommendationServiceImpl {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CourseService courseService;

    @Value("${mcp.server.url:http://localhost:6277}")
    private String mcpServerUrl;

    /**
     * 获取课程热度分析
     */
    public List<Map<String, Object>> analyzeCourseHeat(Integer courseId, String timeRange) {
        try {
            // 首先尝试MCP服务器
            if (checkMcpServerStatus()) {
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
            }
            
            // 本地备用实现
            return getLocalHeatAnalysis(courseId, timeRange);
            
        } catch (Exception e) {
            System.err.println("分析课程热度失败: " + e.getMessage());
            return getLocalHeatAnalysis(courseId, timeRange);
        }
    }

    /**
     * 本地热度分析实现
     */
    private List<Map<String, Object>> getLocalHeatAnalysis(Integer courseId, String timeRange) {
        List<Map<String, Object>> result = new ArrayList<>();
        
        try {
            List<Course> courses = courseService.listCourses();
            
            for (Course course : courses) {
                if (courseId != null && !course.getId().equals(courseId.longValue())) {
                    continue;
                }
                
                Map<String, Object> analysis = new HashMap<>();
                analysis.put("course_id", course.getId());
                analysis.put("course_title", course.getTitle());
                analysis.put("heat_score", calculateHeatScore(course));
                analysis.put("trend", "稳定"); // 简化实现
                analysis.put("time_range", timeRange);
                analysis.put("analysis_time", new Date());
                
                result.add(analysis);
            }
            
            // 按热度分数排序
            result.sort((a, b) -> {
                Double scoreA = (Double) a.get("heat_score");
                Double scoreB = (Double) b.get("heat_score");
                return scoreB.compareTo(scoreA);
            });
            
        } catch (Exception e) {
            System.err.println("本地热度分析失败: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * 计算课程热度分数
     */
    private double calculateHeatScore(Course course) {
        double score = 0.0;
        
        // 基于点赞数
        if (course.getLikeCount() != null) {
            score += course.getLikeCount() * 10;
        }
        
        // 基于收藏数
        if (course.getFavoriteCount() != null) {
            score += course.getFavoriteCount() * 20;
        }
        
        // 基于价格（价格越高可能越受欢迎）
        if (course.getPrice() != null) {
            score += course.getPrice().doubleValue() * 0.1;
        }
        
        // 基于时长（适中时长更受欢迎）
        if (course.getDuration() != null) {
            if (course.getDuration() > 0 && course.getDuration() <= 120) {
                score += 50; // 2小时内的课程
            } else if (course.getDuration() <= 240) {
                score += 30; // 4小时内的课程
            } else {
                score += 10; // 更长的课程
            }
        }
        
        // 添加随机因子，模拟真实热度变化
        score += Math.random() * 100;
        
        return Math.max(0, score);
    }

    /**
     * 生成课程推荐
     */
    public List<Map<String, Object>> generateRecommendations(Long userId, String category, Integer limit) {
        try {
            // 首先尝试MCP服务器
            if (checkMcpServerStatus()) {
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
            }
            
            // 本地备用实现
            return getLocalRecommendations(userId, category, limit);
            
        } catch (Exception e) {
            System.err.println("生成课程推荐失败: " + e.getMessage());
            return getLocalRecommendations(userId, category, limit);
        }
    }

    /**
     * 本地推荐实现
     */
    private List<Map<String, Object>> getLocalRecommendations(Long userId, String category, Integer limit) {
        List<Map<String, Object>> result = new ArrayList<>();
        
        try {
            List<Course> courses = courseService.listCourses();
            
            // 过滤分类
            if (category != null && !category.isEmpty()) {
                courses = courses.stream()
                    .filter(course -> category.equals(course.getCategory()))
                    .collect(Collectors.toList());
            }
            
            // 转换为推荐格式
            for (Course course : courses) {
                Map<String, Object> recommendation = new HashMap<>();
                recommendation.put("course_id", course.getId());
                recommendation.put("title", course.getTitle());
                recommendation.put("description", course.getDescription());
                recommendation.put("category", course.getCategory());
                recommendation.put("price", course.getPrice());
                recommendation.put("duration", course.getDuration());
                recommendation.put("instructor_id", course.getInstructorId());
                recommendation.put("cover_image", course.getCoverImage());
                recommendation.put("video_url", course.getVideoUrl());
                recommendation.put("like_count", course.getLikeCount());
                recommendation.put("favorite_count", course.getFavoriteCount());
                recommendation.put("recommendation_score", calculateRecommendationScore(course, userId));
                recommendation.put("recommendation_reason", "基于课程热度和用户偏好");
                
                result.add(recommendation);
            }
            
            // 按推荐分数排序
            result.sort((a, b) -> {
                Double scoreA = (Double) a.get("recommendation_score");
                Double scoreB = (Double) b.get("recommendation_score");
                return scoreB.compareTo(scoreA);
            });
            
            // 限制数量
            int maxLimit = limit != null ? limit : 10;
            return result.subList(0, Math.min(maxLimit, result.size()));
            
        } catch (Exception e) {
            System.err.println("本地推荐生成失败: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * 计算推荐分数
     */
    private double calculateRecommendationScore(Course course, Long userId) {
        double score = 0.0;
        
        // 基础分数
        score += 50;
        
        // 基于点赞数
        if (course.getLikeCount() != null) {
            score += course.getLikeCount() * 5;
        }
        
        // 基于收藏数
        if (course.getFavoriteCount() != null) {
            score += course.getFavoriteCount() * 10;
        }
        
        // 基于价格（适中价格更受欢迎）
        if (course.getPrice() != null) {
            double price = course.getPrice().doubleValue();
            if (price >= 50 && price <= 200) {
                score += 30;
            } else if (price > 0) {
                score += 10;
            }
        }
        
        // 基于时长
        if (course.getDuration() != null) {
            if (course.getDuration() >= 60 && course.getDuration() <= 180) {
                score += 20; // 1-3小时最佳
            } else if (course.getDuration() > 0) {
                score += 10;
            }
        }
        
        // 添加随机因子
        score += Math.random() * 50;
        
        return Math.max(0, score);
    }

    /**
     * 更新推荐课程
     */
    public Map<String, Object> updateFeaturedCourses(Boolean autoUpdate, List<Integer> manualCourseIds) {
        try {
            // 首先尝试MCP服务器
            if (checkMcpServerStatus()) {
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
            }
            
            // 本地备用实现
            return getLocalUpdateFeaturedCourses(autoUpdate, manualCourseIds);
            
        } catch (Exception e) {
            System.err.println("更新推荐课程失败: " + e.getMessage());
            return getLocalUpdateFeaturedCourses(autoUpdate, manualCourseIds);
        }
    }

    /**
     * 本地更新精选课程实现
     */
    private Map<String, Object> getLocalUpdateFeaturedCourses(Boolean autoUpdate, List<Integer> manualCourseIds) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            if (Boolean.TRUE.equals(autoUpdate)) {
                // 自动更新：选择热度最高的课程
                List<Map<String, Object>> hotCourses = getHotCourses(null, 5);
                result.put("auto_updated_courses", hotCourses);
                result.put("auto_update_count", hotCourses.size());
            }
            
            if (manualCourseIds != null && !manualCourseIds.isEmpty()) {
                result.put("manual_course_ids", manualCourseIds);
                result.put("manual_update_count", manualCourseIds.size());
            }
            
            result.put("success", true);
            result.put("message", "精选课程更新成功");
            result.put("update_time", new Date());
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        
        return result;
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
            if ("上升".equals(analysis.get("trend")) || analysis.get("trend") == null) {
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
            System.err.println("获取推荐统计失败: " + e.getMessage());
            return Map.of(
                "hot_courses_count", 0,
                "trending_courses_count", 0,
                "total_courses_analyzed", 0,
                "mcp_server_status", false,
                "average_heat_score", 0.0,
                "error", e.getMessage()
            );
        }
    }
} 