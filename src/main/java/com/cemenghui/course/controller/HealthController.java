package com.cemenghui.course.controller;

import com.cemenghui.course.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    @Autowired
    private DataSource dataSource;

    @GetMapping
    public ResponseEntity<Result> healthCheck() {
        Map<String, Object> healthInfo = new HashMap<>();
        healthInfo.put("status", "UP");
        healthInfo.put("timestamp", LocalDateTime.now());
        healthInfo.put("service", "course-management");
        
        // 检查数据库连接
        try (Connection connection = dataSource.getConnection()) {
            healthInfo.put("database", "UP");
            healthInfo.put("databaseUrl", connection.getMetaData().getURL());
        } catch (Exception e) {
            healthInfo.put("database", "DOWN");
            healthInfo.put("databaseError", e.getMessage());
        }
        
        return ResponseEntity.ok(Result.success("系统健康检查", healthInfo));
    }

    @GetMapping("/ping")
    public ResponseEntity<Result> ping() {
        return ResponseEntity.ok(Result.success("pong", null));
    }
} 