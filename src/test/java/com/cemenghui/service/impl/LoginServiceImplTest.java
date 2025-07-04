package com.system.service.impl;

import com.system.entity.AdminUser;
import com.system.entity.EnterpriseUser;
import com.system.entity.User;
import com.system.dto.LoginResponseDTO;
import com.system.repository.AdminUserMapper;
import com.system.repository.EnterpriseUserMapper;
import com.system.repository.UserMapper;
import com.system.util.JWTUtil;
import com.system.util.PasswordUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoginServiceImplTest {

    @InjectMocks
    private LoginServiceImpl loginService;

    @Mock
    private UserMapper userMapper;
    @Mock
    private AdminUserMapper adminUserMapper;
    @Mock
    private EnterpriseUserMapper enterpriseUserMapper;
    @Mock
    private JWTUtil jwtUtil;
    @Mock
    private PasswordUtil passwordUtil;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        when(jwtUtil.generateToken(anyString())).thenReturn("token");
        when(jwtUtil.generateToken(any(AdminUser.class))).thenReturn("token");
        // 反射注入 mock
        java.lang.reflect.Field jwtUtilField = LoginServiceImpl.class.getDeclaredField("jwtUtil");
        jwtUtilField.setAccessible(true);
        jwtUtilField.set(loginService, jwtUtil);
        java.lang.reflect.Field passwordUtilField = LoginServiceImpl.class.getDeclaredField("passwordUtil");
        passwordUtilField.setAccessible(true);
        passwordUtilField.set(loginService, passwordUtil);
        java.lang.reflect.Field userMapperField = LoginServiceImpl.class.getDeclaredField("userMapper");
        userMapperField.setAccessible(true);
        userMapperField.set(loginService, userMapper);
        java.lang.reflect.Field adminUserMapperField = LoginServiceImpl.class.getDeclaredField("adminUserMapper");
        adminUserMapperField.setAccessible(true);
        adminUserMapperField.set(loginService, adminUserMapper);
        java.lang.reflect.Field enterpriseUserMapperField = LoginServiceImpl.class.getDeclaredField("enterpriseUserMapper");
        enterpriseUserMapperField.setAccessible(true);
        enterpriseUserMapperField.set(loginService, enterpriseUserMapper);
    }

    @Test
    void testEnterpriseLogin_userNotExist() {
        when(enterpriseUserMapper.findByAccount("a")).thenReturn(null);
        LoginResponseDTO resp = loginService.enterpriseLogin("a", "p", false, "d");
        assertFalse(resp.isSuccess());
        assertTrue(resp.getMessage().contains("用户不存在"));
    }

    @Test
    void testEnterpriseLogin_passwordWrong() {
        EnterpriseUser user = new EnterpriseUser();
        user.setPassword("enc");
        when(enterpriseUserMapper.findByAccount("a")).thenReturn(user);
        when(passwordUtil.matches("p", "enc")).thenReturn(false);
        LoginResponseDTO resp = loginService.enterpriseLogin("a", "p", false, "d");
        assertFalse(resp.isSuccess());
        assertTrue(resp.getMessage().contains("密码错误"));
    }

    @Test
    void testEnterpriseLogin_dynamicCodeWrong() {
        EnterpriseUser user = new EnterpriseUser();
        user.setPassword("enc");
        user.setDynamicCode("right");
        when(enterpriseUserMapper.findByAccount("a")).thenReturn(user);
        when(passwordUtil.matches("p", "enc")).thenReturn(true);
        LoginResponseDTO resp = loginService.enterpriseLogin("a", "p", false, "wrong");
        assertFalse(resp.isSuccess());
        assertTrue(resp.getMessage().contains("动态验证码错误"));
    }

    @Test
    void testEnterpriseLogin_rememberMe() {
        EnterpriseUser user = new EnterpriseUser();
        user.setPassword("enc");
        user.setDynamicCode("d");
        user.setAccount("a");
        when(enterpriseUserMapper.findByAccount("a")).thenReturn(user);
        when(passwordUtil.matches("p", "enc")).thenReturn(true);
        LoginResponseDTO resp = loginService.enterpriseLogin("a", "p", true, "d");
        assertTrue(resp.isSuccess());
        assertEquals("token", resp.getToken());
        verify(enterpriseUserMapper).update(user);
    }

    @Test
    void testAdminLogin_adminNotExist() {
        when(adminUserMapper.findByAccount("a")).thenReturn(null);
        LoginResponseDTO resp = loginService.adminLogin("a", "p", "d");
        assertFalse(resp.isSuccess());
        assertTrue(resp.getMessage().contains("管理员不存在"));
    }

    @Test
    void testAdminLogin_passwordWrong() {
        AdminUser admin = new AdminUser();
        admin.setPassword("enc");
        when(adminUserMapper.findByAccount("a")).thenReturn(admin);
        when(passwordUtil.matches("p", "enc")).thenReturn(false);
        LoginResponseDTO resp = loginService.adminLogin("a", "p", "d");
        assertFalse(resp.isSuccess());
        assertTrue(resp.getMessage().contains("密码错误"));
    }

    @Test
    void testAdminLogin_dynamicCodeWrong() {
        AdminUser admin = new AdminUser();
        admin.setPassword("enc");
        admin.setDynamicCode("right");
        when(adminUserMapper.findByAccount("a")).thenReturn(admin);
        when(passwordUtil.matches("p", "enc")).thenReturn(true);
        LoginResponseDTO resp = loginService.adminLogin("a", "p", "wrong");
        assertFalse(resp.isSuccess());
        assertTrue(resp.getMessage().contains("动态验证码错误"));
    }

    @Test
    void testAdminLogin_success() {
        AdminUser admin = new AdminUser();
        admin.setPassword("enc");
        admin.setDynamicCode("d");
        admin.setAccount("a");
        when(adminUserMapper.findByAccount("a")).thenReturn(admin);
        when(passwordUtil.matches("p", "enc")).thenReturn(true);
        LoginResponseDTO resp = loginService.adminLogin("a", "p", "d");
        assertTrue(resp.isSuccess());
        assertEquals("token", resp.getToken());
    }

    @Test
    void testCheckDynamicCode_userNotExist() {
        when(userMapper.findEnterpriseByAccount("a")).thenReturn(null);
        assertFalse(loginService.checkDynamicCode("a", "d"));
    }

    @Test
    void testCheckDynamicCode_codeMatch() {
        EnterpriseUser user = new EnterpriseUser();
        user.setDynamicCode("d");
        when(userMapper.findEnterpriseByAccount("a")).thenReturn(user);
        assertTrue(loginService.checkDynamicCode("a", "d"));
    }

    @Test
    void testCheckDynamicCode_codeNotMatch() {
        EnterpriseUser user = new EnterpriseUser();
        user.setDynamicCode("d");
        when(userMapper.findEnterpriseByAccount("a")).thenReturn(user);
        assertFalse(loginService.checkDynamicCode("a", "wrong"));
    }

    @Test
    void testGenerateToken() {
        User user = new User();
        user.setAccount("a");
        when(jwtUtil.generateToken("a")).thenReturn("token");
        assertEquals("token", loginService.generateToken(user));
    }

    @Test
    void testValidateToken() {
        assertFalse(loginService.validateToken("any"));
    }

    @Test
    void testGetUserFromToken() {
        when(jwtUtil.getAccountFromToken("t")).thenReturn("a");
        assertEquals("a", loginService.getUserFromToken("t"));
    }

    @Test
    void testRegisterThirdPartyUser_private() throws Exception {
        java.lang.reflect.Method method = LoginServiceImpl.class.getDeclaredMethod("registerThirdPartyUser", String.class, String.class);
        method.setAccessible(true);
        doNothing().when(userMapper).saveEnterpriseUser(any());
        Object result = method.invoke(loginService, "wx", "openid");
        assertNotNull(result);
        assertTrue(result instanceof EnterpriseUser);
        assertTrue(((EnterpriseUser) result).getAccount().startsWith("wx_openid"));
    }

    @Test
    void testLogin_null() {
        assertNull(loginService.login("a", "b"));
    }

    @Test
    void testEnterpriseLogin_noRememberMe() {
        EnterpriseUser user = new EnterpriseUser();
        user.setPassword("enc");
        user.setDynamicCode("d");
        user.setAccount("a");
        when(enterpriseUserMapper.findByAccount("a")).thenReturn(user);
        when(passwordUtil.matches("p", "enc")).thenReturn(true);
        LoginResponseDTO resp = loginService.enterpriseLogin("a", "p", false, "d");
        assertTrue(resp.isSuccess());
        assertEquals("token", resp.getToken());
        verify(enterpriseUserMapper, never()).update(user);
    }

    @Test
    void testEnterpriseLogin_userNull() {
        when(enterpriseUserMapper.findByAccount("notfound")).thenReturn(null);
        LoginResponseDTO resp = loginService.enterpriseLogin("notfound", "p", false, "d");
        assertFalse(resp.isSuccess());
    }

    @Test
    void testEnterpriseLogin_passwordNotMatch() {
        EnterpriseUser user = new EnterpriseUser();
        user.setPassword("enc");
        user.setAccount("a");
        when(enterpriseUserMapper.findByAccount("a")).thenReturn(user);
        when(passwordUtil.matches("p", "enc")).thenReturn(false);
        LoginResponseDTO resp = loginService.enterpriseLogin("a", "p", false, "d");
        assertFalse(resp.isSuccess());
    }

    @Test
    void testEnterpriseLogin_dynamicCodeNotMatch() {
        EnterpriseUser user = new EnterpriseUser();
        user.setPassword("enc");
        user.setDynamicCode("right");
        user.setAccount("a");
        when(enterpriseUserMapper.findByAccount("a")).thenReturn(user);
        when(passwordUtil.matches("p", "enc")).thenReturn(true);
        LoginResponseDTO resp = loginService.enterpriseLogin("a", "p", false, "wrong");
        assertFalse(resp.isSuccess());
    }

    @Test
    void testAdminLogin_adminNull() {
        when(adminUserMapper.findByAccount("notfound")).thenReturn(null);
        LoginResponseDTO resp = loginService.adminLogin("notfound", "p", "d");
        assertFalse(resp.isSuccess());
    }

    @Test
    void testAdminLogin_passwordNotMatch() {
        AdminUser admin = new AdminUser();
        admin.setPassword("enc");
        admin.setAccount("a");
        when(adminUserMapper.findByAccount("a")).thenReturn(admin);
        when(passwordUtil.matches("p", "enc")).thenReturn(false);
        LoginResponseDTO resp = loginService.adminLogin("a", "p", "d");
        assertFalse(resp.isSuccess());
    }

    @Test
    void testAdminLogin_dynamicCodeNotMatch() {
        AdminUser admin = new AdminUser();
        admin.setPassword("enc");
        admin.setDynamicCode("right");
        admin.setAccount("a");
        when(adminUserMapper.findByAccount("a")).thenReturn(admin);
        when(passwordUtil.matches("p", "enc")).thenReturn(true);
        LoginResponseDTO resp = loginService.adminLogin("a", "p", "wrong");
        assertFalse(resp.isSuccess());
    }
}