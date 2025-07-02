package com.cemenghui.meeting.java.com.example.demo.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.cemenghui.meeting.controller.DatabaseController;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DatabaseController 单元测试")
public class DatabaseControllerTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private DatabaseController databaseController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(databaseController).build();
    }

    @Test
    @DisplayName("数据库初始化-成功")
    public void testInitDatabase_Success() throws Exception {
        // mock JdbcTemplate 正常执行
        doNothing().when(jdbcTemplate).execute(anyString());

        mockMvc.perform(post("/api/database/init"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("数据库初始化完成"));

        // 验证 JdbcTemplate.execute 被调用
        verify(jdbcTemplate, atLeastOnce()).execute(anyString());
    }

    @Test
    @DisplayName("数据库初始化-部分SQL执行失败但忽略重复错误")
    public void testInitDatabase_PartialSuccess() throws Exception {
        // mock 第一次执行成功，第二次执行抛出重复键错误
        doNothing().when(jdbcTemplate).execute("CREATE TABLE test1");
        doThrow(new RuntimeException("duplicate key")).when(jdbcTemplate).execute("CREATE TABLE test2");

        mockMvc.perform(post("/api/database/init"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("数据库初始化完成"));
    }

    @Test
    @DisplayName("数据库初始化-JdbcTemplate执行异常")
    public void testInitDatabase_JdbcException() throws Exception {
        // mock JdbcTemplate 抛出异常
        doThrow(new RuntimeException("Database error")).when(jdbcTemplate).execute(anyString());

        mockMvc.perform(post("/api/database/init"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("数据库初始化完成"));
    }
} 