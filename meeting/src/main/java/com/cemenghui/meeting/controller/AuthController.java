package com.cemenghui.meeting.controller;

import com.cemenghui.meeting.bean.ApiResponse;
import com.cemenghui.entity.User;
import com.cemenghui.meeting.bean.UserLoginRequest;
import com.cemenghui.meeting.bean.UserRegisterRequest;
import com.cemenghui.meeting.service.UserService;
import com.cemenghui.common.JWTUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "认证管理", description = "用户登录、注册、token刷新等认证相关接口")
public class AuthController {

    private final UserService userService;
    private final JWTUtil jwtUtil;

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户登录并返回JWT token")
    public ApiResponse<Map<String, Object>> login(@Valid @RequestBody UserLoginRequest request) {
        try {
            String token = userService.login(request);
            String refreshToken = jwtUtil.generateRefreshToken(request.getUsername());
            User user = userService.getUserByUsername(request.getUsername());
            Map<String, Object> result = new HashMap<>();
            result.put("token", token);
            result.put("refreshToken", refreshToken);
            result.put("user", user);
            log.info("用户 {} 登录成功", request.getUsername());
            return ApiResponse.success(result);
        } catch (IllegalArgumentException e) {
            log.warn("用户 {} 登录失败: {}", request.getUsername(), e.getMessage());
            return ApiResponse.error(401, e.getMessage());
        }
    }

    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "新用户注册")
    public ApiResponse<User> register(@Valid @RequestBody UserRegisterRequest request) {
        try {
            User user = userService.register(request);
            log.info("用户 {} 注册成功", request.getUsername());
            return ApiResponse.success(user);
        } catch (IllegalArgumentException e) {
            log.warn("用户 {} 注册失败: {}", request.getUsername(), e.getMessage());
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @PostMapping("/refresh")
    @Operation(summary = "刷新Token", description = "使用刷新token获取新的访问token")
    public ApiResponse<Map<String, String>> refreshToken(@RequestHeader("Authorization") String refreshToken) {
        try {
            String token = refreshToken.replace("Bearer ", "");
            
            if (jwtUtil.validateToken(token)) {
                String username = jwtUtil.getUsernameFromToken(token);
                // 获取用户信息以获取用户ID和角色
                User user = userService.getUserByUsername(username);
                if (user == null || user.getId() == null) {
                    return ApiResponse.error(401, "用户信息无效");
                }
                
                List<String> roles = List.of(user.getUserType() != null ? user.getUserType() : "USER");
                String newToken = jwtUtil.generateToken(user.getId(), user.getUsername(), roles);
                String newRefreshToken = jwtUtil.generateRefreshToken(username);
                
                Map<String, String> result = new HashMap<>();
                result.put("token", newToken);
                result.put("refreshToken", newRefreshToken);
                
                return ApiResponse.success(result);
            } else {
                return ApiResponse.error(401, "刷新token无效");
            }
        } catch (Exception e) {
            log.error("刷新token失败: {}", e.getMessage());
            return ApiResponse.error(401, "刷新token失败");
        }
    }

    @PostMapping("/logout")
    @Operation(summary = "用户登出", description = "用户登出")
    public ApiResponse<String> logout() {
        try {
            SecurityContextHolder.clearContext();
            return ApiResponse.success("登出成功");
        } catch (Exception e) {
            log.error("登出失败: {}", e.getMessage());
            return ApiResponse.error(500, "登出失败");
        }
    }

    @GetMapping("/profile")
    @Operation(summary = "获取用户信息", description = "获取当前登录用户的详细信息")
    public ApiResponse<User> getProfile() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User user = userService.getUserByUsername(username);
            return ApiResponse.success(user);
        } catch (Exception e) {
            log.error("获取用户信息失败: {}", e.getMessage());
            return ApiResponse.error(500, "获取用户信息失败");
        }
    }
} 