package com.cemenghui.course.controller;

import com.cemenghui.common.Result;
import com.cemenghui.course.entity.FeaturedCourse;
import com.cemenghui.course.service.FeaturedCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public Result promoteToFeatured(
            @PathVariable Long courseId,
            @RequestParam(defaultValue = "1") int priority) {
        try {
            boolean success = featuredCourseService.promoteToFeatured(courseId, priority);
            if (success) {
                return Result.success("课程推荐设置成功", null);
            } else {
                return Result.fail("课程推荐设置失败");
            }
        } catch (Exception e) {
            return Result.fail("设置推荐失败: " + e.getMessage());
        }
    }

    /**
     * 取消课程推荐
     */
    @DeleteMapping("/{courseId}")
    public Result removeFromFeatured(@PathVariable Long courseId) {
        try {
            boolean success = featuredCourseService.removeFromFeatured(courseId);
            if (success) {
                return Result.success("取消推荐成功", null);
            } else {
                return Result.fail("取消推荐失败");
            }
        } catch (Exception e) {
            return Result.fail("取消推荐失败: " + e.getMessage());
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