package com.cemenghui.meeting.util;

import com.cemenghui.meeting.bean.Meeting;
import com.cemenghui.entity.User;
import com.cemenghui.meeting.dao.UserDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * PermissionUtil 单元测试类
 * 测试权限检查功能
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("权限工具测试")
public class PermissionUtilTest {

    @Mock
    private UserDao userDao;

    @InjectMocks
    private PermissionUtil permissionUtil;

    private User adminUser;
    private User enterpriseUser;
    private User normalUser;

    @BeforeEach
    void setUp() {
        // 创建测试用户
        adminUser = new User();
        adminUser.setUsername("admin");
        adminUser.setUserType("ADMIN");

        enterpriseUser = new User();
        enterpriseUser.setUsername("enterprise");
        enterpriseUser.setUserType("ENTERPRISE");

        normalUser = new User();
        normalUser.setUsername("normal");
        normalUser.setUserType("NORMAL");
    }

    @Test
    @DisplayName("测试管理员权限检查 - 是管理员")
    public void testIsAdmin_True() {
        // 准备测试数据
        when(userDao.findByUsername("admin")).thenReturn(adminUser);

        // 执行测试
        boolean result = permissionUtil.isAdmin("admin");

        // 验证结果
        assertTrue(result);
        verify(userDao).findByUsername("admin");
    }

    @Test
    @DisplayName("测试管理员权限检查 - 不是管理员")
    public void testIsAdmin_False() {
        // 准备测试数据
        when(userDao.findByUsername("normal")).thenReturn(normalUser);

        // 执行测试
        boolean result = permissionUtil.isAdmin("normal");

        // 验证结果
        assertFalse(result);
        verify(userDao).findByUsername("normal");
    }

    @Test
    @DisplayName("测试管理员权限检查 - 用户不存在")
    public void testIsAdmin_UserNotFound() {
        // 准备测试数据
        when(userDao.findByUsername("nonexistent")).thenReturn(null);

        // 执行测试
        boolean result = permissionUtil.isAdmin("nonexistent");

        // 验证结果
        assertFalse(result);
        verify(userDao).findByUsername("nonexistent");
    }

    @Test
    @DisplayName("测试管理员权限检查 - 用户名为空")
    public void testIsAdmin_NullUsername() {
        // 执行测试
        boolean result = permissionUtil.isAdmin(null);

        // 验证结果
        assertFalse(result);
        verify(userDao, never()).findByUsername(any());
    }

    @Test
    @DisplayName("测试企业用户权限检查 - 是企业用户")
    public void testIsEnterpriseUser_True() {
        // 准备测试数据
        when(userDao.findByUsername("enterprise")).thenReturn(enterpriseUser);

        // 执行测试
        boolean result = permissionUtil.isEnterpriseUser("enterprise");

        // 验证结果
        assertTrue(result);
        verify(userDao).findByUsername("enterprise");
    }

    @Test
    @DisplayName("测试企业用户权限检查 - 不是企业用户")
    public void testIsEnterpriseUser_False() {
        // 准备测试数据
        when(userDao.findByUsername("normal")).thenReturn(normalUser);

        // 执行测试
        boolean result = permissionUtil.isEnterpriseUser("normal");

        // 验证结果
        assertFalse(result);
        verify(userDao).findByUsername("normal");
    }

    @Test
    @DisplayName("测试普通用户权限检查 - 是普通用户")
    public void testIsNormalUser_True() {
        // 准备测试数据
        when(userDao.findByUsername("normal")).thenReturn(normalUser);

        // 执行测试
        boolean result = permissionUtil.isNormalUser("normal");

        // 验证结果
        assertTrue(result);
        // isNormalUser会调用isAdmin和isEnterpriseUser，所以会调用两次
        verify(userDao, times(2)).findByUsername("normal");
    }

    @Test
    @DisplayName("测试普通用户权限检查 - 不是普通用户（是管理员）")
    public void testIsNormalUser_Admin() {
        // 准备测试数据
        when(userDao.findByUsername("admin")).thenReturn(adminUser);

        // 执行测试
        boolean result = permissionUtil.isNormalUser("admin");

        // 验证结果
        assertFalse(result);
    }

    @Test
    @DisplayName("测试普通用户权限检查 - 不是普通用户")
    public void testIsNormalUser_False() {
        // 准备测试数据
        when(userDao.findByUsername("admin")).thenReturn(adminUser);

        // 执行测试
        boolean result = permissionUtil.isNormalUser("admin");

        // 验证结果
        assertFalse(result);
        verify(userDao).findByUsername("admin");
    }

    @Test
    @DisplayName("测试普通用户权限检查 - 用户名为空")
    public void testIsNormalUser_NullUsername() {
        // 执行测试
        boolean result = permissionUtil.isNormalUser(null);

        // 验证结果
        assertTrue(result); // 空用户名被认为是普通用户
        verify(userDao, never()).findByUsername(anyString());
    }

    @Test
    @DisplayName("测试编辑会议权限 - 创建者")
    public void testCanEditMeeting_Creator() {
        // 执行测试
        boolean result = permissionUtil.canEditMeeting("admin", "admin");

        // 验证结果
        assertTrue(result);
    }

    @Test
    @DisplayName("测试编辑会议权限 - 管理员")
    public void testCanEditMeeting_Admin() {
        // 准备测试数据
        when(userDao.findByUsername("admin")).thenReturn(adminUser);

        // 执行测试
        boolean result = permissionUtil.canEditMeeting("admin", "normal");

        // 验证结果
        assertTrue(result);
        verify(userDao).findByUsername("admin");
    }

    @Test
    @DisplayName("测试编辑会议权限 - 无权限")
    public void testCanEditMeeting_NoPermission() {
        // 准备测试数据
        when(userDao.findByUsername("normal")).thenReturn(normalUser);

        // 执行测试
        boolean result = permissionUtil.canEditMeeting("normal", "admin");

        // 验证结果
        assertFalse(result);
        verify(userDao).findByUsername("normal");
    }

    @Test
    @DisplayName("测试编辑会议权限 - 用户名为空")
    public void testCanEditMeeting_NullUsername() {
        // 执行测试
        boolean result = permissionUtil.canEditMeeting(null, "admin");

        // 验证结果
        assertFalse(result);
        verify(userDao, never()).findByUsername(anyString());
    }

    @Test
    @DisplayName("测试删除会议权限 - 创建者")
    public void testCanDeleteMeeting_Creator() {
        // 执行测试
        boolean result = permissionUtil.canDeleteMeeting("admin", "admin");

        // 验证结果
        assertTrue(result);
    }

    @Test
    @DisplayName("测试删除会议权限 - 管理员")
    public void testCanDeleteMeeting_Admin() {
        // 准备测试数据
        when(userDao.findByUsername("admin")).thenReturn(adminUser);

        // 执行测试
        boolean result = permissionUtil.canDeleteMeeting("admin", "normal");

        // 验证结果
        assertTrue(result);
        verify(userDao).findByUsername("admin");
    }

    @Test
    @DisplayName("测试删除会议权限 - 无权限")
    public void testCanDeleteMeeting_NoPermission() {
        // 准备测试数据
        when(userDao.findByUsername("normal")).thenReturn(normalUser);

        // 执行测试
        boolean result = permissionUtil.canDeleteMeeting("normal", "admin");

        // 验证结果
        assertFalse(result);
        verify(userDao).findByUsername("normal");
    }

    @Test
    @DisplayName("测试删除会议权限 - 用户名为空")
    public void testCanDeleteMeeting_NullUsername() {
        // 执行测试
        boolean result = permissionUtil.canDeleteMeeting(null, "admin");

        // 验证结果
        assertFalse(result);
        verify(userDao, never()).findByUsername(anyString());
    }

    @Test
    @DisplayName("测试审核会议权限 - 管理员")
    public void testCanReviewMeeting_Admin() {
        // 准备测试数据
        when(userDao.findByUsername("admin")).thenReturn(adminUser);

        // 执行测试
        boolean result = permissionUtil.canReviewMeeting("admin");

        // 验证结果
        assertTrue(result);
        verify(userDao).findByUsername("admin");
    }

    @Test
    @DisplayName("测试审核会议权限 - 非管理员")
    public void testCanReviewMeeting_NonAdmin() {
        // 准备测试数据
        when(userDao.findByUsername("normal")).thenReturn(normalUser);

        // 执行测试
        boolean result = permissionUtil.canReviewMeeting("normal");

        // 验证结果
        assertFalse(result);
        verify(userDao).findByUsername("normal");
    }

    @Test
    @DisplayName("测试审核会议权限 - 用户名为空")
    public void testCanReviewMeeting_NullUsername() {
        // 执行测试
        boolean result = permissionUtil.canReviewMeeting(null);

        // 验证结果
        assertFalse(result);
        verify(userDao, never()).findByUsername(anyString());
    }

    @Test
    @DisplayName("测试创建会议权限 - 管理员")
    public void testCanCreateMeeting_Admin() {
        // 准备测试数据
        when(userDao.findByUsername("admin")).thenReturn(adminUser);

        // 执行测试
        boolean result = permissionUtil.canCreateMeeting("admin");

        // 验证结果
        assertTrue(result);
        verify(userDao).findByUsername("admin");
    }

    @Test
    @DisplayName("测试创建会议权限 - 企业用户")
    public void testCanCreateMeeting_Enterprise() {
        // 准备测试数据
        when(userDao.findByUsername("enterprise")).thenReturn(enterpriseUser);

        // 执行测试
        boolean result = permissionUtil.canCreateMeeting("enterprise");

        // 验证结果
        assertTrue(result);
        // canCreateMeeting会调用isAdmin和isEnterpriseUser，所以会调用两次
        verify(userDao, times(2)).findByUsername("enterprise");
    }

    @Test
    @DisplayName("测试创建会议权限 - 普通用户")
    public void testCanCreateMeeting_Normal() {
        // 准备测试数据
        when(userDao.findByUsername("normal")).thenReturn(normalUser);

        // 执行测试
        boolean result = permissionUtil.canCreateMeeting("normal");

        // 验证结果
        assertFalse(result);
        // canCreateMeeting会调用isAdmin和isEnterpriseUser，所以会调用两次
        verify(userDao, times(2)).findByUsername("normal");
    }

    @Test
    @DisplayName("测试创建会议权限 - 用户名为空")
    public void testCanCreateMeeting_NullUsername() {
        // 执行测试
        boolean result = permissionUtil.canCreateMeeting(null);

        // 验证结果
        assertFalse(result);
        verify(userDao, never()).findByUsername(anyString());
    }

    @Test
    @DisplayName("测试查看会议列表权限 - 正常用户")
    public void testCanViewMeetings_ValidUser() {
        // 执行测试
        boolean result = permissionUtil.canViewMeetings("admin");

        // 验证结果
        assertTrue(result);
    }

    @Test
    @DisplayName("测试查看会议列表权限 - 用户名为空")
    public void testCanViewMeetings_NullUsername() {
        // 执行测试
        boolean result = permissionUtil.canViewMeetings(null);

        // 验证结果
        assertFalse(result);
    }

    @Test
    @DisplayName("测试查看特定会议权限 - 管理员查看任何会议")
    public void testCanViewMeeting_AdminViewAny() {
        // 准备测试数据
        Meeting meeting = Meeting.builder()
                .id(1L)
                .creatorName("normal")
                .status(0) // 待审核
                .build();
        when(userDao.findByUsername("admin")).thenReturn(adminUser);

        // 执行测试
        boolean result = permissionUtil.canViewMeeting("admin", meeting);

        // 验证结果
        assertTrue(result);
        verify(userDao).findByUsername("admin");
    }

    @Test
    @DisplayName("测试查看特定会议权限 - 企业用户查看自己创建的会议")
    public void testCanViewMeeting_EnterpriseViewOwn() {
        // 准备测试数据
        Meeting meeting = Meeting.builder()
                .id(1L)
                .creatorName("enterprise")
                .status(0) // 待审核
                .build();
        when(userDao.findByUsername("enterprise")).thenReturn(enterpriseUser);

        // 执行测试
        boolean result = permissionUtil.canViewMeeting("enterprise", meeting);

        // 验证结果
        assertTrue(result);
        // canViewMeeting会调用isAdmin和isEnterpriseUser，所以会调用两次
        verify(userDao, times(2)).findByUsername("enterprise");
    }

    @Test
    @DisplayName("测试查看特定会议权限 - 企业用户查看已通过的会议")
    public void testCanViewMeeting_EnterpriseViewApproved() {
        // 准备测试数据
        Meeting meeting = Meeting.builder()
                .id(1L)
                .creatorName("admin")
                .status(1) // 已通过
                .build();
        when(userDao.findByUsername("enterprise")).thenReturn(enterpriseUser);

        // 执行测试
        boolean result = permissionUtil.canViewMeeting("enterprise", meeting);

        // 验证结果
        assertTrue(result);
        // canViewMeeting会调用isAdmin和isEnterpriseUser，所以会调用两次
        verify(userDao, times(2)).findByUsername("enterprise");
    }

    @Test
    @DisplayName("测试查看特定会议权限 - 企业用户查看未通过的他人会议")
    public void testCanViewMeeting_EnterpriseViewOthersPending() {
        // 准备测试数据
        Meeting meeting = Meeting.builder()
                .id(1L)
                .creatorName("admin")
                .status(0) // 待审核
                .build();
        when(userDao.findByUsername("enterprise")).thenReturn(enterpriseUser);

        // 执行测试
        boolean result = permissionUtil.canViewMeeting("enterprise", meeting);

        // 验证结果
        assertFalse(result);
        // canViewMeeting会调用isAdmin和isEnterpriseUser，所以会调用两次
        verify(userDao, times(2)).findByUsername("enterprise");
    }

    @Test
    @DisplayName("测试查看特定会议权限 - 普通用户查看已通过的会议")
    public void testCanViewMeeting_NormalViewApproved() {
        // 准备测试数据
        Meeting meeting = Meeting.builder()
                .id(1L)
                .creatorName("admin")
                .status(1) // 已通过
                .build();
        when(userDao.findByUsername("normal")).thenReturn(normalUser);

        // 执行测试
        boolean result = permissionUtil.canViewMeeting("normal", meeting);

        // 验证结果
        assertTrue(result);
        // canViewMeeting会调用isAdmin和isEnterpriseUser，所以会调用两次
        verify(userDao, times(2)).findByUsername("normal");
    }

    @Test
    @DisplayName("测试查看特定会议权限 - 普通用户查看未通过的会议")
    public void testCanViewMeeting_NormalViewPending() {
        // 准备测试数据
        Meeting meeting = Meeting.builder()
                .id(1L)
                .creatorName("admin")
                .status(0) // 待审核
                .build();
        when(userDao.findByUsername("normal")).thenReturn(normalUser);

        // 执行测试
        boolean result = permissionUtil.canViewMeeting("normal", meeting);

        // 验证结果
        assertFalse(result);
        // canViewMeeting会调用isAdmin和isEnterpriseUser，所以会调用两次
        verify(userDao, times(2)).findByUsername("normal");
    }

    @Test
    @DisplayName("测试查看特定会议权限 - 用户名为空")
    public void testCanViewMeeting_NullUsername() {
        // 准备测试数据
        Meeting meeting = Meeting.builder()
                .id(1L)
                .creatorName("admin")
                .status(1)
                .build();

        // 执行测试
        boolean result = permissionUtil.canViewMeeting(null, meeting);

        // 验证结果
        assertFalse(result);
        verify(userDao, never()).findByUsername(anyString());
    }

    @Test
    @DisplayName("测试查看特定会议权限 - 会议为空")
    public void testCanViewMeeting_NullMeeting() {
        // 执行测试
        boolean result = permissionUtil.canViewMeeting("admin", null);

        // 验证结果
        assertFalse(result);
        verify(userDao, never()).findByUsername(anyString());
    }

    @Test
    @DisplayName("测试查看特定会议权限 - 用户名和会议都为空")
    public void testCanViewMeeting_BothNull() {
        // 执行测试
        boolean result = permissionUtil.canViewMeeting(null, null);

        // 验证结果
        assertFalse(result);
        verify(userDao, never()).findByUsername(anyString());
    }

    @Test
    @DisplayName("测试用户不存在的情况")
    public void testUserNotFound() {
        // 准备测试数据
        when(userDao.findByUsername("nonexistent")).thenReturn(null);

        // 测试各种权限检查
        assertFalse(permissionUtil.isAdmin("nonexistent"));
        assertFalse(permissionUtil.isEnterpriseUser("nonexistent"));
        assertTrue(permissionUtil.isNormalUser("nonexistent")); // 不存在用户被认为是普通用户
        assertFalse(permissionUtil.canEditMeeting("nonexistent", "admin"));
        assertFalse(permissionUtil.canDeleteMeeting("nonexistent", "admin"));
        assertFalse(permissionUtil.canReviewMeeting("nonexistent"));
        assertFalse(permissionUtil.canCreateMeeting("nonexistent"));

        // 验证调用 - 每个方法都会调用findByUsername
        // isAdmin(1) + isEnterpriseUser(1) + isNormalUser(2) + canEditMeeting(1) + canDeleteMeeting(1) + canReviewMeeting(1) + canCreateMeeting(2) = 9次
        verify(userDao, times(9)).findByUsername("nonexistent");
    }

    @Test
    @DisplayName("测试边界情况 - 空字符串用户名")
    public void testEmptyStringUsername() {
        // 测试各种权限检查
        assertFalse(permissionUtil.isAdmin(""));
        assertFalse(permissionUtil.isEnterpriseUser(""));
        assertTrue(permissionUtil.isNormalUser("")); // 空字符串被认为是普通用户
        assertFalse(permissionUtil.canEditMeeting("", "admin"));
        assertFalse(permissionUtil.canDeleteMeeting("", "admin"));
        assertFalse(permissionUtil.canReviewMeeting(""));
        assertFalse(permissionUtil.canCreateMeeting(""));

        // 验证调用 - 每个方法都会调用findByUsername
        // isAdmin(1) + isEnterpriseUser(1) + isNormalUser(2) + canEditMeeting(1) + canDeleteMeeting(1) + canReviewMeeting(1) + canCreateMeeting(2) = 9次
        verify(userDao, times(9)).findByUsername("");
    }
} 