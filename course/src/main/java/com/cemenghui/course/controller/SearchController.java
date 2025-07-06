package com.cemenghui.course.controller;

import com.cemenghui.course.common.Result;
import com.cemenghui.course.entity.Course;
import com.cemenghui.course.service.CourseService;
import com.cemenghui.course.service.AIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private AIService aiService;

    /**
     * 搜索课程
     */
    @GetMapping("/courses")
    public Result searchCourses(@RequestParam String keyword) {
        try {
            List<Course> courses = courseService.searchCourses(keyword);
            return Result.success(courses);
        } catch (Exception e) {
            return Result.fail("搜索课程失败: " + e.getMessage());
        }
    }

    /**
     * 获取热门关键词
     */
    @GetMapping("/hot-keywords")
    public Result getHotKeywords() {
        try {
            List<String> keywords = aiService.getHotSearchTrends();
            return Result.success(keywords);
        } catch (Exception e) {
            return Result.fail("获取热门关键词失败: " + e.getMessage());
        }
    }

    /**
     * 获取搜索建议
     */
    @GetMapping("/suggestions")
    public Result getSearchSuggestions(@RequestParam(required = false) String q) {
        try {
            String query = q != null ? q : "";
            // 这里可以实现搜索建议逻辑
            // 简化实现，返回一些示例建议
            List<String> suggestions = List.of(
                query + " 入门",
                query + " 进阶",
                query + " 实战",
                query + " 教程"
            );
            return Result.success(suggestions);
        } catch (Exception e) {
            return Result.fail("获取搜索建议失败: " + e.getMessage());
        }
    }

    /**
     * 获取相关课程推荐
     */
    @GetMapping("/related/{courseId}")
    public Result getRelatedCourses(@PathVariable Long courseId) {
        try {
            // 这里可以实现相关课程推荐逻辑
            // 简化实现，返回空列表
            List<Course> relatedCourses = List.of();
            return Result.success(relatedCourses);
        } catch (Exception e) {
            return Result.fail("获取相关课程失败: " + e.getMessage());
        }
    }

    /**
     * 高级搜索 - 支持GET和POST请求
     */
    @GetMapping("/advanced")
    @PostMapping("/advanced")
    public Result advancedSearch(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String level,
            @RequestParam(required = false) String status,
            @RequestBody(required = false) Map<String, Object> searchParams) {
        try {
            // 如果使用POST请求，从请求体中获取参数
            if (searchParams != null) {
                keyword = (String) searchParams.get("keyword");
                category = (String) searchParams.get("category");
                level = (String) searchParams.get("level");
                status = (String) searchParams.get("status");
            }
            
            // 这里可以实现高级搜索逻辑
            // 简化实现，使用基础搜索
            List<Course> courses = courseService.searchCourses(keyword != null ? keyword : "");
            return Result.success(courses);
        } catch (Exception e) {
            return Result.fail("高级搜索失败: " + e.getMessage());
        }
    }
} 