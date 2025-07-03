package com.cemenghui.course.controller;

import com.cemenghui.course.common.Result;
import com.cemenghui.course.entity.FeaturedCourse;
import com.cemenghui.course.service.FeaturedCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/featured")
public class FeaturedCourseController {

    @Autowired
    private FeaturedCourseService featuredCourseService;

    /**
     * 获取推荐课程列表
     */
    @GetMapping("/list")
    public Result getFeaturedCourses() {
        try {
            List<FeaturedCourse> courses = featuredCourseService.listFeaturedCourses();
            return Result.success(courses);
        } catch (Exception e) {
            return Result.fail("获取推荐课程失败: " + e.getMessage());
        }
    }

    /**
     * 设置课程推荐
     */
    @PostMapping("/{courseId}/promote")
    public ResponseEntity<Map<String, Object>> promoteToFeatured(@PathVariable Long courseId, @RequestParam int priority) {
        Map<String, Object> result = new HashMap<>();
        if (courseId == null || courseId <= 0) {
            System.out.println("[DEBUG] 课程ID非法: " + courseId);
            result.put("code", -1);
            result.put("msg", "失败: 课程ID非法");
            result.put("data", null);
            return ResponseEntity.ok(result);
        }
        if (priority < 0) {
            System.out.println("[DEBUG] priority非法: " + priority);
            result.put("code", -1);
            result.put("msg", "失败: priority非法");
            result.put("data", null);
            return ResponseEntity.ok(result);
        }
        try {
            boolean success = featuredCourseService.promoteToFeatured(courseId, priority);
            if (success) {
                result.put("code", 0);
                result.put("msg", "课程推荐设置成功");
                result.put("data", null);
                return ResponseEntity.ok(result);
            } else {
                System.out.println("[DEBUG] 课程推荐设置失败: " + courseId);
                result.put("code", -1);
                result.put("msg", "课程推荐设置失败");
                result.put("data", null);
                return ResponseEntity.ok(result);
            }
        } catch (Exception e) {
            System.out.println("[DEBUG] 设置推荐异常: " + e.getMessage());
            result.put("code", -1);
            result.put("msg", "设置推荐失败: " + e.getMessage());
            result.put("data", null);
            return ResponseEntity.ok(result);
        }
    }

    /**
     * 取消课程推荐
     */
    @DeleteMapping("/{courseId}")
    public ResponseEntity<Map<String, Object>> removeFromFeatured(@PathVariable Long courseId) {
        Map<String, Object> result = new HashMap<>();
        if (courseId == null || courseId <= 0) {
            System.out.println("[DEBUG] 课程ID非法: " + courseId);
            result.put("code", -1);
            result.put("msg", "失败: 课程ID非法");
            result.put("data", null);
            return ResponseEntity.ok(result);
        }
        try {
            boolean success = featuredCourseService.removeFromFeatured(courseId);
            if (success) {
                result.put("code", 0);
                result.put("msg", "取消推荐成功");
                result.put("data", null);
                return ResponseEntity.ok(result);
            } else {
                System.out.println("[DEBUG] 取消推荐失败: " + courseId);
                result.put("code", -1);
                result.put("msg", "取消推荐失败");
                result.put("data", null);
                return ResponseEntity.ok(result);
            }
        } catch (Exception e) {
            System.out.println("[DEBUG] 取消推荐异常: " + e.getMessage());
            result.put("code", -1);
            result.put("msg", "取消推荐失败: " + e.getMessage());
            result.put("data", null);
            return ResponseEntity.ok(result);
        }
    }

    /**
     * 更新推荐优先级
     */
    @PutMapping("/{courseId}/priority")
    public Result updatePriority(
            @PathVariable Long courseId,
            @RequestParam int priority) {
        try {
            // 先取消推荐，再重新设置
            featuredCourseService.removeFromFeatured(courseId);
            boolean success = featuredCourseService.promoteToFeatured(courseId, priority);
            if (success) {
                return Result.success("优先级更新成功", null);
            } else {
                return Result.fail("优先级更新失败");
            }
        } catch (Exception e) {
            return Result.fail("更新优先级失败: " + e.getMessage());
        }
    }
} 