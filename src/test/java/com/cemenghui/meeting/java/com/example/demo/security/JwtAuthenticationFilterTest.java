package com.cemenghui.meeting.java.com.example.demo.security;

import com.cemenghui.meeting.util.JwtUtil;
import com.cemenghui.meeting.security.CustomUserDetailsService;
import com.cemenghui.meeting.security.JwtAuthenticationFilter;

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
    private JwtUtil jwtUtil;

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
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

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
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

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
        
        when(jwtUtil.getUsernameFromToken(token)).thenReturn("testuser");
        when(jwtUtil.validateToken(token)).thenReturn(true);
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);

        // 执行测试
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // 验证结果
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertTrue(SecurityContextHolder.getContext().getAuthentication() instanceof UsernamePasswordAuthenticationToken);
        assertEquals("testuser", SecurityContextHolder.getContext().getAuthentication().getName());

        // 验证调用
        verify(jwtUtil).getUsernameFromToken(token);
        verify(jwtUtil).validateToken(token);
        verify(userDetailsService).loadUserByUsername("testuser");
    }

    @Test
    @DisplayName("处理请求-无效JWT Token")
    void testDoFilterInternal_InvalidJwtToken() throws ServletException, IOException {
        // 准备测试数据
        String token = "invalid.jwt.token";
        request.addHeader("Authorization", "Bearer " + token);
        
        UserDetails userDetails = mock(UserDetails.class);
        lenient().when(userDetails.getUsername()).thenReturn("testuser");
        
        when(jwtUtil.getUsernameFromToken(token)).thenReturn("testuser");
        when(jwtUtil.validateToken(token)).thenReturn(false);
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);

        // 执行测试
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // 验证结果
        assertNull(SecurityContextHolder.getContext().getAuthentication());

        // 验证调用
        verify(jwtUtil).getUsernameFromToken(token);
        verify(jwtUtil).validateToken(token);
        verify(userDetailsService).loadUserByUsername("testuser");
    }

    @Test
    @DisplayName("处理请求-用户不存在")
    void testDoFilterInternal_UserNotFound() throws ServletException, IOException {
        // 准备测试数据
        String token = "valid.jwt.token";
        request.addHeader("Authorization", "Bearer " + token);
        
        when(jwtUtil.getUsernameFromToken(token)).thenReturn("testuser");
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(null);
        when(jwtUtil.validateToken(token)).thenReturn(false);

        // 执行测试
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // 验证结果
        assertNull(SecurityContextHolder.getContext().getAuthentication());

        // 验证调用
        verify(jwtUtil).getUsernameFromToken(token);
        verify(userDetailsService).loadUserByUsername("testuser");
        verify(jwtUtil).validateToken(token);
    }

    @Test
    @DisplayName("处理请求-已存在认证信息")
    void testDoFilterInternal_ExistingAuthentication() throws ServletException, IOException {
        // 准备测试数据
        UsernamePasswordAuthenticationToken existingAuth = new UsernamePasswordAuthenticationToken("existinguser", null, null);
        SecurityContextHolder.getContext().setAuthentication(existingAuth);
        
        String token = "valid.jwt.token";
        request.addHeader("Authorization", "Bearer " + token);

        // 执行测试
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // 验证结果
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("existinguser", SecurityContextHolder.getContext().getAuthentication().getName());

        // 验证调用 - 即使有认证信息，仍然会解析token但不设置新的认证
        verify(jwtUtil).getUsernameFromToken(token);
        verify(userDetailsService, never()).loadUserByUsername(anyString());
    }

    @Test
    @DisplayName("处理请求-异常处理")
    void testDoFilterInternal_ExceptionHandling() throws ServletException, IOException {
        // 准备测试数据
        String token = "valid.jwt.token";
        request.addHeader("Authorization", "Bearer " + token);
        
        when(jwtUtil.getUsernameFromToken(token)).thenThrow(new RuntimeException("JWT处理异常"));

        // 执行测试
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // 验证结果
        assertNull(SecurityContextHolder.getContext().getAuthentication());

        // 验证调用
        verify(jwtUtil).getUsernameFromToken(token);
        verify(userDetailsService, never()).loadUserByUsername(anyString());
    }
} 