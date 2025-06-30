package com.cemenghui.course.controller;

import com.cemenghui.common.Result;
import com.cemenghui.course.entity.Course;
import com.cemenghui.course.service.CourseService;
import com.cemenghui.course.service.AIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public Result getSearchSuggestions(@RequestParam String query) {
        try {
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
     * 高级搜索
     */
    @GetMapping("/advanced")
    public Result advancedSearch(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String level,
            @RequestParam(required = false) String status) {
        try {
            // 这里可以实现高级搜索逻辑
            // 简化实现，使用基础搜索
            List<Course> courses = courseService.searchCourses(keyword != null ? keyword : "");
            return Result.success(courses);
        } catch (Exception e) {
            return Result.fail("高级搜索失败: " + e.getMessage());
        }
    }
} 