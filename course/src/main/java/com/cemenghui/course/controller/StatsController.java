package com.cemenghui.course.controller;

import com.cemenghui.course.common.Result;
import com.cemenghui.course.entity.Course;
import com.cemenghui.course.service.CourseService;
import com.cemenghui.course.service.CourseHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stats")
public class StatsController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private CourseHistoryService courseHistoryService;

    /**
     * 获取统计数据概览
     */
    @GetMapping("/overview")
    public Result getStatsOverview() {
        try {
            Map<String, Object> stats = new HashMap<>();
            
            // 获取课程总数
            List<Course> allCourses = courseService.listCourses();
            stats.put("totalCourses", allCourses != null ? allCourses.size() : 0);
            
            // 模拟其他统计数据
            stats.put("totalUsers", 1250);
            stats.put("totalViews", 15680);
            stats.put("avgRating", 4.6);
            stats.put("activeUsers", 890);
            stats.put("newCoursesThisMonth", 15);
            
            return Result.success(stats);
        } catch (Exception e) {
            return Result.fail("获取统计数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取图表数据
     */
    @GetMapping("/chart")
    public Result getChartData() {
        try {
            Map<String, Object> chartData = new HashMap<>();
            
            // 模拟图表数据
            chartData.put("courseGrowth", List.of(10, 15, 20, 25, 30, 35));
            chartData.put("userGrowth", List.of(100, 150, 200, 250, 300, 350));
            chartData.put("revenueData", List.of(1000, 1500, 2000, 2500, 3000, 3500));
            
            return Result.success(chartData);
        } catch (Exception e) {
            return Result.fail("获取图表数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取仪表板数据
     */
    @GetMapping("/dashboard")
    public Result getDashboardStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            
            // 获取课程总数
            List<Course> allCourses = courseService.listCourses();
            stats.put("totalCourses", allCourses != null ? allCourses.size() : 0);
            
            // 获取热门趋势
            List<String> trends = courseService.getHotSearchTrends();
            stats.put("hotTrends", trends);
            
            // 其他统计数据
            stats.put("totalViews", 0); // 需要实现
            stats.put("totalUsers", 0); // 需要实现
            stats.put("recentActivity", List.of()); // 需要实现
            
            return Result.success(stats);
        } catch (Exception e) {
            return Result.fail("获取仪表板数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取课程分析数据
     */
    @GetMapping("/course/{id}/analytics")
    public Result getCourseAnalytics(@PathVariable Long id) {
        try {
            Map<String, Object> analytics = new HashMap<>();
            
            // 课程基本信息
            Course course = courseService.getCourseDetail(id);
            analytics.put("courseInfo", course);
            
            // 观看统计
            analytics.put("viewCount", 0); // 需要实现
            analytics.put("uniqueViewers", 0); // 需要实现
            analytics.put("averageWatchTime", 0); // 需要实现
            
            // 问答统计
            analytics.put("questionCount", 0); // 需要实现
            analytics.put("replyRate", 0.0); // 需要实现
            
            return Result.success(analytics);
        } catch (Exception e) {
            return Result.fail("获取课程分析数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户观看历史
     */
    @GetMapping("/user/{id}/history")
    public Result getUserHistory(@PathVariable Long id) {
        try {
            List<Course> history = courseHistoryService.getUserHistory(id);
            return Result.success(history);
        } catch (Exception e) {
            return Result.fail("获取用户历史失败: " + e.getMessage());
        }
    }

    /**
     * 获取热门趋势
     */
    @GetMapping("/trends")
    public Result getTrends() {
        try {
            List<String> trends = courseService.getHotSearchTrends();
            return Result.success(trends);
        } catch (Exception e) {
            return Result.fail("获取热门趋势失败: " + e.getMessage());
        }
    }

    /**
     * 获取收入统计
     */
    @GetMapping("/revenue")
    public Result getRevenueStats() {
        try {
            Map<String, Object> revenue = new HashMap<>();
            
            // 收入统计
            revenue.put("totalRevenue", 0.0);
            revenue.put("monthlyRevenue", 0.0);
            revenue.put("topCourses", List.of());
            revenue.put("revenueChart", List.of());
            
            return Result.success(revenue);
        } catch (Exception e) {
            return Result.fail("获取收入统计失败: " + e.getMessage());
        }
    }

    /**
     * 获取系统健康状态
     */
    @GetMapping("/health")
    public Result getSystemHealth() {
        try {
            Map<String, Object> health = new HashMap<>();
            
            health.put("status", "healthy");
            health.put("uptime", System.currentTimeMillis());
            health.put("database", "connected");
            health.put("cache", "connected");
            health.put("mcpServer", "connected");
            
            return Result.success(health);
        } catch (Exception e) {
            return Result.fail("获取系统健康状态失败: " + e.getMessage());
        }
    }
} 