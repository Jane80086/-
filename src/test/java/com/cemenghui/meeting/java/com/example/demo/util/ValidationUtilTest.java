package com.cemenghui.meeting.java.com.example.demo.util;

import com.cemenghui.meeting.bean.Meeting;

import com.cemenghui.meeting.util.ValidationUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ValidationUtil 单元测试类
 * 测试数据验证逻辑
 */
@DisplayName("数据验证工具测试")
public class ValidationUtilTest {

    private ValidationUtil validationUtil;
    private Meeting testMeeting;

    @BeforeEach
    void setUp() {
        validationUtil = new ValidationUtil();
        testMeeting = Meeting.builder()
                .meetingName("测试会议")
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(1).plusHours(2))
                .meetingContent("这是一个测试会议，用于验证数据验证功能。")
                .build();
    }

    @Test
    @DisplayName("测试会议创建验证 - 正常情况")
    public void testValidateMeetingCreate_Success() {
        // 执行测试
        String result = validationUtil.validateMeetingCreate(testMeeting);

        // 验证结果
        assertNull(result, "验证应该通过，返回null");
    }

    @Test
    @DisplayName("测试会议创建验证 - 会议为空")
    public void testValidateMeetingCreate_NullMeeting() {
        // 执行测试
        String result = validationUtil.validateMeetingCreate(null);

        // 验证结果
        assertNotNull(result);
        assertEquals("会议数据不能为空", result);
    }

    @Test
    @DisplayName("测试会议创建验证 - 会议名称为空")
    public void testValidateMeetingCreate_EmptyMeetingName() {
        // 准备测试数据
        testMeeting.setMeetingName("");

        // 执行测试
        String result = validationUtil.validateMeetingCreate(testMeeting);

        // 验证结果
        assertNotNull(result);
        assertTrue(result.contains("会议名称"));
    }

    @Test
    @DisplayName("测试会议创建验证 - 会议名称过短")
    public void testValidateMeetingCreate_ShortMeetingName() {
        // 准备测试数据
        testMeeting.setMeetingName("a");

        // 执行测试
        String result = validationUtil.validateMeetingCreate(testMeeting);

        // 验证结果
        assertNotNull(result);
        assertTrue(result.contains("会议名称"));
    }

    @Test
    @DisplayName("测试会议创建验证 - 会议名称过长")
    public void testValidateMeetingCreate_LongMeetingName() {
        // 准备测试数据
        StringBuilder longName = new StringBuilder();
        for (int i = 0; i < 101; i++) {
            longName.append("a");
        }
        testMeeting.setMeetingName(longName.toString());

        // 执行测试
        String result = validationUtil.validateMeetingCreate(testMeeting);

        // 验证结果
        assertNotNull(result);
        assertTrue(result.contains("会议名称"));
    }

    @Test
    @DisplayName("测试会议创建验证 - 开始时间为空")
    public void testValidateMeetingCreate_NullStartTime() {
        // 准备测试数据
        testMeeting.setStartTime(null);

        // 执行测试
        String result = validationUtil.validateMeetingCreate(testMeeting);

        // 验证结果
        assertNotNull(result);
        assertTrue(result.contains("开始时间"));
    }

    @Test
    @DisplayName("测试会议创建验证 - 结束时间为空")
    public void testValidateMeetingCreate_NullEndTime() {
        // 准备测试数据
        testMeeting.setEndTime(null);

        // 执行测试
        String result = validationUtil.validateMeetingCreate(testMeeting);

        // 验证结果
        assertNotNull(result);
        assertTrue(result.contains("结束时间"));
    }

    @Test
    @DisplayName("测试会议创建验证 - 开始时间早于当前时间")
    public void testValidateMeetingCreate_StartTimeBeforeNow() {
        // 准备测试数据
        testMeeting.setStartTime(LocalDateTime.now().minusHours(1));

        // 执行测试
        String result = validationUtil.validateMeetingCreate(testMeeting);

        // 验证结果
        assertNotNull(result);
        assertTrue(result.contains("开始时间"));
    }

    @Test
    @DisplayName("测试会议创建验证 - 开始时间晚于结束时间")
    public void testValidateMeetingCreate_StartTimeAfterEndTime() {
        // 准备测试数据
        testMeeting.setStartTime(testMeeting.getEndTime().plusHours(1));

        // 执行测试
        String result = validationUtil.validateMeetingCreate(testMeeting);

        // 验证结果
        assertNotNull(result);
        assertTrue(result.contains("开始时间"));
    }

    @Test
    @DisplayName("测试会议创建验证 - 会议内容为空")
    public void testValidateMeetingCreate_EmptyMeetingContent() {
        // 准备测试数据
        testMeeting.setMeetingContent("");

        // 执行测试
        String result = validationUtil.validateMeetingCreate(testMeeting);

        // 验证结果
        assertNotNull(result);
        assertTrue(result.contains("会议内容"));
    }

    @Test
    @DisplayName("测试会议创建验证 - 会议内容过短")
    public void testValidateMeetingCreate_ShortMeetingContent() {
        // 准备测试数据
        testMeeting.setMeetingContent("短");

        // 执行测试
        String result = validationUtil.validateMeetingCreate(testMeeting);

        // 验证结果
        assertNotNull(result);
        assertTrue(result.contains("会议内容"));
    }

    @Test
    @DisplayName("测试会议创建验证 - 会议内容过长")
    public void testValidateMeetingCreate_LongMeetingContent() {
        // 准备测试数据
        StringBuilder longContent = new StringBuilder();
        for (int i = 0; i < 2001; i++) {
            longContent.append("a");
        }
        testMeeting.setMeetingContent(longContent.toString());

        // 执行测试
        String result = validationUtil.validateMeetingCreate(testMeeting);

        // 验证结果
        assertNotNull(result);
        assertTrue(result.contains("会议内容"));
    }

    @Test
    @DisplayName("测试会议创建验证 - 会议时长过短")
    public void testValidateMeetingCreate_ShortDuration() {
        // 准备测试数据
        testMeeting.setStartTime(LocalDateTime.now().plusDays(1));
        testMeeting.setEndTime(LocalDateTime.now().plusDays(1).plusMinutes(10)); // 只有10分钟

        // 执行测试
        String result = validationUtil.validateMeetingCreate(testMeeting);

        // 验证结果
        assertNotNull(result);
        assertTrue(result.contains("会议时长"));
    }

    @Test
    @DisplayName("测试会议创建验证 - 会议时长过长")
    public void testValidateMeetingCreate_LongDuration() {
        // 准备测试数据
        testMeeting.setStartTime(LocalDateTime.now().plusDays(1));
        testMeeting.setEndTime(LocalDateTime.now().plusDays(1).plusHours(25)); // 25小时

        // 执行测试
        String result = validationUtil.validateMeetingCreate(testMeeting);

        // 验证结果
        assertNotNull(result);
        assertTrue(result.contains("会议时长"));
    }

    @Test
    @DisplayName("测试用户名验证 - 正常情况")
    public void testValidateUsername_Success() {
        // 执行测试
        String result = validationUtil.validateUsername("testuser123");

        // 验证结果
        assertNull(result, "验证应该通过，返回null");
    }

    @Test
    @DisplayName("测试用户名验证 - 用户名为空")
    public void testValidateUsername_Empty() {
        // 执行测试
        String result = validationUtil.validateUsername("");

        // 验证结果
        assertNotNull(result);
        assertEquals("用户名不能为空", result);
    }

    @Test
    @DisplayName("测试用户名验证 - 用户名为null")
    public void testValidateUsername_Null() {
        // 执行测试
        String result = validationUtil.validateUsername(null);

        // 验证结果
        assertNotNull(result);
        assertEquals("用户名不能为空", result);
    }

    @Test
    @DisplayName("测试用户名验证 - 用户名过短")
    public void testValidateUsername_TooShort() {
        // 执行测试
        String result = validationUtil.validateUsername("ab");

        // 验证结果
        assertNotNull(result);
        assertTrue(result.contains("用户名格式不正确"));
    }

    @Test
    @DisplayName("测试用户名验证 - 用户名过长")
    public void testValidateUsername_TooLong() {
        // 准备测试数据
        StringBuilder longUsername = new StringBuilder();
        for (int i = 0; i < 21; i++) {
            longUsername.append("a");
        }

        // 执行测试
        String result = validationUtil.validateUsername(longUsername.toString());

        // 验证结果
        assertNotNull(result);
        assertTrue(result.contains("用户名格式不正确"));
    }

    @Test
    @DisplayName("测试用户名验证 - 包含特殊字符")
    public void testValidateUsername_SpecialCharacters() {
        // 执行测试
        String result = validationUtil.validateUsername("test@user");

        // 验证结果
        assertNotNull(result);
        assertTrue(result.contains("用户名格式不正确"));
    }

    @Test
    @DisplayName("测试密码验证 - 正常情况")
    public void testValidatePassword_Success() {
        // 执行测试
        String result = validationUtil.validatePassword("password123");

        // 验证结果
        assertNull(result, "验证应该通过，返回null");
    }

    @Test
    @DisplayName("测试密码验证 - 密码为空")
    public void testValidatePassword_Empty() {
        // 执行测试
        String result = validationUtil.validatePassword("");

        // 验证结果
        assertNotNull(result);
        assertEquals("密码不能为空", result);
    }

    @Test
    @DisplayName("测试密码验证 - 密码为null")
    public void testValidatePassword_Null() {
        // 执行测试
        String result = validationUtil.validatePassword(null);

        // 验证结果
        assertNotNull(result);
        assertEquals("密码不能为空", result);
    }

    @Test
    @DisplayName("测试密码验证 - 密码过短")
    public void testValidatePassword_TooShort() {
        // 执行测试
        String result = validationUtil.validatePassword("12345");

        // 验证结果
        assertNotNull(result);
        assertTrue(result.contains("密码长度"));
    }

    @Test
    @DisplayName("测试密码验证 - 密码过长")
    public void testValidatePassword_TooLong() {
        // 准备测试数据
        StringBuilder longPassword = new StringBuilder();
        for (int i = 0; i < 21; i++) {
            longPassword.append("a");
        }

        // 执行测试
        String result = validationUtil.validatePassword(longPassword.toString());

        // 验证结果
        assertNotNull(result);
        assertTrue(result.contains("密码长度"));
    }

    @Test
    @DisplayName("测试分页参数验证 - 正常情况")
    public void testValidatePagination_Success() {
        // 执行测试
        String result = validationUtil.validatePagination(1, 10);

        // 验证结果
        assertNull(result, "验证应该通过，返回null");
    }

    @Test
    @DisplayName("测试分页参数验证 - 页码过小")
    public void testValidatePagination_PageTooSmall() {
        // 执行测试
        String result = validationUtil.validatePagination(0, 10);

        // 验证结果
        assertNotNull(result);
        assertTrue(result.contains("页码"));
    }

    @Test
    @DisplayName("测试分页参数验证 - 页码过大")
    public void testValidatePagination_PageTooLarge() {
        // 执行测试
        String result = validationUtil.validatePagination(1001, 10);

        // 验证结果
        assertNotNull(result);
        assertTrue(result.contains("页码"));
    }

    @Test
    @DisplayName("测试分页参数验证 - 页面大小过小")
    public void testValidatePagination_SizeTooSmall() {
        // 执行测试
        String result = validationUtil.validatePagination(1, 0);

        // 验证结果
        assertNotNull(result);
        assertTrue(result.contains("页面大小"));
    }

    @Test
    @DisplayName("测试分页参数验证 - 页面大小过大")
    public void testValidatePagination_SizeTooLarge() {
        // 执行测试
        String result = validationUtil.validatePagination(1, 101);

        // 验证结果
        assertNotNull(result);
        assertTrue(result.contains("页面大小"));
    }

    @Test
    @DisplayName("测试审核意见验证 - 正常情况")
    public void testValidateReviewComment_Success() {
        // 执行测试
        String result = validationUtil.validateReviewComment("审核通过");

        // 验证结果
        assertNull(result, "验证应该通过，返回null");
    }

    @Test
    @DisplayName("测试审核意见验证 - 意见过长")
    public void testValidateReviewComment_TooLong() {
        // 准备测试数据
        StringBuilder longComment = new StringBuilder();
        for (int i = 0; i < 501; i++) {
            longComment.append("a");
        }

        // 执行测试
        String result = validationUtil.validateReviewComment(longComment.toString());

        // 验证结果
        assertNotNull(result);
        assertTrue(result.contains("审核意见"));
    }

    @Test
    @DisplayName("测试审核意见验证 - 意见为null")
    public void testValidateReviewComment_Null() {
        // 执行测试
        String result = validationUtil.validateReviewComment(null);

        // 验证结果
        assertNull(result, "null值应该通过验证");
    }
} 