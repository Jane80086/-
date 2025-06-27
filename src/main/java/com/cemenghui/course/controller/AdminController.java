package com.cemenghui.course.controller;

import com.cemenghui.course.common.Result;
import com.cemenghui.course.common.User;
import com.cemenghui.course.common.AdminUser;
import com.cemenghui.course.common.EnterpriseUser;
import com.cemenghui.course.common.NormalUser;
import com.cemenghui.course.service.CourseService;
import com.cemenghui.course.service.QnAService;
import com.cemenghui.course.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
        // 这里只做示例，实际应实现分页
        return Result.success(userService.findByUserType(null));
    }

    /**
     * 封禁/解封用户
     */
    @PostMapping("/user/{id}/ban")
    public Result banUser(@PathVariable Long id) {
        // 这里只做示例
        return Result.success("用户已封禁", null);
    }
    @PostMapping("/user/{id}/unban")
    public Result unbanUser(@PathVariable Long id) {
        // 这里只做示例
        return Result.success("用户已解封", null);
    }

    /**
     * 课程分页
     */
    @GetMapping("/courses")
    public Result getCourses(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        // 这里只做示例，实际应实现分页
        return Result.success(courseService.listCourses());
    }

    /**
     * 课程审核
     */
    @PostMapping("/course/{id}/review")
    public Result reviewCourse(@PathVariable Long id, @RequestParam String status, @RequestParam(required = false) String reason) {
        // 这里只做示例
        return Result.success("课程审核操作成功", null);
    }

    /**
     * 评论审核
     */
    @PostMapping("/comment/{id}/review")
    public Result reviewComment(@PathVariable Long id, @RequestParam String status, @RequestParam(required = false) String reason) {
        // 这里只做示例
        return Result.success("评论审核操作成功", null);
    }

    /**
     * 问答审核
     */
    @PostMapping("/qna/{id}/review")
    public Result reviewQnA(@PathVariable Long id, @RequestParam String status, @RequestParam(required = false) String reason) {
        // 这里只做示例
        return Result.success("问答审核操作成功", null);
    }
} 