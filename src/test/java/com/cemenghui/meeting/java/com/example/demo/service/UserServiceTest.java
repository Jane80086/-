package com.cemenghui.meeting.java.com.example.demo.service;

import com.cemenghui.meeting.bean.User;
import com.cemenghui.meeting.bean.UserLoginRequest;
import com.cemenghui.meeting.bean.UserRegisterRequest;
import com.cemenghui.meeting.dao.UserDao;
import com.cemenghui.meeting.service.UserService;
import com.cemenghui.meeting.util.JwtUtil;
import com.cemenghui.meeting.util.ValidationUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("用户服务测试")
class UserServiceTest {

    @Mock
    private UserDao userDao;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private ValidationUtil validationUtil;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UserRegisterRequest registerRequest;
    private UserLoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .password("encodedPassword")
                .realName("测试用户")
                .email("test@example.com")
                .phone("13800138000")
                .userType("NORMAL")
                .status(1)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();

        registerRequest = new UserRegisterRequest();
        registerRequest.setUsername("newuser");
        registerRequest.setPassword("password123");
        registerRequest.setRealName("新用户");
        registerRequest.setEmail("new@example.com");
        registerRequest.setPhone("13900139000");
        registerRequest.setUserType("NORMAL");

        loginRequest = new UserLoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");
    }

    @Test
    @DisplayName("用户注册-成功")
    void testRegister_Success() {
        // 准备测试数据
        when(userDao.findByUsername("newuser")).thenReturn(null);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userDao.insert(any(User.class))).thenReturn(1);

        // 执行测试
        User result = userService.register(registerRequest);

        // 验证结果
        assertNotNull(result);
        assertEquals("newuser", result.getUsername());
        assertNull(result.getPassword()); // 密码应该被清除
        assertEquals("新用户", result.getRealName());
        assertEquals("new@example.com", result.getEmail());
        assertEquals("13900139000", result.getPhone());
        assertEquals("NORMAL", result.getUserType());
        assertEquals(1, result.getStatus());

        // 验证调用
        verify(userDao).findByUsername("newuser");
        verify(passwordEncoder).encode("password123");
        verify(userDao).insert(any(User.class));
    }

    @Test
    @DisplayName("用户注册-用户名已存在")
    void testRegister_UsernameExists() {
        // 准备测试数据
        when(userDao.findByUsername("newuser")).thenReturn(testUser);

        // 执行测试并验证异常
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.register(registerRequest);
        });

        assertEquals("用户名已存在", exception.getMessage());

        // 验证调用
        verify(userDao).findByUsername("newuser");
        verify(userDao, never()).insert(any(User.class));
    }

    @Test
    @DisplayName("用户登录-成功")
    void testLogin_Success() {
        // 准备测试数据
        loginRequest.setPassword("123456"); // 使用默认密码
        when(userDao.findByUsername("testuser")).thenReturn(testUser);
        when(jwtUtil.generateToken("testuser")).thenReturn("jwtToken");

        // 执行测试
        String token = userService.login(loginRequest);

        // 验证结果
        assertEquals("jwtToken", token);

        // 验证调用
        verify(userDao).findByUsername("testuser");
        verify(jwtUtil).generateToken("testuser");
    }

    @Test
    @DisplayName("用户登录-用户不存在")
    void testLogin_UserNotFound() {
        // 准备测试数据
        when(userDao.findByUsername("testuser")).thenReturn(null);

        // 执行测试并验证异常
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.login(loginRequest);
        });

        assertEquals("用户名或密码错误", exception.getMessage());

        // 验证调用
        verify(userDao).findByUsername("testuser");
        verify(jwtUtil, never()).generateToken(anyString());
    }

    @Test
    @DisplayName("用户登录-密码错误")
    void testLogin_WrongPassword() {
        // 准备测试数据
        when(userDao.findByUsername("testuser")).thenReturn(testUser);

        // 执行测试并验证异常
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.login(loginRequest);
        });

        assertEquals("用户名或密码错误", exception.getMessage());

        // 验证调用
        verify(userDao).findByUsername("testuser");
        verify(jwtUtil, never()).generateToken(anyString());
    }

    @Test
    @DisplayName("用户登录-用户被禁用")
    void testLogin_UserDisabled() {
        // 准备测试数据
        testUser.setStatus(0);
        loginRequest.setPassword("123456"); // 使用默认密码
        when(userDao.findByUsername("testuser")).thenReturn(testUser);

        // 执行测试并验证异常
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.login(loginRequest);
        });

        assertEquals("用户已被禁用", exception.getMessage());

        // 验证调用
        verify(userDao).findByUsername("testuser");
        verify(jwtUtil, never()).generateToken(anyString());
    }

    @Test
    @DisplayName("更新密码-成功")
    void testUpdatePassword_Success() {
        // 准备测试数据
        when(userDao.selectById(1L)).thenReturn(testUser);
        when(passwordEncoder.encode("newPassword")).thenReturn("newEncodedPassword");
        when(userDao.updateById(any(User.class))).thenReturn(1);

        // 执行测试
        boolean result = userService.updatePassword(1L, "newPassword");

        // 验证结果
        assertTrue(result);

        // 验证调用
        verify(userDao).selectById(1L);
        verify(passwordEncoder).encode("newPassword");
        verify(userDao).updateById(any(User.class));
    }

    @Test
    @DisplayName("更新密码-用户不存在")
    void testUpdatePassword_UserNotFound() {
        // 准备测试数据
        when(userDao.selectById(1L)).thenReturn(null);

        // 执行测试
        boolean result = userService.updatePassword(1L, "newPassword");

        // 验证结果
        assertFalse(result);

        // 验证调用
        verify(userDao).selectById(1L);
        verify(passwordEncoder, never()).encode(anyString());
        verify(userDao, never()).updateById(any(User.class));
    }

    @Test
    @DisplayName("根据用户名获取用户信息-成功")
    void testGetUserByUsername_Success() {
        // 准备测试数据
        when(userDao.findByUsername("testuser")).thenReturn(testUser);

        // 执行测试
        User result = userService.getUserByUsername("testuser");

        // 验证结果
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertNull(result.getPassword()); // 密码应该被清除

        // 验证调用
        verify(userDao).findByUsername("testuser");
    }

    @Test
    @DisplayName("根据用户名获取用户信息-用户不存在")
    void testGetUserByUsername_UserNotFound() {
        // 准备测试数据
        when(userDao.findByUsername("nonexistent")).thenReturn(null);

        // 执行测试
        User result = userService.getUserByUsername("nonexistent");

        // 验证结果
        assertNull(result);

        // 验证调用
        verify(userDao).findByUsername("nonexistent");
    }

    @Test
    @DisplayName("获取所有用户-成功")
    void testGetAllUsers_Success() {
        // 准备测试数据
        List<User> users = Arrays.asList(testUser);
        when(userDao.selectList(any())).thenReturn(users);

        // 执行测试
        List<User> result = userService.getAllUsers();

        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("testuser", result.get(0).getUsername());
        assertNull(result.get(0).getPassword()); // 密码应该被清除

        // 验证调用
        verify(userDao).selectList(any());
    }

    @Test
    @DisplayName("更新用户状态-成功")
    void testUpdateUserStatus_Success() {
        // 准备测试数据
        when(userDao.selectById(1L)).thenReturn(testUser);
        when(userDao.updateById(any(User.class))).thenReturn(1);

        // 执行测试
        boolean result = userService.updateUserStatus(1L, 0);

        // 验证结果
        assertTrue(result);

        // 验证调用
        verify(userDao).selectById(1L);
        verify(userDao).updateById(any(User.class));
    }

    @Test
    @DisplayName("更新用户状态-失败")
    void testUpdateUserStatus_Failed() {
        // 准备测试数据
        when(userDao.selectById(1L)).thenReturn(null);

        // 执行测试
        boolean result = userService.updateUserStatus(1L, 0);

        // 验证结果
        assertFalse(result);

        // 验证调用
        verify(userDao).selectById(1L);
        verify(userDao, never()).updateById(any(User.class));
    }

    @Test
    @DisplayName("删除用户-成功")
    void testDeleteUser_Success() {
        // 准备测试数据
        when(userDao.deleteById(1L)).thenReturn(1);

        // 执行测试
        boolean result = userService.deleteUser(1L);

        // 验证结果
        assertTrue(result);

        // 验证调用
        verify(userDao).deleteById(1L);
    }

    @Test
    @DisplayName("删除用户-失败")
    void testDeleteUser_Failed() {
        // 准备测试数据
        when(userDao.deleteById(1L)).thenReturn(0);

        // 执行测试
        boolean result = userService.deleteUser(1L);

        // 验证结果
        assertFalse(result);

        // 验证调用
        verify(userDao).deleteById(1L);
    }

    @Test
    @DisplayName("检查是否为管理员-是")
    void testIsAdmin_True() {
        // 准备测试数据
        testUser.setUserType("ADMIN");
        when(userDao.findByUsername("admin")).thenReturn(testUser);

        // 执行测试
        boolean result = userService.isAdmin("admin");

        // 验证结果
        assertTrue(result);

        // 验证调用
        verify(userDao).findByUsername("admin");
    }

    @Test
    @DisplayName("检查是否为管理员-否")
    void testIsAdmin_False() {
        // 准备测试数据
        when(userDao.findByUsername("user")).thenReturn(testUser);

        // 执行测试
        boolean result = userService.isAdmin("user");

        // 验证结果
        assertFalse(result);

        // 验证调用
        verify(userDao).findByUsername("user");
    }

    @Test
    @DisplayName("检查是否为企业用户-是")
    void testIsEnterpriseUser_True() {
        // 准备测试数据
        testUser.setUserType("ENTERPRISE");
        when(userDao.findByUsername("enterprise")).thenReturn(testUser);

        // 执行测试
        boolean result = userService.isEnterpriseUser("enterprise");

        // 验证结果
        assertTrue(result);

        // 验证调用
        verify(userDao).findByUsername("enterprise");
    }

    @Test
    @DisplayName("检查是否为企业用户-否")
    void testIsEnterpriseUser_False() {
        // 准备测试数据
        when(userDao.findByUsername("user")).thenReturn(testUser);

        // 执行测试
        boolean result = userService.isEnterpriseUser("user");

        // 验证结果
        assertFalse(result);

        // 验证调用
        verify(userDao).findByUsername("user");
    }

    @Test
    @DisplayName("根据ID获取用户信息-成功")
    void testGetUserById_Success() {
        // 准备测试数据
        when(userDao.selectById(1L)).thenReturn(testUser);

        // 执行测试
        User result = userService.getUserById(1L);

        // 验证结果
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertNull(result.getPassword()); // 密码应该被清除

        // 验证调用
        verify(userDao).selectById(1L);
    }

    @Test
    @DisplayName("根据ID获取用户信息-用户不存在")
    void testGetUserById_UserNotFound() {
        // 准备测试数据
        when(userDao.selectById(1L)).thenReturn(null);

        // 执行测试
        User result = userService.getUserById(1L);

        // 验证结果
        assertNull(result);

        // 验证调用
        verify(userDao).selectById(1L);
    }

    @Test
    @DisplayName("更新用户基本信息-成功")
    void testUpdateUserInfo_Success() {
        // 准备测试数据
        when(userDao.selectById(1L)).thenReturn(testUser);
        when(userDao.updateById(any(User.class))).thenReturn(1);

        // 执行测试
        boolean result = userService.updateUserInfo(testUser);

        // 验证结果
        assertTrue(result);

        // 验证调用
        verify(userDao).selectById(1L);
        verify(userDao).updateById(any(User.class));
    }

    @Test
    @DisplayName("更新用户基本信息-用户不存在")
    void testUpdateUserInfo_UserNotFound() {
        // 准备测试数据
        when(userDao.selectById(1L)).thenReturn(null);

        // 执行测试
        boolean result = userService.updateUserInfo(testUser);

        // 验证结果
        assertFalse(result);

        // 验证调用
        verify(userDao).selectById(1L);
        verify(userDao, never()).updateById(any(User.class));
    }
} 