package com.cemenghui.meeting.java.com.example.demo.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cemenghui.meeting.bean.Meeting;
import com.cemenghui.meeting.bean.MeetingQuery;
import com.cemenghui.meeting.bean.MeetingReviewRequest;
import com.cemenghui.meeting.bean.MeetingReviewRecord;
import com.cemenghui.meeting.dao.MeetingDao;
import com.cemenghui.meeting.util.PermissionUtil;
import com.cemenghui.meeting.util.ValidationUtil;
import com.cemenghui.meeting.service.MeetingService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * MeetingService 单元测试类
 * 测试业务逻辑层的核心功能
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("会议服务测试")
public class MeetingServiceTest {

    // 测试用户数据
    private static final String ADMIN_USERNAME = "admin";
    private static final String NORMAL_USERNAME = "user1";
    private static final String REVIEWER_USERNAME = "reviewer";

    @Mock
    private MeetingDao meetingDao;

    @Mock
    private PermissionUtil permissionUtil;

    @Mock
    private ValidationUtil validationUtil;

    @InjectMocks
    private MeetingService meetingService;

    private Meeting testMeeting;

    @BeforeEach
    void setUp() {
        testMeeting = Meeting.builder()
                .id(1L)
                .meetingName("测试会议")
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(1).plusHours(2))
                .meetingContent("这是一个测试会议，用于验证业务逻辑。")
                .creator(ADMIN_USERNAME)
                .status(0)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .deleted(0)
                .build();
    }

    /**
     * 创建测试会议对象
     */
    private Meeting createTestMeeting() {
        return Meeting.builder()
                .meetingName("测试会议")
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(1).plusHours(2))
                .meetingContent("这是一个测试会议，包含详细的内容描述，用于验证会议创建功能是否正常工作。")
                .creator(ADMIN_USERNAME)
                .status(0)
                .build();
    }

    /**
     * 创建测试会议查询对象
     */
    private MeetingQuery createTestMeetingQuery() {
        MeetingQuery query = new MeetingQuery();
        query.setPage(1);
        query.setSize(10);
        return query;
    }

    @Test
    @DisplayName("测试创建会议 - 管理员用户")
    public void testCreateMeeting_AdminUser() {
        // 准备测试数据
        Meeting meeting = Meeting.builder()
                .meetingName("管理员测试会议")
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(1).plusHours(2))
                .meetingContent("这是一个管理员创建的测试会议。")
                .build();

        // Mock权限验证
        when(permissionUtil.canCreateMeeting(ADMIN_USERNAME)).thenReturn(true);
        when(permissionUtil.isAdmin(ADMIN_USERNAME)).thenReturn(true);
        
        // Mock数据验证
        when(validationUtil.validateMeetingCreate(any(Meeting.class))).thenReturn(null);
        
        // Mock数据库操作
        when(meetingDao.insert(any(Meeting.class))).thenReturn(1);

        // 执行测试
        Meeting result = meetingService.createMeeting(meeting, ADMIN_USERNAME);

        // 验证结果
        assertNotNull(result);
        assertEquals(meeting.getMeetingName(), result.getMeetingName());
        assertEquals(ADMIN_USERNAME, result.getCreator());
        assertEquals(1, result.getStatus()); // 管理员创建的会议直接通过
        assertNotNull(result.getCreateTime());
        assertNotNull(result.getUpdateTime());
        assertEquals(0, result.getDeleted());

        // 验证方法调用
        verify(permissionUtil).canCreateMeeting(ADMIN_USERNAME);
        verify(permissionUtil).isAdmin(ADMIN_USERNAME);
        verify(validationUtil).validateMeetingCreate(any(Meeting.class));
        verify(meetingDao).insert(any(Meeting.class));
    }

    @Test
    @DisplayName("测试创建会议 - 普通用户")
    public void testCreateMeeting_NormalUser() {
        // 准备测试数据
        Meeting meeting = Meeting.builder()
                .meetingName("普通用户测试会议")
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(1).plusHours(2))
                .meetingContent("这是一个普通用户创建的测试会议。")
                .build();

        // Mock权限验证
        when(permissionUtil.canCreateMeeting(NORMAL_USERNAME)).thenReturn(true);
        when(permissionUtil.isAdmin(NORMAL_USERNAME)).thenReturn(false);
        
        // Mock数据验证
        when(validationUtil.validateMeetingCreate(any(Meeting.class))).thenReturn(null);
        
        // Mock数据库操作
        when(meetingDao.insert(any(Meeting.class))).thenReturn(1);

        // 执行测试
        Meeting result = meetingService.createMeeting(meeting, NORMAL_USERNAME);

        // 验证结果
        assertNotNull(result);
        assertEquals(meeting.getMeetingName(), result.getMeetingName());
        assertEquals(NORMAL_USERNAME, result.getCreator());
        assertEquals(0, result.getStatus()); // 普通用户创建的会议需要审核

        // 验证方法调用
        verify(permissionUtil).canCreateMeeting(NORMAL_USERNAME);
        verify(permissionUtil).isAdmin(NORMAL_USERNAME);
        verify(validationUtil).validateMeetingCreate(any(Meeting.class));
        verify(meetingDao).insert(any(Meeting.class));
    }

    @Test
    @DisplayName("测试创建会议 - 权限不足")
    public void testCreateMeeting_NoPermission() {
        // 准备测试数据
        Meeting meeting = createTestMeeting();

        // Mock权限验证失败
        when(permissionUtil.canCreateMeeting(NORMAL_USERNAME)).thenReturn(false);

        // 执行测试并验证异常
        SecurityException exception = assertThrows(SecurityException.class, () -> {
            meetingService.createMeeting(meeting, NORMAL_USERNAME);
        });

        assertEquals("您没有权限创建会议", exception.getMessage());

        // 验证方法调用
        verify(permissionUtil).canCreateMeeting(NORMAL_USERNAME);
        verify(meetingDao, never()).insert(any(Meeting.class));
    }

    @Test
    @DisplayName("测试创建会议 - 数据验证失败")
    public void testCreateMeeting_ValidationFailed() {
        // 准备测试数据
        Meeting meeting = createTestMeeting();

        // Mock权限验证
        when(permissionUtil.canCreateMeeting(ADMIN_USERNAME)).thenReturn(true);
        
        // Mock数据验证失败
        when(validationUtil.validateMeetingCreate(any(Meeting.class))).thenReturn("会议名称不能为空");

        // 执行测试并验证异常
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            meetingService.createMeeting(meeting, ADMIN_USERNAME);
        });

        assertEquals("会议名称不能为空", exception.getMessage());

        // 验证方法调用
        verify(permissionUtil).canCreateMeeting(ADMIN_USERNAME);
        verify(validationUtil).validateMeetingCreate(any(Meeting.class));
        verify(meetingDao, never()).insert(any(Meeting.class));
    }

    @Test
    @DisplayName("测试获取会议详情")
    public void testGetMeetingById() {
        // Mock数据库查询
        when(meetingDao.selectById(1L)).thenReturn(testMeeting);

        // 执行测试
        Meeting result = meetingService.getMeetingById(1L);

        // 验证结果
        assertNotNull(result);
        assertEquals(testMeeting.getId(), result.getId());
        assertEquals(testMeeting.getMeetingName(), result.getMeetingName());
        assertEquals(testMeeting.getCreator(), result.getCreator());

        // 验证方法调用
        verify(meetingDao).selectById(1L);
    }

    @Test
    @DisplayName("测试获取会议详情 - 会议不存在")
    public void testGetMeetingById_NotFound() {
        // Mock数据库查询返回null
        when(meetingDao.selectById(999L)).thenReturn(null);

        // 执行测试并验证异常
        assertThrows(Exception.class, () -> {
            meetingService.getMeetingById(999L);
        });

        // 验证方法调用
        verify(meetingDao).selectById(999L);
    }

    @Test
    @DisplayName("测试更新会议")
    public void testUpdateMeeting() {
        // 准备测试数据
        Meeting updateMeeting = Meeting.builder()
                .id(1L)
                .meetingName("更新后的会议")
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(1).plusHours(2))
                .meetingContent("这是更新后的会议内容。")
                .build();

        // Mock数据库查询原会议
        when(meetingDao.selectById(1L)).thenReturn(testMeeting);
        
        // Mock权限验证
        when(permissionUtil.canEditMeeting(ADMIN_USERNAME, testMeeting.getCreator())).thenReturn(true);
        
        // Mock数据验证
        when(validationUtil.validateMeetingUpdate(any(Meeting.class))).thenReturn(null);
        
        // Mock数据库更新
        when(meetingDao.updateById(any(Meeting.class))).thenReturn(1);

        // 执行测试
        boolean result = meetingService.updateMeeting(updateMeeting, ADMIN_USERNAME);

        // 验证结果
        assertTrue(result);

        // 验证方法调用
        verify(meetingDao).selectById(1L);
        verify(permissionUtil).canEditMeeting(ADMIN_USERNAME, testMeeting.getCreator());
        verify(validationUtil).validateMeetingUpdate(any(Meeting.class));
        verify(meetingDao).updateById(any(Meeting.class));
    }

    @Test
    @DisplayName("测试更新会议 - 权限不足")
    public void testUpdateMeeting_NoPermission() {
        // 准备测试数据
        Meeting updateMeeting = Meeting.builder()
                .id(1L)
                .meetingName("更新后的会议")
                .build();

        // Mock数据库查询原会议
        when(meetingDao.selectById(1L)).thenReturn(testMeeting);
        
        // Mock权限验证失败
        when(permissionUtil.canEditMeeting(NORMAL_USERNAME, testMeeting.getCreator())).thenReturn(false);

        // 执行测试并验证异常
        SecurityException exception = assertThrows(SecurityException.class, () -> {
            meetingService.updateMeeting(updateMeeting, NORMAL_USERNAME);
        });

        assertEquals("您没有权限编辑此会议", exception.getMessage());

        // 验证方法调用
        verify(meetingDao).selectById(1L);
        verify(permissionUtil).canEditMeeting(NORMAL_USERNAME, testMeeting.getCreator());
        verify(meetingDao, never()).updateById(any(Meeting.class));
    }

    @Test
    @DisplayName("测试删除会议")
    public void testDeleteMeeting() {
        // Mock数据库查询
        when(meetingDao.selectById(1L)).thenReturn(testMeeting);
        
        // Mock权限验证
        when(permissionUtil.canDeleteMeeting(ADMIN_USERNAME, testMeeting.getCreator())).thenReturn(true);
        
        // Mock数据库删除 - 使用正确的方法名
        when(meetingDao.deleteById(1L)).thenReturn(1);

        // 执行测试
        boolean result = meetingService.deleteMeeting(1L, ADMIN_USERNAME, true);

        // 验证结果
        assertTrue(result);

        // 验证方法调用
        verify(meetingDao).selectById(1L);
        verify(permissionUtil).canDeleteMeeting(ADMIN_USERNAME, testMeeting.getCreator());
        verify(meetingDao).deleteById(1L);
    }

    @Test
    @DisplayName("测试删除会议 - 未确认删除")
    public void testDeleteMeeting_NotConfirmed() {
        // 执行测试并验证异常
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            meetingService.deleteMeeting(1L, ADMIN_USERNAME, false);
        });

        assertEquals("请确认删除操作", exception.getMessage());

        // 验证方法调用
        verify(meetingDao, never()).selectById(anyLong());
        verify(meetingDao, never()).updateById(any(Meeting.class));
    }

    @Test
    @DisplayName("测试审核会议")
    public void testReviewMeeting() {
        // 准备测试数据
        MeetingReviewRequest reviewRequest = new MeetingReviewRequest();
        reviewRequest.setMeetingId(1L);
        reviewRequest.setStatus(1);
        reviewRequest.setReviewComment("审核通过");

        // Mock权限验证
        when(permissionUtil.canReviewMeeting(REVIEWER_USERNAME)).thenReturn(true);
        
        // Mock数据库查询
        when(meetingDao.selectById(1L)).thenReturn(testMeeting);
        
        // Mock数据库更新
        when(meetingDao.updateById(any(Meeting.class))).thenReturn(1);
        
        // Mock审核记录插入
        when(meetingDao.insertReviewRecord(any(MeetingReviewRecord.class))).thenReturn(1);

        // 执行测试
        boolean result = meetingService.reviewMeeting(reviewRequest, REVIEWER_USERNAME);

        // 验证结果
        assertTrue(result);

        // 验证方法调用
        verify(permissionUtil).canReviewMeeting(REVIEWER_USERNAME);
        verify(meetingDao).selectById(1L);
        verify(meetingDao).updateById(any(Meeting.class));
        verify(meetingDao).insertReviewRecord(any(MeetingReviewRecord.class));
    }

    @Test
    @DisplayName("测试查询会议列表")
    public void testGetMeetingsByCondition() {
        // 准备测试数据
        MeetingQuery query = createTestMeetingQuery();
        List<Meeting> meetings = new ArrayList<>();
        meetings.add(testMeeting);

        // Mock权限验证
        when(permissionUtil.canViewMeetings(ADMIN_USERNAME)).thenReturn(true);
        when(permissionUtil.isAdmin(ADMIN_USERNAME)).thenReturn(true);
        
        // Mock数据库查询 - 创建一个模拟的Page对象
        Page<Meeting> mockPage = new Page<>();
        mockPage.setRecords(meetings);
        mockPage.setTotal(1);
        mockPage.setPages(1);
        mockPage.setCurrent(1);
        mockPage.setSize(10);
        when(meetingDao.selectPage(any(), any())).thenReturn(mockPage);

        // 执行测试
        Map<String, Object> result = meetingService.getMeetingsByCondition(query, ADMIN_USERNAME);

        // 验证结果
        assertNotNull(result);

        // 验证方法调用
        verify(permissionUtil).canViewMeetings(ADMIN_USERNAME);
        verify(permissionUtil).isAdmin(ADMIN_USERNAME);
        verify(meetingDao).selectPage(any(), any());
    }

    @Test
    @DisplayName("测试查询待审核会议")
    public void testGetPendingMeetings() {
        // 准备测试数据
        List<Meeting> pendingMeetings = new ArrayList<>();
        pendingMeetings.add(testMeeting);

        // Mock权限验证
        when(permissionUtil.canReviewMeeting(REVIEWER_USERNAME)).thenReturn(true);
        
        // Mock数据库查询
        when(meetingDao.findPendingMeetings()).thenReturn(pendingMeetings);

        // 执行测试
        List<Meeting> result = meetingService.getPendingMeetings(REVIEWER_USERNAME);

        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testMeeting.getId(), result.get(0).getId());

        // 验证方法调用
        verify(permissionUtil).canReviewMeeting(REVIEWER_USERNAME);
        verify(meetingDao).findPendingMeetings();
    }

    @Test
    @DisplayName("测试查询审核记录")
    public void testGetReviewRecords() {
        // 准备测试数据
        List<MeetingReviewRecord> records = new ArrayList<>();
        MeetingReviewRecord record = new MeetingReviewRecord();
        record.setId(1L);
        record.setMeetingId(1L);
        record.setReviewer(REVIEWER_USERNAME);
        record.setStatus(1);
        record.setReviewComment("审核通过");
        records.add(record);

        // Mock数据库查询
        when(meetingDao.findReviewRecordsByReviewer(REVIEWER_USERNAME)).thenReturn(records);

        // 执行测试
        List<MeetingReviewRecord> result = meetingService.getReviewRecordsByReviewer(REVIEWER_USERNAME);

        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(record.getId(), result.get(0).getId());
        assertEquals(record.getMeetingId(), result.get(0).getMeetingId());
        assertEquals(record.getReviewer(), result.get(0).getReviewer());

        // 验证方法调用
        verify(meetingDao).findReviewRecordsByReviewer(REVIEWER_USERNAME);
    }
} 