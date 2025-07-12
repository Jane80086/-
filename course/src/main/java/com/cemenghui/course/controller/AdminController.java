package com.cemenghui.course.controller;

import com.cemenghui.course.common.Result;
import com.cemenghui.entity.User;
import com.cemenghui.course.entity.Course;
import com.cemenghui.course.service.CourseService;
import com.cemenghui.course.service.QnAService;
import com.cemenghui.course.service.ReviewService;
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
    @Autowired
    private ReviewService reviewService;

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
            User user = userService.findById(id);
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
     * 课程分页（支持 page/size 参数，返回标准分页结构）
     */
    @GetMapping("/courses")
    public Result getCourses(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "12") int size) {
        try {
            int offset = (page - 1) * size;
            List<Course> all = courseService.listCourses();
            // 只返回已发布课程
            List<Course> published = all.stream().filter(c -> "PUBLISHED".equals(c.getStatus())).toList();
            int total = published.size();
            List<Course> pageList = published.stream().skip(offset).limit(size).toList();
            Map<String, Object> pageInfo = new HashMap<>();
            pageInfo.put("content", pageList);
            pageInfo.put("totalElements", total);
            pageInfo.put("totalPages", (int) Math.ceil((double) total / size));
            pageInfo.put("currentPage", page);
            pageInfo.put("pageSize", size);
            pageInfo.put("hasNext", offset + size < total);
            pageInfo.put("hasPrevious", page > 1);
            return Result.success("获取成功", pageInfo);
        } catch (Exception e) {
            return Result.fail("获取课程列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取待审核课程
     */
    @GetMapping("/courses/pending")
    public Result getPendingCourses() {
        try {
            List<Course> pendingCourses = courseService.getPendingCourses();
            Map<String, Object> pageInfo = new HashMap<>();
            pageInfo.put("content", pendingCourses);
            pageInfo.put("totalElements", pendingCourses.size());
            return Result.success(pageInfo);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("获取待审核课程失败: " + e.getMessage());
        }
    }

    /**
     * 课程审核
     */
    @PostMapping("/course/{id}/review")
    public Result reviewCourse(@PathVariable Long id, @RequestParam String status, @RequestParam(required = false) String reason, @RequestParam Long reviewerId) {
        System.out.println("[审核接口] 收到参数: id=" + id + ", status=" + status + ", reason=" + reason + ", reviewerId=" + reviewerId);
        try {
            if ("approved".equalsIgnoreCase(status) || "PUBLISHED".equalsIgnoreCase(status)) {
                reviewService.approveCourse(id, reviewerId);
                return Result.success("课程审核通过");
            } else if ("rejected".equalsIgnoreCase(status) || "REJECTED".equalsIgnoreCase(status)) {
                reviewService.rejectCourse(id, reviewerId, reason);
                return Result.success("课程已驳回");
            } else {
                return Result.fail("未知审核状态: " + status);
            }
        } catch (Exception e) {
            e.printStackTrace();
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

    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }
} 