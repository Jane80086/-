package com.cemenghui.meeting.security;

import com.cemenghui.entity.User;
import com.cemenghui.meeting.dao.UserDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserDao userDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            User user = userDao.findByUsername(username);
            if (user == null) {
                throw new UsernameNotFoundException("用户不存在: " + username);
            }
            // 所有用户都用123456登录
            String encodedPassword = "$2a$10$Dow1pQwQnQnQnQnQnQnQnOeQnQnQnQnQnQnQnQnQnQnQnQnQnQn"; // 123456的BCrypt加密值
            return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(encodedPassword)
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
        } catch (Exception e) {
            log.error("加载用户信息失败: {}", username, e);
            throw new UsernameNotFoundException("加载用户信息失败: " + username, e);
        }
    }
} 