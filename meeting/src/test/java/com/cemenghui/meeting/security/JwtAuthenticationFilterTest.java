package com.cemenghui.meeting.security;

import com.cemenghui.common.JWTUtil;
import com.cemenghui.common.JwtAuthenticationFilter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import javax.servlet.ServletException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("JWT认证过滤器测试")
class JwtAuthenticationFilterTest {

    @Mock
    private JWTUtil jwtUtil;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private MockFilterChain filterChain;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = new MockFilterChain();
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("处理请求-无Authorization头")
    void testDoFilterInternal_NoAuthorizationHeader() throws ServletException, IOException {
        // 执行测试
        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        // 验证结果
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(jwtUtil, never()).getUsernameFromToken(anyString());
        verify(userDetailsService, never()).loadUserByUsername(anyString());
    }

    @Test
    @DisplayName("处理请求-Authorization头格式错误")
    void testDoFilterInternal_InvalidAuthorizationHeader() throws ServletException, IOException {
        // 准备测试数据
        request.addHeader("Authorization", "InvalidToken");

        // 执行测试
        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        // 验证结果
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(jwtUtil, never()).getUsernameFromToken(anyString());
        verify(userDetailsService, never()).loadUserByUsername(anyString());
    }

    @Test
    @DisplayName("处理请求-有效JWT Token")
    void testDoFilterInternal_ValidJwtToken() throws ServletException, IOException {
        // 准备测试数据
        String token = "valid.jwt.token";
        request.addHeader("Authorization", "Bearer " + token);
        
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser");
        when(userDetails.getAuthorities()).thenReturn(null);
        when(jwtUtil.validateToken(token)).thenReturn(true);
        when(jwtUtil.getUsernameFromToken(token)).thenReturn("testuser");
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);

        // 执行测试
        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        // 验证结果
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertTrue(SecurityContextHolder.getContext().getAuthentication() instanceof UsernamePasswordAuthenticationToken);
        assertEquals("testuser", SecurityContextHolder.getContext().getAuthentication().getName());

        // 验证调用
        verify(jwtUtil).validateToken(token);
        verify(jwtUtil).getUsernameFromToken(token);
        verify(userDetailsService).loadUserByUsername("testuser");
    }

    @Test
    @DisplayName("处理请求-无效JWT Token")
    void testDoFilterInternal_InvalidJwtToken() throws ServletException, IOException {
        // 准备测试数据
        String token = "invalid.jwt.token";
        request.addHeader("Authorization", "Bearer " + token);
        
        when(jwtUtil.validateToken(token)).thenReturn(false);

        // 执行测试
        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        // 验证结果
        assertNull(SecurityContextHolder.getContext().getAuthentication());

        // 验证调用
        verify(jwtUtil).validateToken(token);
        verify(jwtUtil, never()).getUsernameFromToken(anyString());
        verify(userDetailsService, never()).loadUserByUsername(anyString());
    }

    @Test
    @DisplayName("处理请求-用户不存在")
    void testDoFilterInternal_UserNotFound() throws ServletException, IOException {
        // 准备测试数据
        String token = "valid.jwt.token";
        request.addHeader("Authorization", "Bearer " + token);
        
        when(jwtUtil.validateToken(token)).thenReturn(true);
        when(jwtUtil.getUsernameFromToken(token)).thenReturn("testuser");
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(null);

        // 执行测试
        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        // 验证结果
        assertNull(SecurityContextHolder.getContext().getAuthentication());

        // 验证调用
        verify(jwtUtil).validateToken(token);
        verify(jwtUtil).getUsernameFromToken(token);
        verify(userDetailsService).loadUserByUsername("testuser");
    }

    @Test
    @DisplayName("处理请求-已存在认证信息")
    void testDoFilterInternal_ExistingAuthentication() throws ServletException, IOException {
        // 准备测试数据
        UsernamePasswordAuthenticationToken existingAuth = new UsernamePasswordAuthenticationToken("existinguser", null, null);
        SecurityContextHolder.getContext().setAuthentication(existingAuth);
        
        String token = "valid.jwt.token";
        request.addHeader("Authorization", "Bearer " + token);

        // mock validateToken 返回 false，filter 不会覆盖已有认证
        when(jwtUtil.validateToken(token)).thenReturn(false);

        // 执行测试
        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        // 验证结果
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("existinguser", SecurityContextHolder.getContext().getAuthentication().getName());

        // 验证调用
        verify(jwtUtil).validateToken(token);
        verify(jwtUtil, never()).getUsernameFromToken(anyString());
        verify(userDetailsService, never()).loadUserByUsername(anyString());
    }

    @Test
    @DisplayName("处理请求-异常处理")
    void testDoFilterInternal_ExceptionHandling() throws ServletException, IOException {
        // 准备测试数据
        String token = "valid.jwt.token";
        request.addHeader("Authorization", "Bearer " + token);
        
        when(jwtUtil.validateToken(token)).thenReturn(true);
        when(jwtUtil.getUsernameFromToken(token)).thenThrow(new RuntimeException("JWT处理异常"));

        // 执行测试
        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        // 验证结果
        assertNull(SecurityContextHolder.getContext().getAuthentication());

        // 验证调用
        verify(jwtUtil).validateToken(token);
        verify(jwtUtil).getUsernameFromToken(token);
        verify(userDetailsService, never()).loadUserByUsername(anyString());
    }
} 