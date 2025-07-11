package com.cemenghui.system.controller;

import com.cemenghui.system.dto.UserHistoryListDTO;
import com.cemenghui.system.dto.UserHistoryQueryDTO;
import com.cemenghui.system.dto.UserListDTO;
import com.cemenghui.system.dto.UserQueryDTO;
import com.cemenghui.system.entity.EnterpriseUser;
import com.cemenghui.system.entity.UserTemplate;
import com.cemenghui.system.service.UserManagementService;
import com.cemenghui.common.JWTUtil;
import com.cemenghui.system.vo.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/admin/users")
public class UserManagementController {

    @Autowired
    private UserManagementService userManagementService;

    @Autowired
    private JWTUtil jwtUtil;

    /**
     * 获取用户列表（仅超级管理员可访问）
     */
    @GetMapping
    public Object getUserList(@Valid UserQueryDTO query,
                                                             @RequestHeader("Authorization") String token) {
        // 验证超级管理员权限
        if (!isSuperAdmin(token)) {
            return new ResponseEntity<>(ResultVO.unauthorized("无权限访问"), HttpStatus.UNAUTHORIZED);
        }
        try {
            UserListDTO userList = userManagementService.getUserList(query);
            return new ResponseEntity<>(ResultVO.success(userList), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(ResultVO.error("获取用户列表失败：" + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 导出用户列表为Excel
     */
    @GetMapping("/export")
    public void exportUserList(UserQueryDTO query,
                               @RequestHeader("Authorization") String token,
                               HttpServletResponse response) throws IOException {
        if (!isSuperAdmin(token)) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "无权限访问");
            return;
        }

        userManagementService.exportUserList(query, response);
    }

    /**
     * 普通添加用户（不依赖模板）
     */
    @PostMapping
    public Object createUser(@Valid @RequestBody EnterpriseUser user,
                                                       @RequestHeader("Authorization") String token) {
        if (!isSuperAdmin(token)) {
            return new ResponseEntity<>(ResultVO.unauthorized("无权限操作"), HttpStatus.UNAUTHORIZED);
        }
        try {
            Long userId = userManagementService.createUser(user);
            return new ResponseEntity<>(ResultVO.success(userId), HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(ResultVO.error("创建用户失败：" + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 修改用户信息
     */
    @PutMapping
    public ResponseEntity<ResultVO<Boolean>> updateUser(@Valid @RequestBody EnterpriseUser user,
                                                        @RequestHeader("Authorization") String token) {
        if (!isSuperAdmin(token)) {
            return new ResponseEntity<>(ResultVO.unauthorized("无权限操作"), HttpStatus.UNAUTHORIZED);
        }
        try {
            System.out.println("收到更新用户请求: " + user);
            boolean result = userManagementService.updateUser(user);
            System.out.println("更新结果: " + result);
            return new ResponseEntity<>(ResultVO.success(result), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(ResultVO.error("更新用户失败：" + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 获取用户修改历史
     */
    @GetMapping("/{userId}/history")
    public ResponseEntity<ResultVO<List>> getUserModifyHistory(@PathVariable Long userId,
                                                               @RequestHeader("Authorization") String token) {
        if (!isSuperAdmin(token)) {
            return new ResponseEntity<>(ResultVO.unauthorized("无权限访问"), HttpStatus.UNAUTHORIZED);
        }

        List history = userManagementService.getUserModifyHistory(userId);
        return new ResponseEntity<>(ResultVO.success(history), HttpStatus.OK);
    }

    /**
     * 分页查询用户修改历史
     */
    @GetMapping("/history")
    public ResponseEntity<ResultVO<UserHistoryListDTO>> getUserModifyHistoryPaged(
            UserHistoryQueryDTO query,
            @RequestHeader("Authorization") String token) {
        if (!isSuperAdmin(token)) {
            return new ResponseEntity<>(ResultVO.unauthorized("无权限访问"), HttpStatus.UNAUTHORIZED);
        }

        UserHistoryListDTO historyList = userManagementService.getUserModifyHistoryPaged(query);
        return new ResponseEntity<>(ResultVO.success(historyList), HttpStatus.OK);
    }

    /**
     * 恢复用户修改历史
     */
    @PostMapping("/history/{historyId}/restore")
    public ResponseEntity<ResultVO<Boolean>> restoreUserHistory(@PathVariable Long historyId,
                                                                @RequestHeader("Authorization") String token) {
        if (!isSuperAdmin(token)) {
            return new ResponseEntity<>(ResultVO.unauthorized("无权限操作"), HttpStatus.UNAUTHORIZED);
        }

        boolean result = userManagementService.restoreUserHistory(historyId);
        return new ResponseEntity<>(ResultVO.success(result), HttpStatus.OK);
    }

    /**
     * 分配用户权限
     */
    @PostMapping("/{userId}/permissions")
    public ResponseEntity<ResultVO<Boolean>> assignPermissions(@PathVariable Long userId,
                                                               @RequestBody Map<String, Set<String>> permissions,
                                                               @RequestHeader("Authorization") String token) {
        if (!isSuperAdmin(token)) {
            return new ResponseEntity<>(ResultVO.unauthorized("无权限操作"), HttpStatus.UNAUTHORIZED);
        }

        boolean result = userManagementService.assignPermissions(userId, permissions.get("permissions"));
        return new ResponseEntity<>(ResultVO.success(result), HttpStatus.OK);
    }

    /**
     * 获取所有用户模板
     */
    @GetMapping("/templates")
    public ResponseEntity<ResultVO<List<UserTemplate>>> getAllTemplates(@RequestHeader("Authorization") String token) {
        if (!isSuperAdmin(token)) {
            return new ResponseEntity<>(ResultVO.unauthorized("无权限访问"), HttpStatus.UNAUTHORIZED);
        }

        List<UserTemplate> templates = userManagementService.getAllTemplates();
        return new ResponseEntity<>(ResultVO.success(templates), HttpStatus.OK);
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ResultVO<Boolean>> deleteUser(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        if (!isSuperAdmin(token)) {
            return new ResponseEntity<>(ResultVO.unauthorized("无权限操作"), HttpStatus.UNAUTHORIZED);
        }
        try {
            boolean result = userManagementService.deleteUser(id);
            return new ResponseEntity<>(ResultVO.success(result), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(ResultVO.error("删除用户失败：" + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 根据userId修改用户信息（适配前端RESTful风格）
     */
    @PutMapping("/{userId}")
    public ResponseEntity<ResultVO<Boolean>> updateUserById(@PathVariable Long userId,
                                                            @Valid @RequestBody EnterpriseUser user,
                                                            @RequestHeader("Authorization") String token) {
        if (!isSuperAdmin(token)) {
            return new ResponseEntity<>(ResultVO.unauthorized("无权限操作"), HttpStatus.UNAUTHORIZED);
        }
        try {
            user.setId(userId); // 确保userId正确
            boolean result = userManagementService.updateUser(user);
            return new ResponseEntity<>(ResultVO.success(result), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(ResultVO.error("更新用户失败：" + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 辅助方法：从令牌中获取用户ID
    private String getUserIdFromToken(String token) {
        try {
            String cleanToken = jwtUtil.extractTokenFromHeader(token);
            if (cleanToken == null) {
                return null;
            }
            String account = jwtUtil.getAccountFromToken(cleanToken);
            // 实际项目中应通过account查询userId
            return "admin_123"; // 示例返回值
        } catch (Exception e) {
            return null;
        }
    }

    // 辅助方法：验证是否为超级管理员
    private boolean isSuperAdmin(String token) {
        try {
            String cleanToken = jwtUtil.extractTokenFromHeader(token);
            if (cleanToken == null) {
                return false;
            }
            String account = jwtUtil.getAccountFromToken(cleanToken);
            System.out.println("isSuperAdmin校验，token: [" + cleanToken + "]，解析账号: [" + account + "]");
            return account != null && account.startsWith("0000");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}