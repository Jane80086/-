package com.cemenghui.meeting.controller;

import com.cemenghui.meeting.bean.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/database")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "数据库管理", description = "数据库初始化、重置等管理接口")
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
} 