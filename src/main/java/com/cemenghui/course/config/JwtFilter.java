package com.cemenghui.course.config;

import com.cemenghui.course.util.JwtUtil;
import com.cemenghui.course.shiro.AccountProfile;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            if (jwtUtil.validateToken(token)) {
                Claims claims = jwtUtil.getAllClaimsFromToken(token);
                String profileJson = claims.get("profile", String.class);
                if (profileJson != null) {
                    AccountProfile profile = objectMapper.readValue(profileJson, AccountProfile.class);
                    request.setAttribute("profile", profile);
                }
            }
        }
        filterChain.doFilter(request, response);
    }
} 