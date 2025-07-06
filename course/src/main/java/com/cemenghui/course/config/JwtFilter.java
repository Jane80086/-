package com.cemenghui.course.config;

import com.cemenghui.common.JWTUtil;
import com.cemenghui.course.entity.AccountProfile;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(JwtFilter.class);

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            try {
                if (jwtUtil.validateToken(token)) {
                    Claims claims = jwtUtil.getAllClaimsFromToken(token);
                    String profileJson = claims.get("profile", String.class);
                    if (profileJson != null) {
                        try {
                            AccountProfile profile = objectMapper.readValue(profileJson, AccountProfile.class);
                            request.setAttribute("profile", profile);
                        } catch (Exception e) {
                            log.warn("JWT profile反序列化失败: {}", e.getMessage());
                        }
                    }
                }
            } catch (JwtException e) {
                log.warn("JWT解析失败: {}", e.getMessage());
            } catch (Exception e) {
                log.warn("JWT处理异常: {}", e.getMessage());
            }
        }
        filterChain.doFilter(request, response);
    }
}