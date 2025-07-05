package com.cemenghui.meeting.controller;

import com.cemenghui.meeting.bean.ApiResponse;
import com.cemenghui.entity.User;
import com.cemenghui.meeting.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "用户管理", description = "用户信息管理相关接口")
public class UserController {
    
    private final UserService userService;

    @GetMapping("/profile")
    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的详细信息")
    public ApiResponse<User> getCurrentUserInfo() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User user = userService.getUserByUsername(username);
            if (user != null) {
                return ApiResponse.success(user);
            } else {
                return ApiResponse.error(404, "用户不存在");
            }
        } catch (Exception e) {
            log.error("获取用户信息失败", e);
            return ApiResponse.error(500, "获取用户信息失败");
        }
    }

    @PutMapping("/profile")
    @Operation(summary = "更新用户信息", description = "更新当前登录用户的基本信息")
    public ApiResponse<Boolean> updateUserInfo(@Valid @RequestBody User user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User currentUser = userService.getUserByUsername(username);
        if (currentUser == null) {
            log.warn("用户 {} 更新信息失败：用户不存在", username);
            return ApiResponse.error(404, "用户不存在");
        }
        user.setId(currentUser.getId());
        boolean success = userService.updateUserInfo(user);
        if (success) {
            log.info("用户 {} 成功更新信息", username);
            return ApiResponse.success(true);
        } else {
            log.warn("用户 {} 更新信息失败", username);
            return ApiResponse.error(500, "用户信息更新失败");
        }
    }

    @PutMapping("/password")
    @Operation(summary = "修改密码", description = "修改当前登录用户的密码")
    public ApiResponse<Boolean> updatePassword(@RequestBody Map<String, String> request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.getUserByUsername(username);
        if (user == null) {
            log.warn("用户 {} 修改密码失败：用户不存在", username);
            return ApiResponse.error(404, "用户不存在");
        }
        if (request == null || !request.containsKey("newPassword")) {
            log.warn("用户 {} 修改密码失败：新密码参数缺失", username);
            return ApiResponse.error(400, "新密码不能为空");
        }
        String newPassword = request.get("newPassword");
        if (newPassword == null || newPassword.trim().isEmpty()) {
            log.warn("用户 {} 修改密码失败：新密码为空", username);
            return ApiResponse.error(400, "新密码不能为空");
        }
        boolean success = userService.updatePassword(user.getId(), newPassword);
        if (success) {
            log.info("用户 {} 成功修改密码", username);
            return ApiResponse.success(true);
        } else {
            log.warn("用户 {} 修改密码失败", username);
            return ApiResponse.error(500, "密码修改失败");
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "获取用户列表", description = "获取所有用户列表（仅管理员）")
    public ApiResponse<List<User>> getUserList() {
        try {
            List<User> users = userService.getAllUsers();
            return ApiResponse.success(users);
        } catch (Exception e) {
            log.error("获取用户列表失败", e);
            return ApiResponse.error(500, "获取用户列表失败");
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    @Operation(summary = "获取用户详情", description = "根据用户ID获取用户详情")
    public ApiResponse<User> getUserById(@Parameter(description = "用户ID") @PathVariable Long id) {
        try {
            User user = userService.getUserById(id);
            if (user != null) {
                return ApiResponse.success(user);
            } else {
                return ApiResponse.error(404, "用户不存在");
            }
        } catch (Exception e) {
            log.error("获取用户详情失败", e);
            return ApiResponse.error(500, "获取用户详情失败");
        }
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "更新用户状态", description = "更新用户状态（仅管理员）")
    public ApiResponse<Boolean> updateUserStatus(
            @Parameter(description = "用户ID") @PathVariable Long id,
            @RequestBody Map<String, Integer> request) {
        if (request == null || !request.containsKey("status")) {
            log.warn("管理员更新用户 {} 状态失败：参数缺失", id);
            return ApiResponse.error(400, "状态不能为空");
        }
        Integer status = request.get("status");
        boolean success = userService.updateUserStatus(id, status);
        if (success) {
            log.info("管理员成功更新用户 {} 状态为 {}", id, status);
            return ApiResponse.success(true);
        } else {
            log.warn("管理员更新用户 {} 状态失败", id);
            return ApiResponse.error(500, "用户状态更新失败");
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "删除用户", description = "删除用户（仅管理员）")
    public ApiResponse<Boolean> deleteUser(@Parameter(description = "用户ID") @PathVariable Long id) {
        try {
            boolean success = userService.deleteUser(id);
            if (success) {
                return ApiResponse.success(true);
            } else {
                return ApiResponse.error(500, "删除用户失败");
            }
        } catch (Exception e) {
            log.error("删除用户失败", e);
            return ApiResponse.error(500, "删除用户失败");
        }
    }
} 