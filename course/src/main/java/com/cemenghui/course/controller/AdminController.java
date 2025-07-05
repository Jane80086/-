package com.cemenghui.course.controller;

import com.cemenghui.course.common.Result;
import com.cemenghui.entity.User;
import com.cemenghui.course.entity.Course;
import com.cemenghui.course.service.CourseService;
import com.cemenghui.course.service.QnAService;
import com.cemenghui.course.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private CourseService courseService;
    @Autowired
    private QnAService qnaService;

    /**
     * 用户分页
     */
    @GetMapping("/users")
    public Result getUsers(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        try {
            // 获取分页用户数据
            List<User> users = userService.getUsersByPage(page, size);
            
            // 获取用户总数
            long total = userService.count();
            
            // 构建分页信息
            Map<String, Object> pageInfo = new HashMap<>();
            pageInfo.put("content", users);
            pageInfo.put("totalElements", total);
            pageInfo.put("totalPages", (int) Math.ceil((double) total / size));
            pageInfo.put("currentPage", page);
            pageInfo.put("pageSize", size);
            pageInfo.put("hasNext", (page + 1) * size < total);
            pageInfo.put("hasPrevious", page > 0);
            
            return Result.success(pageInfo);
        } catch (Exception e) {
            return Result.fail("获取用户列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取所有用户（不分页）
     */
    @GetMapping("/users/all")
    public Result getAllUsers() {
        try {
            List<User> users = userService.getAllUsers();
            return Result.success(users);
        } catch (Exception e) {
            return Result.fail("获取所有用户失败: " + e.getMessage());
        }
    }

    /**
     * 根据用户类型获取用户
     */
    @GetMapping("/users/type/{userType}")
    public Result getUsersByType(@PathVariable String userType) {
        try {
            List<User> users = userService.findByUserType(userType);
            return Result.success(users);
        } catch (Exception e) {
            return Result.fail("根据类型获取用户失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户详情
     */
    @GetMapping("/user/{id}")
    public Result getUserById(@PathVariable Long id) {
        try {
            User user = userService.getById(id);
            if (user != null) {
                return Result.success(user);
            } else {
                return Result.fail("用户不存在");
            }
        } catch (Exception e) {
            return Result.fail("获取用户详情失败: " + e.getMessage());
        }
    }

    /**
     * 封禁/解封用户
     */
    @PostMapping("/user/{id}/ban")
    public Result banUser(@PathVariable Long id) {
        try {
            // 这里应该实现实际的封禁逻辑
            // 暂时返回成功
            Map<String, Object> result = new HashMap<>();
            result.put("userId", id);
            result.put("status", "banned");
            result.put("message", "用户已封禁");
            return Result.success("用户已封禁", result);
        } catch (Exception e) {
            return Result.fail("封禁用户失败: " + e.getMessage());
    }
    }
    
    @PostMapping("/user/{id}/unban")
    public Result unbanUser(@PathVariable Long id) {
        try {
            // 这里应该实现实际的解封逻辑
            // 暂时返回成功
            Map<String, Object> result = new HashMap<>();
            result.put("userId", id);
            result.put("status", "active");
            result.put("message", "用户已解封");
            return Result.success("用户已解封", result);
        } catch (Exception e) {
            return Result.fail("解封用户失败: " + e.getMessage());
        }
    }

    /**
     * 课程分页
     */
    @GetMapping("/courses")
    public Result getCourses(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        try {
        // 这里只做示例，实际应实现分页
            List<Course> courses = courseService.listCourses();
            return Result.success(courses);
        } catch (Exception e) {
            return Result.fail("获取课程列表失败: " + e.getMessage());
        }
    }

    /**
     * 课程审核
     */
    @PostMapping("/course/{id}/review")
    public Result reviewCourse(@PathVariable Long id, @RequestParam String status, @RequestParam(required = false) String reason) {
        try {
            // 这里应该实现实际的审核逻辑
            Map<String, Object> result = new HashMap<>();
            result.put("courseId", id);
            result.put("status", status);
            result.put("reason", reason);
            result.put("reviewTime", System.currentTimeMillis());
            return Result.success("课程审核操作成功", result);
        } catch (Exception e) {
            return Result.fail("课程审核失败: " + e.getMessage());
        }
    }

    /**
     * 评论审核
     */
    @PostMapping("/comment/{id}/review")
    public Result reviewComment(@PathVariable Long id, @RequestParam String status, @RequestParam(required = false) String reason) {
        try {
            // 这里应该实现实际的审核逻辑
            Map<String, Object> result = new HashMap<>();
            result.put("commentId", id);
            result.put("status", status);
            result.put("reason", reason);
            result.put("reviewTime", System.currentTimeMillis());
            return Result.success("评论审核操作成功", result);
        } catch (Exception e) {
            return Result.fail("评论审核失败: " + e.getMessage());
        }
    }

    /**
     * 问答审核
     */
    @PostMapping("/qna/{id}/review")
    public Result reviewQnA(@PathVariable Long id, @RequestParam String status, @RequestParam(required = false) String reason) {
        try {
            // 这里应该实现实际的审核逻辑
            Map<String, Object> result = new HashMap<>();
            result.put("qnaId", id);
            result.put("status", status);
            result.put("reason", reason);
            result.put("reviewTime", System.currentTimeMillis());
            return Result.success("问答审核操作成功", result);
        } catch (Exception e) {
            return Result.fail("问答审核失败: " + e.getMessage());
        }
    }

    /**
     * 获取管理员仪表板数据
     */
    @GetMapping("/dashboard")
    public Result getDashboard() {
        try {
            Map<String, Object> dashboard = new HashMap<>();
            
            // 用户统计
            long totalUsers = userService.count();
            dashboard.put("totalUsers", totalUsers);
            
            // 课程统计
            List<Course> courses = courseService.listCourses();
            dashboard.put("totalCourses", courses != null ? courses.size() : 0);
            
            // 其他统计信息
            dashboard.put("totalComments", 0);
            dashboard.put("totalQuestions", 0);
            dashboard.put("pendingReviews", 0);
            
            return Result.success(dashboard);
        } catch (Exception e) {
            return Result.fail("获取仪表板数据失败: " + e.getMessage());
        }
    }
} 