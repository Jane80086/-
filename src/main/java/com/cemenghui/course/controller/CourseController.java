package com.cemenghui.course.controller;

import com.cemenghui.course.common.Result;
import com.cemenghui.course.entity.Course;
import com.cemenghui.course.service.CourseService;
import com.cemenghui.course.service.impl.CourseManagerServiceImpl;
import com.cemenghui.course.service.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/course")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private CourseManagerServiceImpl courseManagerService;

    /**
     * 创建新课程
     */
    @PostMapping("/create")
    public Result createCourse(@RequestBody Course course) {
        try {
            Course createdCourse = courseManagerService.createCourse(course);
            return Result.success("课程创建成功", createdCourse);
        } catch (Exception e) {
            return Result.fail("课程创建失败: " + e.getMessage());
        }
    }

    /**
     * 获取课程详情
     */
    @GetMapping("/{id}")
    public Result getCourseDetail(@PathVariable Long id) {
        try {
            Course course = courseService.getCourseDetail(id);
            return Result.success(course);
        } catch (NotFoundException e) {
            return Result.fail("课程不存在: " + e.getMessage());
        } catch (Exception e) {
            return Result.fail("获取课程详情失败: " + e.getMessage());
        }
    }

    /**
     * 更新课程信息
     */
    @PutMapping("/{id}")
    public Result updateCourse(@PathVariable Long id, @RequestBody Course updatedCourse) {
        try {
            Course course = courseManagerService.editCourse(id, updatedCourse);
            return Result.success("课程更新成功", course);
        } catch (NotFoundException e) {
            return Result.fail("课程不存在: " + e.getMessage());
        } catch (Exception e) {
            return Result.fail("课程更新失败: " + e.getMessage());
        }
    }

    /**
     * 删除课程
     */
    @DeleteMapping("/{id}")
    public Result deleteCourse(@PathVariable Long id) {
        try {
            boolean success = courseManagerService.deleteCourse(id);
            if (success) {
                return Result.success("课程删除成功", null);
            } else {
                return Result.fail("课程不存在或删除失败");
            }
        } catch (Exception e) {
            return Result.fail("课程删除失败: " + e.getMessage());
        }
    }

    /**
     * 获取课程列表
     */
    @GetMapping("/list")
    public Result getCourseList() {
        try {
            List<Course> courses = courseService.listCourses();
            return Result.success(courses);
        } catch (Exception e) {
            return Result.fail("获取课程列表失败: " + e.getMessage());
        }
    }

    /**
     * 搜索课程
     */
    @GetMapping("/search")
    public Result searchCourses(@RequestParam String keyword) {
        try {
            List<Course> courses = courseService.searchCourses(keyword);
            return Result.success(courses);
        } catch (Exception e) {
            return Result.fail("搜索课程失败: " + e.getMessage());
        }
    }

    /**
     * 提交课程审核
     */
    @PostMapping("/{id}/submit")
    public Result submitForReview(@PathVariable Long id) {
        try {
            boolean success = courseManagerService.submitForReview(id);
            if (success) {
                return Result.success("课程提交审核成功", null);
            } else {
                return Result.fail("课程提交审核失败");
            }
        } catch (Exception e) {
            return Result.fail("提交审核失败: " + e.getMessage());
        }
    }

    /**
     * 获取热门搜索趋势
     */
    @GetMapping("/trends")
    public Result getHotSearchTrends() {
        try {
            List<String> trends = courseService.getHotSearchTrends();
            return Result.success(trends);
        } catch (Exception e) {
            return Result.fail("获取热门趋势失败: " + e.getMessage());
        }
    }
} 