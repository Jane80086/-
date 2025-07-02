package com.cemenghui.meeting.java.com.example.demo.security;       

import com.cemenghui.meeting.bean.User;
import com.cemenghui.meeting.dao.UserDao;
import com.cemenghui.meeting.security.CustomUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomUserDetailsService 单元测试")
public class CustomUserDetailsServiceTest {

    @Mock
    private UserDao userDao;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @BeforeEach
    void setUp() {
        // 设置测试环境
    }

    @Test
    @DisplayName("加载用户信息-成功")
    public void testLoadUserByUsername_Success() {
        // 准备测试数据
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setUserType("NORMAL");

        when(userDao.findByUsername("testuser")).thenReturn(user);

        // 执行测试
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("testuser");

        // 验证结果
        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertTrue(userDetails.isCredentialsNonExpired());
        assertTrue(userDetails.isEnabled());
        assertEquals(1, userDetails.getAuthorities().size());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_USER")));
    }

    @Test
    @DisplayName("加载用户信息-用户不存在")
    public void testLoadUserByUsername_UserNotFound() {
        // 准备测试数据
        when(userDao.findByUsername("nonexistent")).thenReturn(null);

        // 执行测试并验证异常
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetailsService.loadUserByUsername("nonexistent");
        });

        // 验证异常信息
        assertNotNull(exception.getMessage());
        assertTrue(exception.getMessage().contains("nonexistent"));
    }

    @Test
    @DisplayName("加载用户信息-数据库异常")
    public void testLoadUserByUsername_DatabaseException() {
        // 准备测试数据
        when(userDao.findByUsername("testuser")).thenThrow(new RuntimeException("数据库连接失败"));

        // 执行测试并验证异常
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetailsService.loadUserByUsername("testuser");
        });

        // 验证异常信息
        assertTrue(exception.getMessage().contains("加载用户信息失败"));
        assertTrue(exception.getMessage().contains("testuser"));
        assertNotNull(exception.getCause());
    }

    @Test
    @DisplayName("加载管理员用户信息-成功")
    public void testLoadUserByUsername_AdminUser() {
        // 准备测试数据
        User adminUser = new User();
        adminUser.setId(1L);
        adminUser.setUsername("admin");
        adminUser.setUserType("ADMIN");

        when(userDao.findByUsername("admin")).thenReturn(adminUser);

        // 执行测试
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("admin");

        // 验证结果
        assertNotNull(userDetails);
        assertEquals("admin", userDetails.getUsername());
        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertTrue(userDetails.isCredentialsNonExpired());
        assertTrue(userDetails.isEnabled());
        assertEquals(1, userDetails.getAuthorities().size());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_USER")));
    }
} 