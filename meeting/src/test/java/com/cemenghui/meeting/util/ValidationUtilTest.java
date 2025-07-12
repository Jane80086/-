package com.cemenghui.meeting.util;

import com.cemenghui.meeting.bean.Meeting;
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
        assertNull(result, "null审核意见应该通过验证");
    }

    @Test
    @DisplayName("测试用户注册验证 - 正常情况")
    public void testValidateUserRegistration_Success() {
        // 执行测试
        String result = validationUtil.validateUserRegistration(
                "testuser123", "password123", "张三", "test@example.com", "13800138000", "NORMAL"
        );

        // 验证结果
        assertNull(result, "验证应该通过，返回null");
    }

    @Test
    @DisplayName("测试用户注册验证 - 用户名为空")
    public void testValidateUserRegistration_EmptyUsername() {
        // 执行测试
        String result = validationUtil.validateUserRegistration(
                "", "password123", "张三", "test@example.com", "13800138000", "NORMAL"
        );

        // 验证结果
        assertNotNull(result);
        assertTrue(result.contains("用户名"));
    }

    @Test
    @DisplayName("测试用户注册验证 - 密码为空")
    public void testValidateUserRegistration_EmptyPassword() {
        // 执行测试
        String result = validationUtil.validateUserRegistration(
                "testuser123", "", "张三", "test@example.com", "13800138000", "NORMAL"
        );

        // 验证结果
        assertNotNull(result);
        assertTrue(result.contains("密码"));
    }

    @Test
    @DisplayName("测试用户注册验证 - 真实姓名为空")
    public void testValidateUserRegistration_EmptyRealName() {
        // 执行测试
        String result = validationUtil.validateUserRegistration(
                "testuser123", "password123", "", "test@example.com", "13800138000", "NORMAL"
        );

        // 验证结果
        assertNotNull(result);
        assertTrue(result.contains("真实姓名"));
    }

    @Test
    @DisplayName("测试用户注册验证 - 真实姓名过长")
    public void testValidateUserRegistration_LongRealName() {
        // 准备测试数据
        StringBuilder longName = new StringBuilder();
        for (int i = 0; i < 51; i++) {
            longName.append("张");
        }

        // 执行测试
        String result = validationUtil.validateUserRegistration(
                "testuser123", "password123", longName.toString(), "test@example.com", "13800138000", "NORMAL"
        );

        // 验证结果
        assertNotNull(result);
        assertTrue(result.contains("真实姓名"));
    }

    @Test
    @DisplayName("测试用户注册验证 - 邮箱格式错误")
    public void testValidateUserRegistration_InvalidEmail() {
        // 执行测试
        String result = validationUtil.validateUserRegistration(
                "testuser123", "password123", "张三", "invalid-email", "13800138000", "NORMAL"
        );

        // 验证结果
        assertNotNull(result);
        assertTrue(result.contains("邮箱格式"));
    }

    @Test
    @DisplayName("测试用户注册验证 - 邮箱过长")
    public void testValidateUserRegistration_LongEmail() {
        // 准备测试数据
        StringBuilder longEmail = new StringBuilder();
        for (int i = 0; i < 200; i++) {
            longEmail.append("a");
        }
        longEmail.append("@example.com");

        // 执行测试
        String result = validationUtil.validateUserRegistration(
                "testuser123", "password123", "张三", longEmail.toString(), "13800138000", "NORMAL"
        );

        // 验证结果
        assertNotNull(result);
        assertTrue(result.contains("邮箱长度"));
    }

    @Test
    @DisplayName("测试用户注册验证 - 手机号格式错误")
    public void testValidateUserRegistration_InvalidPhone() {
        // 执行测试
        String result = validationUtil.validateUserRegistration(
                "testuser123", "password123", "张三", "test@example.com", "12345678901", "NORMAL"
        );

        // 验证结果
        assertNotNull(result);
        assertTrue(result.contains("手机号格式"));
    }

    @Test
    @DisplayName("测试用户注册验证 - 用户类型为空")
    public void testValidateUserRegistration_EmptyUserType() {
        // 执行测试
        String result = validationUtil.validateUserRegistration(
                "testuser123", "password123", "张三", "test@example.com", "13800138000", ""
        );

        // 验证结果
        assertNotNull(result);
        assertTrue(result.contains("用户类型"));
    }

    @Test
    @DisplayName("测试用户注册验证 - 无效用户类型")
    public void testValidateUserRegistration_InvalidUserType() {
        // 执行测试
        String result = validationUtil.validateUserRegistration(
                "testuser123", "password123", "张三", "test@example.com", "13800138000", "INVALID"
        );

        // 验证结果
        assertNotNull(result);
        assertTrue(result.contains("用户类型"));
    }

    @Test
    @DisplayName("测试用户注册验证 - 邮箱为空")
    public void testValidateUserRegistration_NullEmail() {
        // 执行测试
        String result = validationUtil.validateUserRegistration(
                "testuser123", "password123", "张三", null, "13800138000", "NORMAL"
        );

        // 验证结果
        assertNull(result, "邮箱为空应该通过验证");
    }

    @Test
    @DisplayName("测试用户注册验证 - 手机号为空")
    public void testValidateUserRegistration_NullPhone() {
        // 执行测试
        String result = validationUtil.validateUserRegistration(
                "testuser123", "password123", "张三", "test@example.com", null, "NORMAL"
        );

        // 验证结果
        assertNull(result, "手机号为空应该通过验证");
    }

    @Test
    @DisplayName("测试会议查询验证 - 正常情况")
    public void testValidateMeetingQuery_Success() {
        // 执行测试
        String result = validationUtil.validateMeetingQuery(
                1, 10, "测试会议", "admin", "2024-01-01T10:00:00", "2024-01-02T10:00:00"
        );

        // 验证结果
        assertNull(result, "验证应该通过，返回null");
    }

    @Test
    @DisplayName("测试会议查询验证 - 会议名称过长")
    public void testValidateMeetingQuery_LongMeetingName() {
        // 准备测试数据
        StringBuilder longName = new StringBuilder();
        for (int i = 0; i < 101; i++) {
            longName.append("a");
        }

        // 执行测试
        String result = validationUtil.validateMeetingQuery(
                1, 10, longName.toString(), "admin", "2024-01-01T10:00:00", "2024-01-02T10:00:00"
        );

        // 验证结果
        assertNotNull(result);
        assertTrue(result.contains("会议名称"));
    }

    @Test
    @DisplayName("测试会议查询验证 - 创建人过长")
    public void testValidateMeetingQuery_LongCreator() {
        // 准备测试数据
        StringBuilder longCreator = new StringBuilder();
        for (int i = 0; i < 101; i++) {
            longCreator.append("a");
        }

        // 执行测试
        String result = validationUtil.validateMeetingQuery(
                1, 10, "测试会议", longCreator.toString(), "2024-01-01T10:00:00", "2024-01-02T10:00:00"
        );

        // 验证结果
        assertNotNull(result);
        assertTrue(result.contains("创建人"));
    }

    @Test
    @DisplayName("测试会议查询验证 - 开始日期晚于结束日期")
    public void testValidateMeetingQuery_StartDateAfterEndDate() {
        // 执行测试
        String result = validationUtil.validateMeetingQuery(
                1, 10, "测试会议", "admin", "2024-01-02T10:00:00", "2024-01-01T10:00:00"
        );

        // 验证结果
        assertNotNull(result);
        assertTrue(result.contains("开始日期"));
    }

    @Test
    @DisplayName("测试会议查询验证 - 日期格式错误")
    public void testValidateMeetingQuery_InvalidDateFormat() {
        // 执行测试
        String result = validationUtil.validateMeetingQuery(
                1, 10, "测试会议", "admin", "invalid-date", "2024-01-02T10:00:00"
        );

        // 验证结果
        assertNotNull(result);
        assertTrue(result.contains("日期格式"));
    }

    @Test
    @DisplayName("测试审核请求验证 - 正常情况")
    public void testValidateReviewRequest_Success() {
        // 执行测试
        String result = validationUtil.validateReviewRequest(1L, 1, "审核通过");

        // 验证结果
        assertNull(result, "验证应该通过，返回null");
    }

    @Test
    @DisplayName("测试审核请求验证 - 会议ID为空")
    public void testValidateReviewRequest_NullMeetingId() {
        // 执行测试
        String result = validationUtil.validateReviewRequest(null, 1, "审核通过");

        // 验证结果
        assertNotNull(result);
        assertTrue(result.contains("会议ID"));
    }

    @Test
    @DisplayName("测试审核请求验证 - 会议ID小于等于0")
    public void testValidateReviewRequest_InvalidMeetingId() {
        // 执行测试
        String result = validationUtil.validateReviewRequest(0L, 1, "审核通过");

        // 验证结果
        assertNotNull(result);
        assertTrue(result.contains("会议ID"));
    }

    @Test
    @DisplayName("测试审核请求验证 - 审核状态为空")
    public void testValidateReviewRequest_NullStatus() {
        // 执行测试
        String result = validationUtil.validateReviewRequest(1L, null, "审核通过");

        // 验证结果
        assertNotNull(result);
        assertTrue(result.contains("审核状态"));
    }

    @Test
    @DisplayName("测试审核请求验证 - 无效审核状态")
    public void testValidateReviewRequest_InvalidStatus() {
        // 执行测试
        String result = validationUtil.validateReviewRequest(1L, 3, "审核通过");

        // 验证结果
        assertNotNull(result);
        assertTrue(result.contains("审核状态"));
    }

    @Test
    @DisplayName("测试审核请求验证 - 审核意见过长")
    public void testValidateReviewRequest_LongComment() {
        // 准备测试数据
        StringBuilder longComment = new StringBuilder();
        for (int i = 0; i < 501; i++) {
            longComment.append("a");
        }

        // 执行测试
        String result = validationUtil.validateReviewRequest(1L, 1, longComment.toString());

        // 验证结果
        assertNotNull(result);
        assertTrue(result.contains("审核意见"));
    }

    @Test
    @DisplayName("测试URL验证 - 正常情况")
    public void testValidateUrl_Success() {
        // 执行测试
        String result = validationUtil.validateUrl("https://example.com");

        // 验证结果
        assertNull(result, "验证应该通过，返回null");
    }

    @Test
    @DisplayName("测试URL验证 - URL为空")
    public void testValidateUrl_Empty() {
        // 执行测试
        String result = validationUtil.validateUrl("");

        // 验证结果
        assertNull(result, "空URL应该通过验证");
    }

    @Test
    @DisplayName("测试URL验证 - URL为null")
    public void testValidateUrl_Null() {
        // 执行测试
        String result = validationUtil.validateUrl(null);

        // 验证结果
        assertNull(result, "null URL应该通过验证");
    }

    @Test
    @DisplayName("测试URL验证 - URL过长")
    public void testValidateUrl_TooLong() {
        // 准备测试数据
        StringBuilder longUrl = new StringBuilder("https://");
        for (int i = 0; i < 500; i++) {
            longUrl.append("a");
        }
        longUrl.append(".com");

        // 执行测试
        String result = validationUtil.validateUrl(longUrl.toString());

        // 验证结果
        assertNotNull(result);
        assertTrue(result.contains("URL长度"));
    }

    @Test
    @DisplayName("测试URL验证 - 无效协议")
    public void testValidateUrl_InvalidProtocol() {
        // 执行测试
        String result = validationUtil.validateUrl("ftp://example.com");

        // 验证结果
        assertNotNull(result);
        assertTrue(result.contains("http://或https://"));
    }

    @Test
    @DisplayName("测试日期时间解析 - 正常情况")
    public void testParseDateTime_Success() {
        // 执行测试
        String dateTimeStr = "2024-01-01T10:00:00";
        LocalDateTime result = validationUtil.parseDateTime(dateTimeStr);

        // 验证结果
        assertNotNull(result);
        assertEquals(2024, result.getYear());
        assertEquals(1, result.getMonthValue());
        assertEquals(1, result.getDayOfMonth());
        assertEquals(10, result.getHour());
        assertEquals(0, result.getMinute());
    }

    @Test
    @DisplayName("测试日期时间解析 - 字符串为空")
    public void testParseDateTime_Empty() {
        // 执行测试
        LocalDateTime result = validationUtil.parseDateTime("");

        // 验证结果
        assertNull(result);
    }

    @Test
    @DisplayName("测试日期时间解析 - 字符串为null")
    public void testParseDateTime_Null() {
        // 执行测试
        LocalDateTime result = validationUtil.parseDateTime(null);

        // 验证结果
        assertNull(result);
    }

    @Test
    @DisplayName("测试日期时间解析 - 格式错误")
    public void testParseDateTime_InvalidFormat() {
        // 执行测试并验证异常
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            validationUtil.parseDateTime("invalid-date-time");
        });

        // 验证异常信息
        assertTrue(exception.getMessage().contains("日期时间格式不正确"));
    }

    @Test
    @DisplayName("测试会议更新验证 - 正常情况")
    public void testValidateMeetingUpdate_Success() {
        // 准备测试数据
        testMeeting.setId(1L);

        // 执行测试
        String result = validationUtil.validateMeetingUpdate(testMeeting);

        // 验证结果
        assertNull(result, "验证应该通过，返回null");
    }

    @Test
    @DisplayName("测试会议更新验证 - 会议为空")
    public void testValidateMeetingUpdate_NullMeeting() {
        // 执行测试
        String result = validationUtil.validateMeetingUpdate(null);

        // 验证结果
        assertNotNull(result);
        assertEquals("会议数据不能为空", result);
    }

    @Test
    @DisplayName("测试会议更新验证 - 会议ID为空")
    public void testValidateMeetingUpdate_NullId() {
        // 准备测试数据
        testMeeting.setId(null);

        // 执行测试
        String result = validationUtil.validateMeetingUpdate(testMeeting);

        // 验证结果
        assertNotNull(result);
        assertEquals("会议ID不能为空", result);
    }

    @Test
    @DisplayName("测试会议更新验证 - 部分字段更新")
    public void testValidateMeetingUpdate_PartialFields() {
        // 准备测试数据
        testMeeting.setId(1L);
        testMeeting.setMeetingName(null);
        testMeeting.setStartTime(null);
        testMeeting.setEndTime(null);

        // 执行测试
        String result = validationUtil.validateMeetingUpdate(testMeeting);

        // 验证结果
        assertNull(result, "部分字段更新应该通过验证");
    }

    @Test
    @DisplayName("测试会议更新验证 - 异常情况")
    public void testValidateMeetingUpdate_Exception() {
        // 准备测试数据，设置一个会导致异常的情况
        testMeeting.setId(1L);
        testMeeting.setMeetingName("测试会议");
        testMeeting.setStartTime(LocalDateTime.now().plusDays(1));
        testMeeting.setEndTime(LocalDateTime.now().plusDays(1).plusHours(2));

        // 执行测试
        String result = validationUtil.validateMeetingUpdate(testMeeting);

        // 验证结果
        assertNull(result, "正常情况应该通过验证");
    }

    @Test
    @DisplayName("测试会议创建验证 - 异常情况")
    public void testValidateMeetingCreate_Exception() {
        // 准备测试数据，设置一个会导致异常的情况
        testMeeting.setMeetingName("测试会议");
        testMeeting.setStartTime(LocalDateTime.now().plusDays(1));
        testMeeting.setEndTime(LocalDateTime.now().plusDays(1).plusHours(2));
        testMeeting.setMeetingContent("这是一个测试会议，用于验证数据验证功能。");

        // 执行测试
        String result = validationUtil.validateMeetingCreate(testMeeting);

        // 验证结果
        assertNull(result, "正常情况应该通过验证");
    }
} 