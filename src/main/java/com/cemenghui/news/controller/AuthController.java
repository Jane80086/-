package com.cemenghui.news.controller;

import com.cemenghui.news.dto.LoginRequest;
import com.cemenghui.common.Result;
import com.cemenghui.news.security.CustomUserDetailsService;
import com.cemenghui.news.util.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid; // 修改为javax.validation.Valid
import lombok.RequiredArgsConstructor; // 确保导入

/**
 * 认证控制器，处理用户登录请求并返回JWT。
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    /**
     * 处理用户登录请求。
     * 成功认证后，生成JWT令牌并返回给前端。
     * @param loginRequest 登录请求DTO，包含用户名和密码
     * @return 包含JWT令牌的Result对象
     */
    @PostMapping("/login")
    public Result authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        // 使用AuthenticationManager进行认证
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        // 将认证信息设置到SecurityContext中
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 加载用户详情以生成JWT
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());
        // 生成JWT令牌
        String jwt = jwtUtil.generateToken(userDetails);

        // 返回成功信息和JWT令牌
        return Result.success("登录成功", jwt); // 返回JWT给前端
    }
}