package com.cemenghui.meeting.java.com.example.demo.security;  

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.AuthenticationException;

import com.cemenghui.meeting.security.JwtAuthenticationEntryPoint;

import javax.servlet.ServletException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("JWT认证入口点测试")
class JwtAuthenticationEntryPointTest {

    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private AuthenticationException authException;

    @BeforeEach
    void setUp() {
        jwtAuthenticationEntryPoint = new JwtAuthenticationEntryPoint();
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        authException = mock(AuthenticationException.class);
        when(authException.getMessage()).thenReturn("认证失败");
    }

    @Test
    @DisplayName("处理未认证请求-成功")
    void testCommence_Success() throws ServletException, IOException {
        // 执行测试
        jwtAuthenticationEntryPoint.commence(request, response, authException);

        // 验证结果
        assertEquals(401, response.getStatus());
        assertEquals("application/json;charset=UTF-8", response.getContentType());
        assertTrue(response.getContentAsString().contains("未授权访问，请先登录"));
        assertTrue(response.getContentAsString().contains("\"code\":401"));
    }

    @Test
    @DisplayName("处理未认证请求-不同请求路径")
    void testCommence_DifferentPaths() throws ServletException, IOException {
        // 测试不同的请求路径
        String[] paths = {"/api/meetings", "/api/users", "/api/files", "/"};
        
        for (String path : paths) {
            request.setRequestURI(path);
            response.reset();
            
            // 执行测试
            jwtAuthenticationEntryPoint.commence(request, response, authException);

            // 验证结果
            assertEquals(401, response.getStatus());
            assertEquals("application/json;charset=UTF-8", response.getContentType());
            assertTrue(response.getContentAsString().contains("未授权访问，请先登录"));
            assertTrue(response.getContentAsString().contains("\"code\":401"));
        }
    }

    @Test
    @DisplayName("处理未认证请求-不同HTTP方法")
    void testCommence_DifferentMethods() throws ServletException, IOException {
        // 测试不同的HTTP方法
        String[] methods = {"GET", "POST", "PUT", "DELETE", "OPTIONS"};
        
        for (String method : methods) {
            request.setMethod(method);
            response.reset();
            
            // 执行测试
            jwtAuthenticationEntryPoint.commence(request, response, authException);

            // 验证结果
            assertEquals(401, response.getStatus());
            assertEquals("application/json;charset=UTF-8", response.getContentType());
            assertTrue(response.getContentAsString().contains("未授权访问，请先登录"));
            assertTrue(response.getContentAsString().contains("\"code\":401"));
        }
    }

    @Test
    @DisplayName("处理未认证请求-空异常消息")
    void testCommence_EmptyExceptionMessage() throws ServletException, IOException {
        // 准备测试数据
        when(authException.getMessage()).thenReturn(null);

        // 执行测试
        jwtAuthenticationEntryPoint.commence(request, response, authException);

        // 验证结果
        assertEquals(401, response.getStatus());
        assertEquals("application/json;charset=UTF-8", response.getContentType());
        assertTrue(response.getContentAsString().contains("未授权访问，请先登录"));
        assertTrue(response.getContentAsString().contains("\"code\":401"));
    }

    @Test
    @DisplayName("处理未认证请求-验证JSON格式")
    void testCommence_JsonFormat() throws ServletException, IOException {
        // 执行测试
        jwtAuthenticationEntryPoint.commence(request, response, authException);

        // 验证结果
        String content = response.getContentAsString();
        
        // 验证JSON格式
        assertTrue(content.startsWith("{"));
        assertTrue(content.endsWith("}"));
        assertTrue(content.contains("\"code\""));
        assertTrue(content.contains("\"message\""));
        assertTrue(content.contains("\"data\""));
        assertTrue(content.contains("401"));
        assertTrue(content.contains("未授权访问，请先登录"));
    }
} 