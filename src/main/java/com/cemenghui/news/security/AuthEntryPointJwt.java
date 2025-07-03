package com.cemenghui.news.security;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException; // 修改为javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest; // 修改为javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse; // 修改为javax.servlet.http.HttpServletResponse
import java.io.IOException;

/**
 * 处理未经认证的请求的入口点。
 * 当用户尝试访问需要认证的资源但未提供任何凭证，或者凭证无效时，此方法会被调用。
 */
@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

    /**
     * 当用户未经认证尝试访问受保护的资源时调用。
     * @param request HttpServlet请求
     * @param response HttpServlet响应
     * @param authException 认证异常
     * @throws IOException 如果发生I/O错误
     * @throws ServletException 如果发生Servlet错误
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        // 返回401 Unauthorized状态码和错误信息
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error: Unauthorized");
    }
}

