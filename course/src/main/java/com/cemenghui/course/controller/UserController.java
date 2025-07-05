package com.cemenghui.course.controller;

import com.cemenghui.course.common.Result;
import com.cemenghui.entity.User;
import com.cemenghui.course.service.impl.UserServiceImpl;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@Validated
public class UserController {
    @Autowired
    private UserServiceImpl userService;

    /**
     * 获取当前用户信息
     */
    @GetMapping("/info")
    public Result getUserInfo() {
        try {
            // 模拟用户信息（实际应该从JWT token中获取）
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", 1L);
            userInfo.put("username", "admin");
            userInfo.put("email", "admin@example.com");
            userInfo.put("name", "管理员");
            userInfo.put("avatar", "https://randomuser.me/api/portraits/men/32.jpg");
            userInfo.put("userType", "ADMIN");
            userInfo.put("status", 1);
            
            return Result.success(userInfo);
        } catch (Exception e) {
            return Result.fail("获取用户信息失败: " + e.getMessage());
        }
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result login(@RequestBody Map<String, String> loginData) {
        try {
            String username = loginData.get("username");
            String password = loginData.get("password");
            
            // 简单的登录验证（实际应该有密码加密和数据库验证）
            if ("admin".equals(username) && "admin123".equals(password)) {
                Map<String, Object> result = new HashMap<>();
                result.put("token", "mock-jwt-token-" + System.currentTimeMillis());
                result.put("user", Map.of(
                    "id", 1L,
                    "username", username,
                    "name", "管理员"
                ));
                return Result.success("登录成功", result);
            } else {
                return Result.fail("用户名或密码错误");
            }
        } catch (Exception e) {
            return Result.fail("登录失败: " + e.getMessage());
        }
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result register(@RequestBody @Valid User user) {
        try {
            if (userService.existsByUsername(user.getUsername())) {
                return Result.fail("用户已存在");
            }
            userService.saveUser(user);
            return Result.success("注册成功");
        } catch (Exception e) {
            return Result.fail("注册失败: " + e.getMessage());
        }
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/info")
    public Result updateUserInfo(@RequestBody Map<String, Object> userData) {
        try {
            // 模拟更新用户信息
            Map<String, Object> result = new HashMap<>();
            result.put("message", "用户信息更新成功");
            result.put("updatedFields", userData.keySet());
            return Result.success(result);
        } catch (Exception e) {
            return Result.fail("更新用户信息失败: " + e.getMessage());
        }
    }

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