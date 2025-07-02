package com.cemenghui.course.controller;

import com.cemenghui.course.common.User;
import com.cemenghui.course.service.impl.UserServiceImpl;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@Validated
public class UserController {
    @Autowired
    private UserServiceImpl userService;

    @PostMapping("/add")
    public ResponseEntity<String> addUser(@RequestBody User user) {
        System.out.println("[DEBUG] 检查用户名是否存在: " + user.getUsername());
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            return ResponseEntity.ok("失败: 用户名不能为空");
        }
        if (user.getPassword() == null || user.getPassword().length() < 6) {
            return ResponseEntity.ok("失败: 密码过短");
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            return ResponseEntity.ok("失败: 邮箱不能为空");
        }
        if (!"normal".equals(user.getUserType()) && !"admin".equals(user.getUserType()) && !"enterprise".equals(user.getUserType())) {
            return ResponseEntity.ok("失败: 用户类型非法");
        }
        if (userService.existsByUsername(user.getUsername())) {
            System.out.println("[DEBUG] 用户已存在: " + user.getUsername());
            return ResponseEntity.ok("失败: 用户已存在");
        }
        System.out.println("[DEBUG] 用户不存在，准备插入: " + user.getUsername());
        userService.saveUser(user);
        return ResponseEntity.ok("用户添加成功: " + user.getUsername());
    }

    @PostMapping("/update")
    public String updateUser(@RequestBody @Valid User user) {
        // 这里只做示例，实际应调用service更新
        return "用户更新成功: " + user.getUsername();
    }
} 