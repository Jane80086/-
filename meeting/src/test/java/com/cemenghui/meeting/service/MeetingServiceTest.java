package com.cemenghui.meeting.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cemenghui.meeting.bean.Meeting;
import com.cemenghui.meeting.bean.MeetingQuery;
import com.cemenghui.meeting.bean.MeetingReviewRequest;
import com.cemenghui.meeting.bean.MeetingReviewRecord;
import com.cemenghui.meeting.dao.MeetingDao;
import com.cemenghui.meeting.exception.ServiceException;
import com.cemenghui.meeting.util.PermissionUtil;
import com.cemenghui.meeting.util.ValidationUtil;
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

import com.cemenghui.entity.User;

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

    @Mock
    private UserService userService;

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
                .creatorName(ADMIN_USERNAME)
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
                .creatorName(ADMIN_USERNAME)
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

        // Mock用户服务
        User adminUser = new User();
        adminUser.setId(1L);
        adminUser.setUsername(ADMIN_USERNAME);
        adminUser.setUserType("ADMIN");
        when(userService.getUserByUsername(ADMIN_USERNAME)).thenReturn(adminUser);

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
        assertEquals(ADMIN_USERNAME, result.getCreatorName());
        assertEquals(1, result.getStatus()); // 管理员创建的会议直接通过
        assertNotNull(result.getCreateTime());
        assertNotNull(result.getUpdateTime());
        assertEquals(0, result.getDeleted());

        // 验证方法调用
        verify(userService).getUserByUsername(ADMIN_USERNAME);
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

        // Mock用户服务
        User normalUser = new User();
        normalUser.setId(2L);
        normalUser.setUsername(NORMAL_USERNAME);
        normalUser.setUserType("NORMAL");
        when(userService.getUserByUsername(NORMAL_USERNAME)).thenReturn(normalUser);

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
        assertEquals(NORMAL_USERNAME, result.getCreatorName());
        assertEquals(0, result.getStatus()); // 普通用户创建的会议需要审核

        // 验证方法调用
        verify(userService).getUserByUsername(NORMAL_USERNAME);
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
    @DisplayName("测试创建会议 - 参数为空")
    public void testCreateMeeting_NullMeeting() {
        // 执行测试并验证异常
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            meetingService.createMeeting(null, ADMIN_USERNAME);
        });

        assertEquals("会议数据不能为空", exception.getMessage());
    }

    @Test
    @DisplayName("测试创建会议 - 用户名为空")
    public void testCreateMeeting_EmptyUsername() {
        // 准备测试数据
        Meeting meeting = createTestMeeting();

        // 执行测试并验证异常
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            meetingService.createMeeting(meeting, null);
        });

        assertEquals("用户名不能为空", exception.getMessage());
    }

    @Test
    @DisplayName("测试创建会议 - 数据库插入失败")
    public void testCreateMeeting_DatabaseInsertFailed() {
        // 准备测试数据
        Meeting meeting = createTestMeeting();

        // Mock用户服务
        User adminUser = new User();
        adminUser.setId(1L);
        adminUser.setUsername(ADMIN_USERNAME);
        adminUser.setUserType("ADMIN");
        when(userService.getUserByUsername(ADMIN_USERNAME)).thenReturn(adminUser);

        // Mock权限验证
        when(permissionUtil.canCreateMeeting(ADMIN_USERNAME)).thenReturn(true);
        when(permissionUtil.isAdmin(ADMIN_USERNAME)).thenReturn(true);
        
        // Mock数据验证
        when(validationUtil.validateMeetingCreate(any(Meeting.class))).thenReturn(null);
        
        // Mock数据库操作失败
        when(meetingDao.insert(any(Meeting.class))).thenReturn(0);

        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            meetingService.createMeeting(meeting, ADMIN_USERNAME);
        });

        assertEquals("会议创建失败: 会议创建失败，数据库插入异常", exception.getMessage());
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
        assertEquals(testMeeting.getCreatorName(), result.getCreatorName());

        // 验证方法调用
        verify(meetingDao).selectById(1L);
    }

    @Test
    @DisplayName("测试获取会议详情 - ID无效")
    public void testGetMeetingById_InvalidId() {
        // 执行测试并验证异常
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            meetingService.getMeetingById(null);
        });

        assertEquals("会议ID无效", exception.getMessage());
    }

    @Test
    @DisplayName("测试获取会议详情 - ID为负数")
    public void testGetMeetingById_NegativeId() {
        // 执行测试并验证异常
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            meetingService.getMeetingById(-1L);
        });

        assertEquals("会议ID无效", exception.getMessage());
    }

    @Test
    @DisplayName("测试获取会议详情 - 会议已删除")
    public void testGetMeetingById_DeletedMeeting() {
        // Mock数据库返回已删除的会议
        Meeting deletedMeeting = Meeting.builder()
                .id(1L)
                .meetingName("测试会议")
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(1).plusHours(2))
                .meetingContent("这是一个测试会议")
                .creatorName(ADMIN_USERNAME)
                .status(0)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .deleted(1)
                .build();
        when(meetingDao.selectById(1L)).thenReturn(deletedMeeting);

        // 执行测试并验证异常
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            meetingService.getMeetingById(1L);
        });

        assertEquals("会议不存在", exception.getMessage());
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
        when(permissionUtil.canEditMeeting(ADMIN_USERNAME, testMeeting.getCreatorName())).thenReturn(true);
        
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
        verify(permissionUtil).canEditMeeting(ADMIN_USERNAME, testMeeting.getCreatorName());
        verify(validationUtil).validateMeetingUpdate(any(Meeting.class));
        verify(meetingDao).updateById(any(Meeting.class));
    }

    @Test
    @DisplayName("测试更新会议 - 会议不存在")
    public void testUpdateMeeting_MeetingNotFound() {
        // 准备测试数据
        Meeting meeting = Meeting.builder()
                .id(999L)
                .meetingName("测试会议")
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(1).plusHours(2))
                .meetingContent("这是一个测试会议")
                .creatorName(ADMIN_USERNAME)
                .status(0)
                .build();

        // Mock数据验证
        when(validationUtil.validateMeetingUpdate(any(Meeting.class))).thenReturn(null);
        
        // Mock数据库查询返回null
        when(meetingDao.selectById(999L)).thenReturn(null);

        // 执行测试并验证异常
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            meetingService.updateMeeting(meeting, ADMIN_USERNAME);
        });

        assertEquals("会议不存在", exception.getMessage());
    }

    @Test
    @DisplayName("测试更新会议 - 会议已删除")
    public void testUpdateMeeting_DeletedMeeting() {
        // 准备测试数据
        Meeting meeting = Meeting.builder()
                .id(1L)
                .meetingName("测试会议")
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(1).plusHours(2))
                .meetingContent("这是一个测试会议")
                .creatorName(ADMIN_USERNAME)
                .status(0)
                .build();
        Meeting deletedMeeting = Meeting.builder()
                .id(1L)
                .meetingName("测试会议")
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(1).plusHours(2))
                .meetingContent("这是一个测试会议")
                .creatorName(ADMIN_USERNAME)
                .status(0)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .deleted(1)
                .build();

        // Mock数据验证
        when(validationUtil.validateMeetingUpdate(any(Meeting.class))).thenReturn(null);
        
        // Mock数据库查询返回已删除的会议
        when(meetingDao.selectById(1L)).thenReturn(deletedMeeting);

        // 执行测试并验证异常
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            meetingService.updateMeeting(meeting, ADMIN_USERNAME);
        });

        assertEquals("会议不存在", exception.getMessage());
    }

    @Test
    @DisplayName("测试更新会议 - 会议状态为已删除")
    public void testUpdateMeeting_MeetingStatusDeleted() {
        // 准备测试数据
        Meeting meeting = Meeting.builder()
                .id(1L)
                .meetingName("测试会议")
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(1).plusHours(2))
                .meetingContent("这是一个测试会议")
                .creatorName(ADMIN_USERNAME)
                .status(0)
                .build();
        Meeting deletedStatusMeeting = Meeting.builder()
                .id(1L)
                .meetingName("测试会议")
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(1).plusHours(2))
                .meetingContent("这是一个测试会议")
                .creatorName(ADMIN_USERNAME)
                .status(3)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .deleted(0)
                .build();

        // Mock权限验证
        when(permissionUtil.canEditMeeting(ADMIN_USERNAME, ADMIN_USERNAME)).thenReturn(true);
        
        // Mock数据验证
        when(validationUtil.validateMeetingUpdate(any(Meeting.class))).thenReturn(null);
        
        // Mock数据库查询返回状态为已删除的会议
        when(meetingDao.selectById(1L)).thenReturn(deletedStatusMeeting);

        // 执行测试并验证异常
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            meetingService.updateMeeting(meeting, ADMIN_USERNAME);
        });

        assertEquals("已删除的会议不能编辑", exception.getMessage());
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
        when(permissionUtil.canEditMeeting(NORMAL_USERNAME, testMeeting.getCreatorName())).thenReturn(false);

        // 执行测试并验证异常
        SecurityException exception = assertThrows(SecurityException.class, () -> {
            meetingService.updateMeeting(updateMeeting, NORMAL_USERNAME);
        });

        assertEquals("您没有权限编辑此会议", exception.getMessage());

        // 验证方法调用
        verify(meetingDao).selectById(1L);
        verify(permissionUtil).canEditMeeting(NORMAL_USERNAME, testMeeting.getCreatorName());
        verify(meetingDao, never()).updateById(any(Meeting.class));
    }

    @Test
    @DisplayName("测试更新会议 - 数据库更新失败")
    public void testUpdateMeeting_DatabaseUpdateFailed() {
        // 准备测试数据
        Meeting meeting = Meeting.builder()
                .id(1L)
                .meetingName("测试会议")
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(1).plusHours(2))
                .meetingContent("这是一个测试会议")
                .creatorName(ADMIN_USERNAME)
                .status(0)
                .build();

        // Mock权限验证
        when(permissionUtil.canEditMeeting(ADMIN_USERNAME, ADMIN_USERNAME)).thenReturn(true);
        
        // Mock数据验证
        when(validationUtil.validateMeetingUpdate(any(Meeting.class))).thenReturn(null);
        
        // Mock数据库查询
        when(meetingDao.selectById(1L)).thenReturn(testMeeting);
        
        // Mock数据库更新失败
        when(meetingDao.updateById(any(Meeting.class))).thenReturn(0);

        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            meetingService.updateMeeting(meeting, ADMIN_USERNAME);
        });

        assertEquals("会议更新失败: 会议更新失败，数据库更新异常", exception.getMessage());
    }

    @Test
    @DisplayName("测试删除会议")
    public void testDeleteMeeting() {
        // Mock数据库查询
        when(meetingDao.selectById(1L)).thenReturn(testMeeting);
        
        // Mock权限验证
        when(permissionUtil.canDeleteMeeting(ADMIN_USERNAME, testMeeting.getCreatorName())).thenReturn(true);
        
        // Mock数据库删除 - 使用正确的方法名
        when(meetingDao.deleteById(1L)).thenReturn(1);

        // 执行测试
        boolean result = meetingService.deleteMeeting(1L, ADMIN_USERNAME, true);

        // 验证结果
        assertTrue(result);

        // 验证方法调用
        verify(meetingDao).selectById(1L);
        verify(permissionUtil).canDeleteMeeting(ADMIN_USERNAME, testMeeting.getCreatorName());
        verify(meetingDao).deleteById(1L);
    }

    @Test
    @DisplayName("测试删除会议 - ID无效")
    public void testDeleteMeeting_InvalidId() {
        // 执行测试并验证异常
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            meetingService.deleteMeeting(null, ADMIN_USERNAME, true);
        });

        assertEquals("会议ID无效", exception.getMessage());
    }

    @Test
    @DisplayName("测试删除会议 - 未确认删除")
    public void testDeleteMeeting_NotConfirmed() {
        // 执行测试并验证异常
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            meetingService.deleteMeeting(1L, ADMIN_USERNAME, false);
        });

        assertEquals("请确认删除操作", exception.getMessage());
    }

    @Test
    @DisplayName("测试删除会议 - 会议不存在")
    public void testDeleteMeeting_MeetingNotFound() {
        // Mock数据库查询返回null
        when(meetingDao.selectById(999L)).thenReturn(null);

        // 执行测试并验证异常
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            meetingService.deleteMeeting(999L, ADMIN_USERNAME, true);
        });

        assertEquals("会议不存在", exception.getMessage());
    }

    @Test
    @DisplayName("测试删除会议 - 数据库删除失败")
    public void testDeleteMeeting_DatabaseDeleteFailed() {
        // Mock数据库查询
        when(meetingDao.selectById(1L)).thenReturn(testMeeting);
        
        // Mock权限验证
        when(permissionUtil.canDeleteMeeting(ADMIN_USERNAME, ADMIN_USERNAME)).thenReturn(true);
        
        // Mock数据库删除失败
        when(meetingDao.deleteById(1L)).thenReturn(0);

        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            meetingService.deleteMeeting(1L, ADMIN_USERNAME, true);
        });

        assertEquals("会议删除失败: 会议删除失败，数据库删除异常", exception.getMessage());
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
    @DisplayName("测试审核会议 - 请求为空")
    public void testReviewMeeting_NullRequest() {
        // 执行测试并验证异常
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            meetingService.reviewMeeting(null, REVIEWER_USERNAME);
        });

        assertEquals("审核请求不能为空", exception.getMessage());
    }

    @Test
    @DisplayName("测试审核会议 - 审核人为空")
    public void testReviewMeeting_EmptyReviewer() {
        // 准备测试数据
        MeetingReviewRequest request = new MeetingReviewRequest();
        request.setMeetingId(1L);
        request.setStatus(1);

        // 执行测试并验证异常
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            meetingService.reviewMeeting(request, null);
        });

        assertEquals("审核人不能为空", exception.getMessage());
    }

    @Test
    @DisplayName("测试审核会议 - 会议不存在")
    public void testReviewMeeting_MeetingNotFound() {
        // 准备测试数据
        MeetingReviewRequest request = new MeetingReviewRequest();
        request.setMeetingId(999L);
        request.setStatus(1);

        // Mock权限验证
        when(permissionUtil.canReviewMeeting(REVIEWER_USERNAME)).thenReturn(true);
        
        // Mock数据库查询返回null
        when(meetingDao.selectById(999L)).thenReturn(null);

        // 执行测试并验证异常
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            meetingService.reviewMeeting(request, REVIEWER_USERNAME);
        });

        assertEquals("会议不存在", exception.getMessage());
    }

    @Test
    @DisplayName("测试审核会议 - 会议状态不是待审核")
    public void testReviewMeeting_MeetingNotPending() {
        // 准备测试数据
        MeetingReviewRequest request = new MeetingReviewRequest();
        request.setMeetingId(1L);
        request.setStatus(1);
        
        Meeting approvedMeeting = Meeting.builder()
                .id(1L)
                .meetingName("测试会议")
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(1).plusHours(2))
                .meetingContent("这是一个测试会议")
                .creatorName(ADMIN_USERNAME)
                .status(1)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .deleted(0)
                .build();

        // Mock权限验证
        when(permissionUtil.canReviewMeeting(REVIEWER_USERNAME)).thenReturn(true);
        
        // Mock数据库查询返回已审核的会议
        when(meetingDao.selectById(1L)).thenReturn(approvedMeeting);

        // 执行测试并验证异常
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            meetingService.reviewMeeting(request, REVIEWER_USERNAME);
        });

        assertEquals("只能审核待审核状态的会议", exception.getMessage());
    }

    @Test
    @DisplayName("测试审核会议 - 数据库更新失败")
    public void testReviewMeeting_DatabaseUpdateFailed() {
        // 准备测试数据
        MeetingReviewRequest request = new MeetingReviewRequest();
        request.setMeetingId(1L);
        request.setStatus(1);
        request.setReviewComment("审核通过");

        // Mock权限验证
        when(permissionUtil.canReviewMeeting(REVIEWER_USERNAME)).thenReturn(true);
        
        // Mock数据库查询
        when(meetingDao.selectById(1L)).thenReturn(testMeeting);
        
        // Mock数据库更新失败
        when(meetingDao.updateById(any(Meeting.class))).thenReturn(0);

        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            meetingService.reviewMeeting(request, REVIEWER_USERNAME);
        });

        assertEquals("会议审核失败: 会议审核失败，数据库更新异常", exception.getMessage());
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
    @DisplayName("测试查询会议列表 - 查询条件为空")
    public void testGetMeetingsByCondition_NullQuery() {
        // 执行测试并验证异常
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            meetingService.getMeetingsByCondition(null, ADMIN_USERNAME);
        });

        assertEquals("查询条件不能为空", exception.getMessage());
    }

    @Test
    @DisplayName("测试查询会议列表 - 用户名为空")
    public void testGetMeetingsByCondition_EmptyUsername() {
        // 准备测试数据
        MeetingQuery query = createTestMeetingQuery();

        // 执行测试并验证异常
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            meetingService.getMeetingsByCondition(query, null);
        });

        assertEquals("用户名不能为空", exception.getMessage());
    }

    @Test
    @DisplayName("测试查询会议列表 - 管理员权限")
    public void testGetMeetingsByCondition_AdminPermission() {
        // 准备测试数据
        MeetingQuery query = createTestMeetingQuery();
        query.setStatus(1);

        // Mock权限验证
        when(permissionUtil.canViewMeetings(ADMIN_USERNAME)).thenReturn(true);
        when(permissionUtil.isAdmin(ADMIN_USERNAME)).thenReturn(true);
        
        // Mock分页查询结果
        Page<Meeting> page = new Page<>(1, 10);
        page.setRecords(List.of(testMeeting));
        page.setTotal(1);
        when(meetingDao.selectPage(any(Page.class), any())).thenReturn(page);

        // 执行测试
        Map<String, Object> result = meetingService.getMeetingsByCondition(query, ADMIN_USERNAME);

        // 验证结果
        assertNotNull(result);
        assertTrue(result.containsKey("meetings"));
        assertTrue(result.containsKey("total"));
        assertEquals(1L, result.get("total"));
    }

    @Test
    @DisplayName("测试查询会议列表 - 企业用户权限")
    public void testGetMeetingsByCondition_EnterprisePermission() {
        // 准备测试数据
        MeetingQuery query = createTestMeetingQuery();

        // Mock权限验证
        when(permissionUtil.canViewMeetings(NORMAL_USERNAME)).thenReturn(true);
        when(permissionUtil.isAdmin(NORMAL_USERNAME)).thenReturn(false);
        when(permissionUtil.isEnterpriseUser(NORMAL_USERNAME)).thenReturn(true);
        
        // Mock分页查询结果
        Page<Meeting> page = new Page<>(1, 10);
        page.setRecords(List.of(testMeeting));
        page.setTotal(1);
        when(meetingDao.selectPage(any(Page.class), any())).thenReturn(page);

        // 执行测试
        Map<String, Object> result = meetingService.getMeetingsByCondition(query, NORMAL_USERNAME);

        // 验证结果
        assertNotNull(result);
        assertTrue(result.containsKey("meetings"));
        assertTrue(result.containsKey("total"));
        assertEquals(1L, result.get("total"));
    }

    @Test
    @DisplayName("测试查询会议列表 - 普通用户权限")
    public void testGetMeetingsByCondition_NormalPermission() {
        // 准备测试数据
        MeetingQuery query = createTestMeetingQuery();

        // Mock权限验证
        when(permissionUtil.canViewMeetings(NORMAL_USERNAME)).thenReturn(true);
        when(permissionUtil.isAdmin(NORMAL_USERNAME)).thenReturn(false);
        when(permissionUtil.isEnterpriseUser(NORMAL_USERNAME)).thenReturn(false);
        
        // Mock分页查询结果
        Page<Meeting> page = new Page<>(1, 10);
        page.setRecords(List.of(testMeeting));
        page.setTotal(1);
        when(meetingDao.selectPage(any(Page.class), any())).thenReturn(page);

        // 执行测试
        Map<String, Object> result = meetingService.getMeetingsByCondition(query, NORMAL_USERNAME);

        // 验证结果
        assertNotNull(result);
        assertTrue(result.containsKey("meetings"));
        assertTrue(result.containsKey("total"));
        assertEquals(1L, result.get("total"));
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
    @DisplayName("测试查询待审核会议 - 权限不足")
    public void testGetPendingMeetings_NoPermission() {
        // Mock权限验证失败
        when(permissionUtil.canReviewMeeting(NORMAL_USERNAME)).thenReturn(false);

        // 执行测试并验证异常
        SecurityException exception = assertThrows(SecurityException.class, () -> {
            meetingService.getPendingMeetings(NORMAL_USERNAME);
        });

        assertEquals("您没有权限查看待审核会议", exception.getMessage());
    }

    @Test
    @DisplayName("测试查询审核记录")
    public void testGetReviewRecords() {
        // 准备测试数据
        List<MeetingReviewRecord> records = new ArrayList<>();
        MeetingReviewRecord record = new MeetingReviewRecord();
        record.setId(1L);
        record.setMeetingId(1L);
        record.setReviewerName(REVIEWER_USERNAME);
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
        assertEquals(record.getReviewerName(), result.get(0).getReviewerName());

        // 验证方法调用
        verify(meetingDao).findReviewRecordsByReviewer(REVIEWER_USERNAME);
    }

    @Test
    @DisplayName("测试查询审核记录 - 按审核人")
    public void testGetReviewRecordsByReviewer() {
        // 准备测试数据
        List<MeetingReviewRecord> records = List.of(
            MeetingReviewRecord.builder()
                .meetingId(1L)
                .meetingName("测试会议")
                .reviewerName(REVIEWER_USERNAME)
                .status(1)
                .build()
        );

        // Mock数据库查询
        when(meetingDao.findReviewRecordsByReviewer(REVIEWER_USERNAME)).thenReturn(records);

        // 执行测试
        List<MeetingReviewRecord> result = meetingService.getReviewRecordsByReviewer(REVIEWER_USERNAME);

        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(REVIEWER_USERNAME, result.get(0).getReviewerName());
    }

    @Test
    @DisplayName("测试查询审核记录 - 按创建人")
    public void testGetReviewRecordsByCreator() {
        // 准备测试数据
        List<MeetingReviewRecord> records = List.of(
            MeetingReviewRecord.builder()
                .meetingId(1L)
                .meetingName("测试会议")
                .creatorName(ADMIN_USERNAME)
                .status(1)
                .build()
        );

        // Mock数据库查询
        when(meetingDao.findReviewRecordsByCreator(ADMIN_USERNAME)).thenReturn(records);

        // 执行测试
        List<MeetingReviewRecord> result = meetingService.getReviewRecordsByCreator(ADMIN_USERNAME);

        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(ADMIN_USERNAME, result.get(0).getCreatorName());
    }

    @Test
    @DisplayName("测试查询审核记录 - 按会议ID")
    public void testGetReviewRecordsByMeetingId() {
        // 准备测试数据
        List<MeetingReviewRecord> records = List.of(
            MeetingReviewRecord.builder()
                .meetingId(1L)
                .meetingName("测试会议")
                .reviewerName(REVIEWER_USERNAME)
                .status(1)
                .build()
        );

        // Mock数据库查询
        when(meetingDao.findReviewRecordsByMeetingId(1L)).thenReturn(records);

        // 执行测试
        List<MeetingReviewRecord> result = meetingService.getReviewRecordsByMeetingId(1L);

        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getMeetingId());
    }

    @Test
    @DisplayName("测试处理图片URL - 空会议列表")
    public void testProcessImageUrls_EmptyList() {
        // Mock权限验证
        when(permissionUtil.canViewMeetings(ADMIN_USERNAME)).thenReturn(true);
        when(permissionUtil.isAdmin(ADMIN_USERNAME)).thenReturn(true);
        
        // Mock分页查询结果 - 空列表
        Page<Meeting> page = new Page<>(1, 10);
        page.setRecords(List.of());
        page.setTotal(0);
        when(meetingDao.selectPage(any(Page.class), any())).thenReturn(page);

        // 执行测试 - 不应该抛出异常
        assertDoesNotThrow(() -> {
            meetingService.getMeetingsByCondition(createTestMeetingQuery(), ADMIN_USERNAME);
        });
    }

    @Test
    @DisplayName("测试处理图片URL - 会议无图片")
    public void testProcessImageUrl_NoImage() {
        // 准备测试数据
        Meeting meetingWithoutImage = Meeting.builder()
                .id(1L)
                .meetingName("测试会议")
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(1).plusHours(2))
                .meetingContent("这是一个测试会议")
                .creatorName(ADMIN_USERNAME)
                .status(0)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .deleted(0)
                .imageUrl(null)
                .build();

        // Mock权限验证
        when(permissionUtil.canViewMeetings(ADMIN_USERNAME)).thenReturn(true);
        when(permissionUtil.isAdmin(ADMIN_USERNAME)).thenReturn(true);
        
        // Mock分页查询结果
        Page<Meeting> page = new Page<>(1, 10);
        page.setRecords(List.of(meetingWithoutImage));
        page.setTotal(1);
        when(meetingDao.selectPage(any(Page.class), any())).thenReturn(page);

        // 执行测试 - 不应该抛出异常
        assertDoesNotThrow(() -> {
            meetingService.getMeetingsByCondition(createTestMeetingQuery(), ADMIN_USERNAME);
        });
    }

    @Test
    @DisplayName("测试处理图片URL - HTTP URL")
    public void testProcessImageUrl_HttpUrl() {
        // 准备测试数据
        Meeting meetingWithHttpImage = Meeting.builder()
                .id(1L)
                .meetingName("测试会议")
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(1).plusHours(2))
                .meetingContent("这是一个测试会议")
                .creatorName(ADMIN_USERNAME)
                .status(0)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .deleted(0)
                .imageUrl("https://example.com/image.jpg")
                .build();

        // Mock权限验证
        when(permissionUtil.canViewMeetings(ADMIN_USERNAME)).thenReturn(true);
        when(permissionUtil.isAdmin(ADMIN_USERNAME)).thenReturn(true);
        
        // Mock分页查询结果
        Page<Meeting> page = new Page<>(1, 10);
        page.setRecords(List.of(meetingWithHttpImage));
        page.setTotal(1);
        when(meetingDao.selectPage(any(Page.class), any())).thenReturn(page);

        // 执行测试 - 不应该抛出异常
        assertDoesNotThrow(() -> {
            meetingService.getMeetingsByCondition(createTestMeetingQuery(), ADMIN_USERNAME);
        });
    }
} 