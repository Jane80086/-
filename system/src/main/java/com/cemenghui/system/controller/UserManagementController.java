package com.cemenghui.system.controller;

import com.cemenghui.system.dto.UserListDTO;
import com.cemenghui.system.dto.UserQueryDTO;
import com.cemenghui.system.entity.EnterpriseUser;
import com.cemenghui.system.entity.UserTemplate;
import com.cemenghui.system.service.UserManagementService;
import com.cemenghui.common.JWTUtil;
import com.cemenghui.system.vo.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.ByteArrayOutputStream;
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
    public ResponseEntity<ResultVO<UserListDTO>> getUserList(@Valid UserQueryDTO query,
                                                             @RequestHeader("Authorization") String token) {
        // 验证超级管理员权限
        if (!isSuperAdmin(token)) {
            return new ResponseEntity<>(ResultVO.unauthorized("无权限访问"), HttpStatus.UNAUTHORIZED);
        }

        UserListDTO userList = userManagementService.getUserList(query);
        return new ResponseEntity<>(ResultVO.success(userList), HttpStatus.OK);
    }

    /**
     * 导出用户列表为Excel
     */
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportUsers() {
        try {
            byte[] excelData = userManagementService.exportUsersToExcel();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "用户列表.xlsx");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelData);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 普通添加用户（不依赖模板）
     */
    @PostMapping
    public ResponseEntity<ResultVO<String>> createUser(@Valid @RequestBody EnterpriseUser user,
                                                       @RequestHeader("Authorization") String token) {
        if (!isSuperAdmin(token)) {
            return new ResponseEntity<>(ResultVO.unauthorized("无权限操作"), HttpStatus.UNAUTHORIZED);
        }
        String userId = userManagementService.createUser(user);
        return new ResponseEntity<>(ResultVO.success(userId), HttpStatus.CREATED);
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
        boolean result = userManagementService.updateUser(user);
        return new ResponseEntity<>(ResultVO.success(result), HttpStatus.OK);
    }

    /**
     * 获取用户修改历史
     */
    @GetMapping("/{userId}/history")
    public ResponseEntity<ResultVO<List>> getUserModifyHistory(@PathVariable String userId,
                                                               @RequestHeader("Authorization") String token) {
        if (!isSuperAdmin(token)) {
            return new ResponseEntity<>(ResultVO.unauthorized("无权限访问"), HttpStatus.UNAUTHORIZED);
        }

        List history = userManagementService.getUserModifyHistory(userId);
        return new ResponseEntity<>(ResultVO.success(history), HttpStatus.OK);
    }

    /**
     * 分配用户权限
     */
    @PostMapping("/{userId}/permissions")
    public ResponseEntity<ResultVO<Boolean>> assignPermissions(@PathVariable String userId,
                                                               @RequestBody Map<String, Set<String>> permissions,
                                                               @RequestHeader("Authorization") String token) {
        if (!isSuperAdmin(token)) {
            return new ResponseEntity<>(ResultVO.unauthorized("无权限操作"), HttpStatus.UNAUTHORIZED);
        }

        boolean result = userManagementService.assignPermissions(userId, permissions.get("permissions"));
        return new ResponseEntity<>(ResultVO.success(result), HttpStatus.OK);
    }

    /**
     * 继承角色权限
     */
    @PostMapping("/{userId}/roles/{roleName}/permissions")
    public ResponseEntity<ResultVO<Boolean>> inheritRolePermissions(@PathVariable String userId,
                                                                    @PathVariable String roleName,
                                                                    @RequestHeader("Authorization") String token) {
        if (!isSuperAdmin(token)) {
            return new ResponseEntity<>(ResultVO.unauthorized("无权限操作"), HttpStatus.UNAUTHORIZED);
        }

        boolean result = userManagementService.inheritRolePermissions(userId, roleName);
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
    public ResponseEntity<ResultVO<Boolean>> deleteUser(@PathVariable String id, @RequestHeader("Authorization") String token) {
        if (!isSuperAdmin(token)) {
            return new ResponseEntity<>(ResultVO.unauthorized("无权限操作"), HttpStatus.UNAUTHORIZED);
        }
        boolean result = userManagementService.deleteUser(id);
        return new ResponseEntity<>(ResultVO.success(result), HttpStatus.OK);
    }

    /**
     * 根据userId修改用户信息（适配前端RESTful风格）
     */
    @PutMapping("/{userId}")
    public ResponseEntity<ResultVO<Boolean>> updateUserById(@PathVariable String userId,
                                                            @Valid @RequestBody EnterpriseUser user,
                                                            @RequestHeader("Authorization") String token) {
        if (!isSuperAdmin(token)) {
            return new ResponseEntity<>(ResultVO.unauthorized("无权限操作"), HttpStatus.UNAUTHORIZED);
        }
        user.setUserId(userId); // 确保userId正确
        boolean result = userManagementService.updateUser(user);
        return new ResponseEntity<>(ResultVO.success(result), HttpStatus.OK);
    }

    // 辅助方法：从令牌中获取用户ID
    private String getUserIdFromToken(String token) {
        try {
            String account = jwtUtil.getAccountFromToken(token);
            // 实际项目中应通过account查询userId
            return "admin_123"; // 示例返回值
        } catch (Exception e) {
            return null;
        }
    }

    // 辅助方法：验证是否为超级管理员
    private boolean isSuperAdmin(String token) {
        try {
            if (token != null) token = token.trim();
            String account = jwtUtil.getAccountFromToken(token);
            System.out.println("isSuperAdmin校验，token: [" + token + "]，解析账号: [" + account + "]");
            return account != null && account.startsWith("0000");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}