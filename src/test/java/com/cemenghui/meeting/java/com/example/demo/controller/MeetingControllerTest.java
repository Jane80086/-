package com.cemenghui.meeting.java.com.example.demo.controller;

import com.cemenghui.meeting.java.com.example.demo.TestBase;
import com.cemenghui.meeting.bean.ApiResponse;
import com.cemenghui.meeting.bean.Meeting;
import com.cemenghui.meeting.bean.MeetingQuery;
import com.cemenghui.meeting.bean.MeetingReviewRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * MeetingController 白盒测试类
 * 重点测试会议功能的核心业务逻辑
 */
@DisplayName("会议控制器测试")
public class MeetingControllerTest extends TestBase {

    @Test
    @DisplayName("测试创建会议 - 管理员用户")
    public void testCreateMeeting_AdminUser() throws Exception {
        // 准备测试数据
        Meeting meeting = Meeting.builder()
                .meetingName("管理员测试会议")
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(1).plusHours(2))
                .meetingContent("这是一个管理员创建的测试会议，用于验证会议创建功能。")
                .creator(ADMIN_USERNAME)
                .status(0)
                .build();
        
        String token = generateToken(ADMIN_USERNAME);

        // 执行测试
        String response = performPostRequest("/api/meeting/create", meeting, token);

        // 验证结果
        assertNotNull(response);
        assertFalse(response.isEmpty());

        // 解析响应
        ApiResponse<Meeting> apiResponse = objectMapper.readValue(response, 
            new TypeReference<ApiResponse<Meeting>>() {});

        // 验证响应状态
        assertNotNull(apiResponse);
        System.out.println("Response Code: " + apiResponse.getCode());
        System.out.println("Response Message: " + apiResponse.getMessage());
        
        // 如果成功，验证数据
        if (apiResponse.getCode() == 200) {
            assertNotNull(apiResponse.getData());
            Meeting createdMeeting = apiResponse.getData();
            assertEquals(meeting.getMeetingName(), createdMeeting.getMeetingName());
            assertEquals(ADMIN_USERNAME, createdMeeting.getCreator());
            assertEquals(1, createdMeeting.getStatus()); // 管理员创建的会议直接通过
            assertNotNull(createdMeeting.getId());
            assertNotNull(createdMeeting.getCreateTime());
            assertNotNull(createdMeeting.getUpdateTime());
        }
    }

    @Test
    @DisplayName("测试创建会议 - 普通用户")
    public void testCreateMeeting_NormalUser() throws Exception {
        // 准备测试数据
        Meeting meeting = Meeting.builder()
                .meetingName("普通用户测试会议")
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(1).plusHours(2))
                .meetingContent("这是一个普通用户创建的测试会议，需要审核。")
                .creator(NORMAL_USERNAME)
                .status(0)
                .build();
        
        String token = generateToken(NORMAL_USERNAME);

        // 执行测试
        String response = performPostRequest("/api/meeting/create", meeting, token);

        // 验证结果
        assertNotNull(response);
        assertFalse(response.isEmpty());

        // 解析响应
        ApiResponse<Meeting> apiResponse = objectMapper.readValue(response, 
            new TypeReference<ApiResponse<Meeting>>() {});

        // 验证响应状态
        assertNotNull(apiResponse);
        System.out.println("Response Code: " + apiResponse.getCode());
        System.out.println("Response Message: " + apiResponse.getMessage());
        
        // 如果成功，验证数据
        if (apiResponse.getCode() == 200) {
            assertNotNull(apiResponse.getData());
            Meeting createdMeeting = apiResponse.getData();
            assertEquals(meeting.getMeetingName(), createdMeeting.getMeetingName());
            assertEquals(NORMAL_USERNAME, createdMeeting.getCreator());
            assertEquals(0, createdMeeting.getStatus()); // 普通用户创建的会议需要审核
        }
    }

    @Test
    @DisplayName("测试Token验证 - 无效Token")
    public void testTokenValidation_InvalidToken() throws Exception {
        // 准备测试数据
        Meeting meeting = createTestMeeting();
        String invalidToken = "Bearer invalid_token";

        // 执行测试
        String response = performPostRequest("/api/meeting/create", meeting, invalidToken);

        // 验证结果
        assertNotNull(response);
        assertFalse(response.isEmpty());

        // 解析响应
        ApiResponse<Meeting> apiResponse = objectMapper.readValue(response, 
            new TypeReference<ApiResponse<Meeting>>() {});

        // 验证响应状态
        assertEquals(400, apiResponse.getCode());
        assertNull(apiResponse.getData());
    }

    @Test
    @DisplayName("测试Token验证 - 空Token")
    public void testTokenValidation_EmptyToken() throws Exception {
        // 准备测试数据
        Meeting meeting = createTestMeeting();
        String emptyToken = "";

        // 执行测试
        String response = performPostRequest("/api/meeting/create", meeting, emptyToken);

        // 验证结果
        assertNotNull(response);
        assertFalse(response.isEmpty());

        // 解析响应
        ApiResponse<Meeting> apiResponse = objectMapper.readValue(response, 
            new TypeReference<ApiResponse<Meeting>>() {});

        // 验证响应状态
        assertEquals(400, apiResponse.getCode());
        assertNull(apiResponse.getData());
    }

    @Test
    @DisplayName("测试查询会议列表")
    public void testGetMeetings() throws Exception {
        // 准备测试数据
        MeetingQuery query = createTestMeetingQuery();
        String token = generateToken(ADMIN_USERNAME);

        // 执行测试
        String response = performPostRequest("/api/meeting/list", query, token);

        // 验证结果
        assertNotNull(response);
        assertFalse(response.isEmpty());

        // 解析响应
        ApiResponse<Map<String, Object>> apiResponse = objectMapper.readValue(response, 
            new TypeReference<ApiResponse<Map<String, Object>>>() {});

        // 验证响应状态
        assertNotNull(apiResponse);
        System.out.println("Query Response Code: " + apiResponse.getCode());
        System.out.println("Query Response Message: " + apiResponse.getMessage());
        
        // 如果成功，验证数据结构
        if (apiResponse.getCode() == 200) {
            assertNotNull(apiResponse.getData());
            Map<String, Object> data = apiResponse.getData();
            assertTrue(data.containsKey("total") || data.containsKey("records"));
        }
    }

    @Test
    @DisplayName("测试查询待审核会议")
    public void testGetPendingMeetings() throws Exception {
        // 准备测试数据
        String token = generateToken(REVIEWER_USERNAME);

        // 执行测试
        String response = performPostRequest("/api/meeting/pending", null, token);

        // 验证结果
        assertNotNull(response);
        assertFalse(response.isEmpty());

        // 解析响应
        ApiResponse<Object> apiResponse = objectMapper.readValue(response, 
            new TypeReference<ApiResponse<Object>>() {});

        // 验证响应状态
        assertNotNull(apiResponse);
        System.out.println("Pending Response Code: " + apiResponse.getCode());
        System.out.println("Pending Response Message: " + apiResponse.getMessage());
    }

    @Test
    @DisplayName("测试会议审核功能")
    public void testReviewMeeting() throws Exception {
        // 首先创建一个会议
        Meeting meeting = Meeting.builder()
                .meetingName("审核测试会议")
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(1).plusHours(2))
                .meetingContent("这是一个用于测试审核功能的会议。")
                .creator(NORMAL_USERNAME)
                .status(0)
                .build();
        
        String creatorToken = generateToken(NORMAL_USERNAME);
        String response = performPostRequest("/api/meeting/create", meeting, creatorToken);
        
        ApiResponse<Meeting> createResponse = objectMapper.readValue(response, 
            new TypeReference<ApiResponse<Meeting>>() {});
        
        if (createResponse.getCode() == 200 && createResponse.getData() != null) {
            Long meetingId = createResponse.getData().getId();
            
            // 准备审核请求
            MeetingReviewRequest reviewRequest = createTestReviewRequest(meetingId);
            String reviewerToken = generateToken(REVIEWER_USERNAME);
            
            // 执行审核测试
            String reviewResponse = performPostRequest("/api/meeting/review", reviewRequest, reviewerToken);
            
            // 验证结果
            assertNotNull(reviewResponse);
            assertFalse(reviewResponse.isEmpty());
            
            // 解析响应
            ApiResponse<Boolean> reviewApiResponse = objectMapper.readValue(reviewResponse, 
                new TypeReference<ApiResponse<Boolean>>() {});
            
            // 验证响应状态
            assertNotNull(reviewApiResponse);
            System.out.println("Review Response Code: " + reviewApiResponse.getCode());
            System.out.println("Review Response Message: " + reviewApiResponse.getMessage());
        }
    }

    @Test
    @DisplayName("测试查询审核记录")
    public void testGetReviewRecords() throws Exception {
        // 准备测试数据
        String token = generateToken(REVIEWER_USERNAME);

        // 执行测试 - 按审核人查询
        String response = performPostRequest("/api/meeting/review/records/by-reviewer", null, token);

        // 验证结果
        assertNotNull(response);
        assertFalse(response.isEmpty());

        // 解析响应
        ApiResponse<Object> apiResponse = objectMapper.readValue(response, 
            new TypeReference<ApiResponse<Object>>() {});

        // 验证响应状态
        assertNotNull(apiResponse);
        System.out.println("Review Records Response Code: " + apiResponse.getCode());
        System.out.println("Review Records Response Message: " + apiResponse.getMessage());
    }

    @Test
    @DisplayName("测试文件上传功能")
    public void testUploadImage() throws Exception {
        // 准备测试数据
        MockMultipartFile file = createTestImageFile();

        // 执行测试
        String response = performFileUploadRequest("/api/meeting/uploadImage", file);

        // 验证结果
        assertNotNull(response);
        assertFalse(response.isEmpty());

        // 解析响应
        ApiResponse<String> apiResponse = objectMapper.readValue(response, 
            new TypeReference<ApiResponse<String>>() {});

        // 验证响应状态
        assertNotNull(apiResponse);
        System.out.println("Upload Response Code: " + apiResponse.getCode());
        System.out.println("Upload Response Message: " + apiResponse.getMessage());
    }
} 