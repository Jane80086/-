package com.cemenghui.meeting.controller;

import com.cemenghui.meeting.entity.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/database")
@CrossOrigin(origins = "http://localhost:5173")
public class DatabaseController {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @PostMapping("/init")
    public ApiResponse<String> initDatabase() {
        try {
            // 读取SQL脚本
            ClassPathResource resource = new ClassPathResource("sql/init_data.sql");
            String sql = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            
            // 分割SQL语句
            String[] statements = sql.split(";");
            
            int successCount = 0;
            int totalCount = 0;
            
            for (String statement : statements) {
                statement = statement.trim();
                if (!statement.isEmpty() && !statement.startsWith("--")) {
                    try {
                        jdbcTemplate.execute(statement);
                        successCount++;
                    } catch (Exception e) {
                        // 忽略已存在的表等错误
                        if (e.getMessage().contains("already exists") || 
                            e.getMessage().contains("duplicate key")) {
                            successCount++;
                        }
                    }
                    totalCount++;
                }
            }
            
            return ApiResponse.success("数据库初始化完成", 
                String.format("成功执行 %d/%d 条SQL语句", successCount, totalCount));
        } catch (IOException e) {
            return ApiResponse.error("读取SQL脚本失败: " + e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("数据库初始化失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/check-tables")
    public ApiResponse<Map<String, Object>> checkTables() {
        try {
            // 检查用户表
            List<Map<String, Object>> userTable = jdbcTemplate.queryForList(
                "SELECT COUNT(*) as count FROM user_info"
            );
            
            // 检查会议表
            List<Map<String, Object>> meetingTable = jdbcTemplate.queryForList(
                "SELECT COUNT(*) as count FROM meeting_info"
            );
            
            Map<String, Object> result = Map.of(
                "user_count", userTable.get(0).get("count"),
                "meeting_count", meetingTable.get(0).get("count")
            );
            
            return ApiResponse.success("表检查完成", result);
        } catch (Exception e) {
            return ApiResponse.error("表检查失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/create-admin")
    public ApiResponse<String> createAdminUser() {
        try {
            // 创建管理员用户
            String sql = "INSERT INTO user_info (username, password, real_name, email, phone, user_type, status) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?) " +
                        "ON CONFLICT (username) DO NOTHING";
            
            String password = org.springframework.util.DigestUtils.md5DigestAsHex("123456".getBytes());
            
            int result = jdbcTemplate.update(sql, 
                "admin", password, "系统管理员", "admin@company.com", "13800138001", "ADMIN", 1);
            
            if (result > 0) {
                return ApiResponse.success("管理员用户创建成功", "用户名: admin, 密码: 123456");
            } else {
                return ApiResponse.success("管理员用户已存在", "用户名: admin, 密码: 123456");
            }
        } catch (Exception e) {
            return ApiResponse.error("创建管理员用户失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/reset-password")
    public ApiResponse<String> resetPassword(@RequestBody Map<String, String> request) {
        try {
            String username = request.get("username");
            String newPassword = request.get("password");
            
            if (username == null || newPassword == null) {
                return ApiResponse.error("用户名和密码不能为空");
            }
            
            String encryptedPassword = org.springframework.util.DigestUtils.md5DigestAsHex(newPassword.getBytes());
            
            int result = jdbcTemplate.update(
                "UPDATE user_info SET password = ? WHERE username = ?",
                encryptedPassword, username
            );
            
            if (result > 0) {
                return ApiResponse.success("密码重置成功", 
                    String.format("用户 %s 的密码已重置为: %s", username, newPassword));
            } else {
                return ApiResponse.error("用户不存在: " + username);
            }
        } catch (Exception e) {
            return ApiResponse.error("密码重置失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/fix")
    public ApiResponse<String> fixDatabase() {
        try {
            int totalFixed = 0;
            
            // 1. 修复密码格式 - 将明文密码转换为MD5
            String encryptedPassword = org.springframework.util.DigestUtils.md5DigestAsHex("123456".getBytes());
            int passwordFixed = jdbcTemplate.update(
                "UPDATE user_info SET password = ? WHERE password = '123456'",
                encryptedPassword
            );
            totalFixed += passwordFixed;
            
            // 2. 修复用户类型
            int adminFixed = jdbcTemplate.update(
                "UPDATE user_info SET user_type = 'ADMIN' WHERE username LIKE 'admin%'"
            );
            totalFixed += adminFixed;
            
            int enterpriseFixed = jdbcTemplate.update(
                "UPDATE user_info SET user_type = 'ENTERPRISE' WHERE username LIKE 'enterprise%'"
            );
            totalFixed += enterpriseFixed;
            
            int normalFixed = jdbcTemplate.update(
                "UPDATE user_info SET user_type = 'NORMAL' WHERE username LIKE 'user%'"
            );
            totalFixed += normalFixed;
            
            // 3. 修复真实姓名
            int nameFixed = jdbcTemplate.update(
                "UPDATE user_info SET real_name = username WHERE real_name IS NULL"
            );
            totalFixed += nameFixed;
            
            return ApiResponse.success("数据库修复完成", 
                String.format("修复了 %d 条记录（密码: %d, 用户类型: %d, 姓名: %d）", 
                    totalFixed, passwordFixed, adminFixed + enterpriseFixed + normalFixed, nameFixed));
        } catch (Exception e) {
            return ApiResponse.error("数据库修复失败: " + e.getMessage());
        }
    }
} 