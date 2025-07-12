//package com.cemenghui.common;
//
//import io.jsonwebtoken.Claims; // 需要导入 Claims
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority; // 需要导入 SimpleGrantedAuthority
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.util.StringUtils;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.util.Arrays; // 需要导入 Arrays
//import java.util.Collection; // 需要导入 Collection
//import java.util.Collections;
//import java.util.stream.Collectors; // 需要导入 Collectors
//
//public class JwtAuthenticationFilter extends OncePerRequestFilter {
//    private final JWTUtil jwtUtil;
//
//    public JwtAuthenticationFilter(JWTUtil jwtUtil) {
//        this.jwtUtil = jwtUtil;
//    }
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//            throws ServletException, IOException {
//        String token = getTokenFromRequest(request);
//
//        if (StringUtils.hasText(token) && jwtUtil.validateToken(token)) {
//            // 1. 获取 JWT 的所有 claims
//            Claims claims = jwtUtil.getAllClaimsFromToken(token);
//
//            // 2. 从 claims 中提取 username (subject)
//            String username = claims.getSubject(); // 通常 subject 就是 username
//
//            // 3. 从 "scope" claim 中提取权限信息
//            String scopeString = claims.get("scope", String.class);
//            Collection<? extends GrantedAuthority> authorities = null;
//
//            if (StringUtils.hasText(scopeString)) {
//                // "scope" claim 存储的是用空格分隔的权限字符串，例如 "ROLE_ADMIN ROLE_USER"
//                authorities = Arrays.stream(scopeString.split(" "))
//                        .map(SimpleGrantedAuthority::new)
//                        .collect(Collectors.toList());
//            } else {
//                // 如果没有 scope，可以给一个默认的空权限列表
//                authorities = Collections.emptyList();
//            }
//
//            // 4. 使用提取到的 username 和 authorities 创建 UsernamePasswordAuthenticationToken
//            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
//                    username, null, authorities); // <-- 这里传入了正确的权限列表
//
//            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//        }
//        filterChain.doFilter(request, response);
//    }
//
//    private String getTokenFromRequest(HttpServletRequest request) {
//        String bearerToken = request.getHeader("Authorization");
//        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
//            return bearerToken.substring(7);
//        }
//        return null;
//    }
//}