package com.cemenghui.course.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    @Autowired
    private DataSource dataSource;

    @GetMapping("/db")
    public Map<String, Object> checkDatabase() {
        Map<String, Object> result = new HashMap<>();
        
        try (Connection connection = dataSource.getConnection()) {
            boolean isValid = connection.isValid(5);
            result.put("status", isValid ? "UP" : "DOWN");
            result.put("database", "KingBase8");
            result.put("message", isValid ? "数据库连接正常" : "数据库连接异常");
            result.put("timestamp", System.currentTimeMillis());
        } catch (SQLException e) {
            result.put("status", "DOWN");
            result.put("database", "KingBase8");
            result.put("message", "数据库连接失败: " + e.getMessage());
            result.put("error", e.getClass().getSimpleName());
            result.put("timestamp", System.currentTimeMillis());
        }
        
        return result;
    }

    @GetMapping("/system")
    public Map<String, Object> checkSystem() {
        Map<String, Object> result = new HashMap<>();
        
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        long maxMemory = runtime.maxMemory();
        
        result.put("status", "UP");
        result.put("application", "Course Management System");
        result.put("memory", Map.of(
            "total", totalMemory,
            "used", usedMemory,
            "free", freeMemory,
            "max", maxMemory,
            "usage", String.format("%.2f%%", (double) usedMemory / maxMemory * 100)
        ));
        result.put("timestamp", System.currentTimeMillis());
        
        return result;
    }
} 