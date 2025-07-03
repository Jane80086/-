package com.cemenghui.news.security;

import com.cemenghui.news.util.JwtUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain; // 修改为javax.servlet.FilterChain
import javax.servlet.ServletException; // 修改为javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest; // 修改为javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse; // 修改为javax.servlet.http.HttpServletResponse
import java.io.IOException;
import lombok.RequiredArgsConstructor; // 确保导入

/**
 * JWT认证过滤器，用于拦截所有请求，解析JWT并设置Spring Security的认证信息。
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService; // 您的CustomUserDetailsService实例

    /**
     * 过滤请求，检查并处理JWT令牌。
     * @param request HttpServlet请求
     * @param response HttpServlet响应
     * @param filterChain 过滤器链
     * @throws ServletException 如果发生Servlet错误
     * @throws IOException 如果发生I/O错误
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // 从请求头中获取Authorization字段
        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        // 检查Authorization头是否存在且以"Bearer "开头
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            // 提取JWT令牌
            jwt = authorizationHeader.substring(7);
            try {
                // 从JWT中提取用户名
                username = jwtUtil.extractUsername(jwt);
            } catch (Exception e) {
                // 记录JWT解析异常，例如令牌过期、签名无效等
                logger.error("JWT Token Error: " + e.getMessage());
                // 这里可以根据需要向响应中写入更详细的错误信息
            }
        }

        // 如果成功提取到用户名且当前SecurityContext中没有认证信息
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // 根据用户名加载用户详情
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // 验证JWT令牌是否有效且未过期
            if (jwtUtil.validateToken(jwt, userDetails)) {
                // 创建UsernamePasswordAuthenticationToken，表示用户已认证
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                // 设置认证详情，包括请求的IP地址和会话ID
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // 将认证信息设置到SecurityContext中
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
        // 继续执行过滤器链中的下一个过滤器或目标资源
        filterChain.doFilter(request, response);
    }
}
