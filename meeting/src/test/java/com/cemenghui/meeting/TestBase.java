package com.cemenghui.meeting;

import com.cemenghui.meeting.bean.Meeting;
import com.cemenghui.meeting.bean.MeetingQuery;
import com.cemenghui.meeting.bean.MeetingReviewRequest;
import com.cemenghui.entity.User;
import com.cemenghui.common.JWTUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

/**
 * 测试基础类，提供通用的测试配置和工具方法
 */
@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
public abstract class TestBase {

    @Autowired
    protected WebApplicationContext webApplicationContext;

    @Autowired
    protected JWTUtil jwtUtil;

    @Autowired
    protected ObjectMapper objectMapper;

    protected MockMvc mockMvc;

    // 测试用户数据
    protected static final String ADMIN_USERNAME = "admin";
    protected static final String NORMAL_USERNAME = "user1";
    protected static final String REVIEWER_USERNAME = "reviewer";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    /**
     * 生成测试用的JWT Token
     */
    protected String generateToken(String username) {
        return "Bearer " + jwtUtil.generateToken(1L, username, java.util.List.of("ADMIN"));
    }

    /**
     * 创建测试会议对象
     */
    protected Meeting createTestMeeting() {
        return Meeting.builder()
                .meetingName("测试会议")
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(1).plusHours(2))
                .meetingContent("这是一个测试会议，包含详细的内容描述，用于验证会议创建功能是否正常工作。")
                .creatorName(ADMIN_USERNAME)
                .status(0)
                .build();
    }

    /**
     * 创建测试会议查询对象
     */
    protected MeetingQuery createTestMeetingQuery() {
        MeetingQuery query = new MeetingQuery();
        query.setPage(1);
        query.setSize(10);
        return query;
    }

    /**
     * 创建测试审核请求对象
     */
    protected MeetingReviewRequest createTestReviewRequest(Long meetingId) {
        MeetingReviewRequest request = new MeetingReviewRequest();
        request.setMeetingId(meetingId);
        request.setStatus(1);
        request.setReviewComment("审核通过");
        return request;
    }

    /**
     * 创建测试用户对象
     */
    protected User createTestUser(String username) {
        User user = new User();
        user.setUsername(username);
        user.setPassword("password123");
        user.setUserType("NORMAL");
        return user;
    }

    /**
     * 执行POST请求的通用方法
     */
    protected String performPostRequest(String url, Object requestBody, String token) throws Exception {
        return mockMvc.perform(post(url)
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    /**
     * 执行GET请求的通用方法
     */
    protected String performGetRequest(String url, String token) throws Exception {
        return mockMvc.perform(get(url)
                .header("Authorization", token))
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    /**
     * 执行文件上传请求的通用方法
     */
    protected String performFileUploadRequest(String url, MockMultipartFile file) throws Exception {
        return mockMvc.perform(multipart(url)
                .file(file))
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    /**
     * 创建测试用的文件上传对象
     */
    protected MockMultipartFile createTestImageFile() {
        return new MockMultipartFile(
                "file",
                "test-image.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );
    }

    /**
     * 创建删除会议的请求参数
     */
    protected Map<String, Object> createDeleteRequest(Long meetingId, boolean confirmDelete) {
        Map<String, Object> request = new HashMap<>();
        request.put("meeting_id", meetingId);
        request.put("confirm_delete", confirmDelete);
        return request;
    }

    /**
     * 创建查询会议详情的请求参数
     */
    protected Map<String, Long> createMeetingDetailRequest(Long meetingId) {
        Map<String, Long> request = new HashMap<>();
        request.put("meeting_id", meetingId);
        return request;
    }
} 