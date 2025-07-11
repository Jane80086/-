package com.cemenghui.news.service.impl;

import com.cemenghui.news.entity.UserOperationLog;
import com.cemenghui.news.entity.UserViewLog;
import com.cemenghui.news.mapper.UserOperationLogMapper;
import com.cemenghui.news.mapper.UserViewLogMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogServiceImplTest {

    @Mock
    private UserOperationLogMapper operationLogMapper;

    @Mock
    private UserViewLogMapper viewLogMapper;

    @InjectMocks
    private LogServiceImpl logService;

    private UserOperationLog operationLog;
    private UserViewLog viewLog;

    @BeforeEach
    void setUp() {
        // 准备测试数据
        operationLog = new UserOperationLog();
        operationLog.setId(1L);
        operationLog.setUserId(100L);
        operationLog.setOperationType("CREATE");
        operationLog.setResourceType("NEWS");
        operationLog.setResourceId(200L);
        operationLog.setResourceTitle("测试新闻标题");
        operationLog.setOperationDesc("创建新闻");
        operationLog.setOldValue(null);
        operationLog.setNewValue("新闻内容");
        operationLog.setIpAddress("192.168.1.1");
        operationLog.setOperationTime(LocalDateTime.now());
        operationLog.setOperationResult(1);

        viewLog = new UserViewLog();
        viewLog.setId(1L);
        viewLog.setUserId(100L);
        viewLog.setResourceType("NEWS");
        viewLog.setNewsId(200L);
        viewLog.setResourceTitle("测试新闻标题");
        viewLog.setViewTime(LocalDateTime.now());
        viewLog.setIpAddress("192.168.1.1");
        viewLog.setUserAgent("Mozilla/5.0");
        viewLog.setSessionId("session-123");
    }

    // ==================== recordOperation 正常情况测试 ====================

    @Test
    void recordOperation_WithValidOperationLog_ShouldSucceed() {
        // Given
        when(operationLogMapper.insert(any(UserOperationLog.class))).thenReturn(1);

        // When
        logService.recordOperation(operationLog);

        // Then
        verify(operationLogMapper, times(1)).insert(operationLog);
    }

    @Test
    void recordOperation_WithMinimalValidData_ShouldSucceed() {
        // Given
        UserOperationLog minimalLog = new UserOperationLog();
        minimalLog.setUserId(1L);
        minimalLog.setOperationType("VIEW");
        minimalLog.setResourceType("NEWS");
        minimalLog.setResourceId(1L);
        minimalLog.setOperationTime(LocalDateTime.now());
        minimalLog.setOperationResult(1);

        when(operationLogMapper.insert(any(UserOperationLog.class))).thenReturn(1);

        // When
        logService.recordOperation(minimalLog);

        // Then
        verify(operationLogMapper, times(1)).insert(minimalLog);
    }

    @Test
    void recordOperation_WithCompleteData_ShouldSucceed() {
        // Given
        when(operationLogMapper.insert(any(UserOperationLog.class))).thenReturn(1);

        // When
        logService.recordOperation(operationLog);

        // Then
        verify(operationLogMapper, times(1)).insert(operationLog);
    }

    // ==================== recordOperation 异常情况测试 ====================

    @Test
    void recordOperation_WithNullOperationLog_ShouldHandleException() {
        // Given
        when(operationLogMapper.insert(null)).thenThrow(new IllegalArgumentException("操作日志不能为空"));

        // When
        logService.recordOperation(null);

        // Then
        verify(operationLogMapper, times(1)).insert(null);
        // 验证异常被捕获，方法正常返回
    }

    @Test
    void recordOperation_WhenMapperThrowsException_ShouldHandleException() {
        // Given
        when(operationLogMapper.insert(any(UserOperationLog.class)))
                .thenThrow(new RuntimeException("数据库连接异常"));

        // When
        logService.recordOperation(operationLog);

        // Then
        verify(operationLogMapper, times(1)).insert(operationLog);
        // 验证异常被捕获，方法正常返回
    }

    @Test
    void recordOperation_WhenDatabaseConstraintViolation_ShouldHandleException() {
        // Given
        when(operationLogMapper.insert(any(UserOperationLog.class)))
                .thenThrow(new RuntimeException("外键约束违反"));

        // When
        logService.recordOperation(operationLog);

        // Then
        verify(operationLogMapper, times(1)).insert(operationLog);
    }

    // ==================== recordOperation 边界条件测试 ====================

    @Test
    void recordOperation_WithEmptyStringFields_ShouldSucceed() {
        // Given
        UserOperationLog emptyFieldsLog = new UserOperationLog();
        emptyFieldsLog.setUserId(1L);
        emptyFieldsLog.setOperationType("");
        emptyFieldsLog.setResourceType("");
        emptyFieldsLog.setResourceId(1L);
        emptyFieldsLog.setResourceTitle("");
        emptyFieldsLog.setOperationDesc("");
        emptyFieldsLog.setOldValue("");
        emptyFieldsLog.setNewValue("");
        emptyFieldsLog.setIpAddress("");
        emptyFieldsLog.setOperationTime(LocalDateTime.now());

        when(operationLogMapper.insert(any(UserOperationLog.class))).thenReturn(1);

        // When
        logService.recordOperation(emptyFieldsLog);

        // Then
        verify(operationLogMapper, times(1)).insert(emptyFieldsLog);
    }

    @Test
    void recordOperation_WithMaxLengthStrings_ShouldSucceed() {
        // Given
        UserOperationLog maxLengthLog = new UserOperationLog();
        maxLengthLog.setUserId(Long.MAX_VALUE);
        maxLengthLog.setOperationType("A".repeat(255)); // 假设最大长度为255
        maxLengthLog.setResourceType("NEWS");
        maxLengthLog.setResourceId(Long.MAX_VALUE);
        maxLengthLog.setResourceTitle("标题".repeat(100));
        maxLengthLog.setOperationDesc("描述".repeat(500));
        maxLengthLog.setOperationTime(LocalDateTime.now());

        when(operationLogMapper.insert(any(UserOperationLog.class))).thenReturn(1);

        // When
        logService.recordOperation(maxLengthLog);

        // Then
        verify(operationLogMapper, times(1)).insert(maxLengthLog);
    }

    @Test
    void recordOperation_WithZeroIds_ShouldSucceed() {
        // Given
        UserOperationLog zeroIdLog = new UserOperationLog();
        zeroIdLog.setUserId(0L);
        zeroIdLog.setOperationType("VIEW");
        zeroIdLog.setResourceType("NEWS");
        zeroIdLog.setResourceId(0L);
        zeroIdLog.setOperationTime(LocalDateTime.now());

        when(operationLogMapper.insert(any(UserOperationLog.class))).thenReturn(1);

        // When
        logService.recordOperation(zeroIdLog);

        // Then
        verify(operationLogMapper, times(1)).insert(zeroIdLog);
    }

    @Test
    void recordOperation_WithNegativeIds_ShouldSucceed() {
        // Given
        UserOperationLog negativeIdLog = new UserOperationLog();
        negativeIdLog.setUserId(-1L);
        negativeIdLog.setOperationType("DELETE");
        negativeIdLog.setResourceType("NEWS");
        negativeIdLog.setResourceId(-1L);
        negativeIdLog.setOperationTime(LocalDateTime.now());

        when(operationLogMapper.insert(any(UserOperationLog.class))).thenReturn(1);

        // When
        logService.recordOperation(negativeIdLog);

        // Then
        verify(operationLogMapper, times(1)).insert(negativeIdLog);
    }

    // ==================== recordView 正常情况测试 ====================

    @Test
    void recordView_WithValidViewLog_ShouldSucceed() {
        // Given
        when(viewLogMapper.insert(any(UserViewLog.class))).thenReturn(1);

        // When
        logService.recordView(viewLog);

        // Then
        verify(viewLogMapper, times(1)).insert(viewLog);
    }

    @Test
    void recordView_WithMinimalValidData_ShouldSucceed() {
        // Given
        UserViewLog minimalLog = new UserViewLog();
        minimalLog.setUserId(1L);
        minimalLog.setResourceType("NEWS");
        minimalLog.setNewsId(1L);
        minimalLog.setViewTime(LocalDateTime.now());

        when(viewLogMapper.insert(any(UserViewLog.class))).thenReturn(1);

        // When
        logService.recordView(minimalLog);

        // Then
        verify(viewLogMapper, times(1)).insert(minimalLog);
    }

    @Test
    void recordView_WithCompleteData_ShouldSucceed() {
        // Given
        when(viewLogMapper.insert(any(UserViewLog.class))).thenReturn(1);

        // When
        logService.recordView(viewLog);

        // Then
        verify(viewLogMapper, times(1)).insert(viewLog);
    }

    // ==================== recordView 异常情况测试 ====================

    @Test
    void recordView_WithNullViewLog_ShouldHandleException() {
        // Given
        when(viewLogMapper.insert(null)).thenThrow(new IllegalArgumentException("浏览日志不能为空"));

        // When
        logService.recordView(null);

        // Then
        verify(viewLogMapper, times(1)).insert(null);
        // 验证异常被捕获，方法正常返回
    }

    @Test
    void recordView_WhenMapperThrowsException_ShouldHandleException() {
        // Given
        when(viewLogMapper.insert(any(UserViewLog.class)))
                .thenThrow(new RuntimeException("数据库连接异常"));

        // When
        logService.recordView(viewLog);

        // Then
        verify(viewLogMapper, times(1)).insert(viewLog);
        // 验证异常被捕获，方法正常返回
    }

    @Test
    void recordView_WhenDatabaseConstraintViolation_ShouldHandleException() {
        // Given
        when(viewLogMapper.insert(any(UserViewLog.class)))
                .thenThrow(new RuntimeException("外键约束违反"));

        // When
        logService.recordView(viewLog);

        // Then
        verify(viewLogMapper, times(1)).insert(viewLog);
    }

    // ==================== recordView 边界条件测试 ====================

    @Test
    void recordView_WithEmptyStringFields_ShouldSucceed() {
        // Given
        UserViewLog emptyFieldsLog = new UserViewLog();
        emptyFieldsLog.setUserId(1L);
        emptyFieldsLog.setResourceType("");
        emptyFieldsLog.setNewsId(1L);
        emptyFieldsLog.setResourceTitle("");
        emptyFieldsLog.setViewTime(LocalDateTime.now());
        emptyFieldsLog.setIpAddress("");
        emptyFieldsLog.setUserAgent("");
        emptyFieldsLog.setSessionId("");

        when(viewLogMapper.insert(any(UserViewLog.class))).thenReturn(1);

        // When
        logService.recordView(emptyFieldsLog);

        // Then
        verify(viewLogMapper, times(1)).insert(emptyFieldsLog);
    }

    @Test
    void recordView_WithMaxLengthStrings_ShouldSucceed() {
        // Given
        UserViewLog maxLengthLog = new UserViewLog();
        maxLengthLog.setUserId(Long.MAX_VALUE);
        maxLengthLog.setResourceType("NEWS");
        maxLengthLog.setNewsId(Long.MAX_VALUE);
        maxLengthLog.setResourceTitle("标题".repeat(100));
        maxLengthLog.setViewTime(LocalDateTime.now());
        maxLengthLog.setIpAddress("255.255.255.255");
        maxLengthLog.setUserAgent("Mozilla/5.0".repeat(50));
        maxLengthLog.setSessionId("session-".repeat(20));

        when(viewLogMapper.insert(any(UserViewLog.class))).thenReturn(1);

        // When
        logService.recordView(maxLengthLog);

        // Then
        verify(viewLogMapper, times(1)).insert(maxLengthLog);
    }

    @Test
    void recordView_WithZeroIds_ShouldSucceed() {
        // Given
        UserViewLog zeroIdLog = new UserViewLog();
        zeroIdLog.setUserId(0L);
        zeroIdLog.setResourceType("NEWS");
        zeroIdLog.setNewsId(0L);
        zeroIdLog.setViewTime(LocalDateTime.now());

        when(viewLogMapper.insert(any(UserViewLog.class))).thenReturn(1);

        // When
        logService.recordView(zeroIdLog);

        // Then
        verify(viewLogMapper, times(1)).insert(zeroIdLog);
    }

    @Test
    void recordView_WithNegativeIds_ShouldSucceed() {
        // Given
        UserViewLog negativeIdLog = new UserViewLog();
        negativeIdLog.setUserId(-1L);
        negativeIdLog.setResourceType("NEWS");
        negativeIdLog.setNewsId(-1L);
        negativeIdLog.setViewTime(LocalDateTime.now());

        when(viewLogMapper.insert(any(UserViewLog.class))).thenReturn(1);

        // When
        logService.recordView(negativeIdLog);

        // Then
        verify(viewLogMapper, times(1)).insert(negativeIdLog);
    }

    // ==================== 特殊时间边界测试 ====================

    @Test
    void recordOperation_WithMinDateTime_ShouldSucceed() {
        // Given
        operationLog.setOperationTime(LocalDateTime.MIN);
        when(operationLogMapper.insert(any(UserOperationLog.class))).thenReturn(1);

        // When
        logService.recordOperation(operationLog);

        // Then
        verify(operationLogMapper, times(1)).insert(operationLog);
    }

    @Test
    void recordOperation_WithMaxDateTime_ShouldSucceed() {
        // Given
        operationLog.setOperationTime(LocalDateTime.MAX);
        when(operationLogMapper.insert(any(UserOperationLog.class))).thenReturn(1);

        // When
        logService.recordOperation(operationLog);

        // Then
        verify(operationLogMapper, times(1)).insert(operationLog);
    }

    @Test
    void recordView_WithMinDateTime_ShouldSucceed() {
        // Given
        viewLog.setViewTime(LocalDateTime.MIN);
        when(viewLogMapper.insert(any(UserViewLog.class))).thenReturn(1);

        // When
        logService.recordView(viewLog);

        // Then
        verify(viewLogMapper, times(1)).insert(viewLog);
    }

    @Test
    void recordView_WithMaxDateTime_ShouldSucceed() {
        // Given
        viewLog.setViewTime(LocalDateTime.MAX);
        when(viewLogMapper.insert(any(UserViewLog.class))).thenReturn(1);

        // When
        logService.recordView(viewLog);

        // Then
        verify(viewLogMapper, times(1)).insert(viewLog);
    }

    // ==================== 特殊字符测试 ====================

    @Test
    void recordOperation_WithSpecialCharacters_ShouldSucceed() {
        // Given
        operationLog.setResourceTitle("特殊字符测试!@#$%^&*()");
        operationLog.setOperationDesc("包含特殊字符的描述：<script>alert('test')</script>");
        operationLog.setOldValue("旧值：中文测试");
        operationLog.setNewValue("新值：English Test");

        when(operationLogMapper.insert(any(UserOperationLog.class))).thenReturn(1);

        // When
        logService.recordOperation(operationLog);

        // Then
        verify(operationLogMapper, times(1)).insert(operationLog);
    }

    @Test
    void recordView_WithSpecialCharacters_ShouldSucceed() {
        // Given
        viewLog.setResourceTitle("特殊字符测试!@#$%^&*()");
        viewLog.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");

        when(viewLogMapper.insert(any(UserViewLog.class))).thenReturn(1);

        // When
        logService.recordView(viewLog);

        // Then
        verify(viewLogMapper, times(1)).insert(viewLog);
    }
}