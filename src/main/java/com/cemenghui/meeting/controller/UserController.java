package com.cemenghui.meeting.controller;

import com.cemenghui.meeting.entity.*;
import com.cemenghui.meeting.service.UserService;
import com.cemenghui.meeting.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ApiResponse<User> register(@RequestBody UserRegisterRequest request) {
        try {
            User user = userService.register(request);
            return ApiResponse.success("用户注册成功", user);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("用户注册失败: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ApiResponse<Map<String, Object>> login(@RequestBody UserLoginRequest request) {
        try {
            String token = userService.login(request);
            User user = userService.getUserByUsername(request.getUsername());
            
            Map<String, Object> result = new HashMap<>();
            result.put("token", token);
            result.put("user", user);
            
            return ApiResponse.success("登录成功", result);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("登录失败: " + e.getMessage());
        }
    }

    @PostMapping("/info")
    public ApiResponse<User> getUserInfo(@RequestHeader("Authorization") String token) {
        try {
            if (!jwtUtil.validateToken(token.replace("Bearer ", ""))) {
                return ApiResponse.error(401, "无效的token");
            }
            String username = jwtUtil.getUsernameFromToken(token.replace("Bearer ", ""));
            User user = userService.getUserByUsername(username);
            if (user != null) {
                return ApiResponse.success("获取用户信息成功", user);
            } else {
                return ApiResponse.error("用户不存在");
            }
        } catch (Exception e) {
            return ApiResponse.error("获取用户信息失败: " + e.getMessage());
        }
    }

    @PostMapping("/update")
    public ApiResponse<Boolean> updateUserInfo(@RequestBody User user,
                                              @RequestHeader("Authorization") String token) {
        try {
            if (!jwtUtil.validateToken(token.replace("Bearer ", ""))) {
                return ApiResponse.error(401, "无效的token");
            }
            String username = jwtUtil.getUsernameFromToken(token.replace("Bearer ", ""));
            User currentUser = userService.getUserByUsername(username);
            if (currentUser == null) {
                return ApiResponse.error("用户不存在");
            }
            
            user.setId(currentUser.getId());
            boolean success = userService.updateUserInfo(user);
            if (success) {
                return ApiResponse.success("用户信息更新成功", true);
            } else {
                return ApiResponse.error("用户信息更新失败");
            }
        } catch (Exception e) {
            return ApiResponse.error("用户信息更新失败: " + e.getMessage());
        }
    }

    @PostMapping("/password")
    public ApiResponse<Boolean> updatePassword(@RequestBody Map<String, String> request,
                                              @RequestHeader("Authorization") String token) {
        try {
            if (!jwtUtil.validateToken(token.replace("Bearer ", ""))) {
                return ApiResponse.error(401, "无效的token");
            }
            String username = jwtUtil.getUsernameFromToken(token.replace("Bearer ", ""));
            User user = userService.getUserByUsername(username);
            if (user == null) {
                return ApiResponse.error("用户不存在");
            }
            
            String newPassword = request.get("newPassword");
            if (newPassword == null || newPassword.trim().isEmpty()) {
                return ApiResponse.error(400, "新密码不能为空");
            }
            
            boolean success = userService.updatePassword(user.getId(), newPassword);
            if (success) {
                return ApiResponse.success("密码修改成功", true);
            } else {
                return ApiResponse.error("密码修改失败");
            }
        } catch (Exception e) {
            return ApiResponse.error("密码修改失败: " + e.getMessage());
        }
    }

    @PostMapping("/list")
    public ApiResponse<List<User>> getUserList(@RequestHeader("Authorization") String token) {
        try {
            if (!jwtUtil.validateToken(token.replace("Bearer ", ""))) {
                return ApiResponse.error(401, "无效的token");
            }
            String username = jwtUtil.getUsernameFromToken(token.replace("Bearer ", ""));
            
            // 只有管理员可以查看用户列表
            if (!userService.isAdmin(username)) {
                return ApiResponse.error(403, "权限不足");
            }
            
            List<User> users = userService.getAllUsers();
            return ApiResponse.success("获取用户列表成功", users);
        } catch (Exception e) {
            return ApiResponse.error("获取用户列表失败: " + e.getMessage());
        }
    }

    @PostMapping("/status")
    public ApiResponse<Boolean> updateUserStatus(@RequestBody Map<String, Object> request,
                                                @RequestHeader("Authorization") String token) {
        try {
            if (!jwtUtil.validateToken(token.replace("Bearer ", ""))) {
                return ApiResponse.error(401, "无效的token");
            }
            String username = jwtUtil.getUsernameFromToken(token.replace("Bearer ", ""));
            
            // 只有管理员可以修改用户状态
            if (!userService.isAdmin(username)) {
                return ApiResponse.error(403, "权限不足");
            }
            
            Long userId = Long.valueOf(request.get("userId").toString());
            Integer status = Integer.valueOf(request.get("status").toString());
            
            boolean success = userService.updateUserStatus(userId, status);
            if (success) {
                return ApiResponse.success("用户状态更新成功", true);
            } else {
                return ApiResponse.error("用户状态更新失败");
            }
        } catch (Exception e) {
            return ApiResponse.error("用户状态更新失败: " + e.getMessage());
        }
    }
} 