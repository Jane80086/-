package com.cemenghui.news.security;

import com.cemenghui.news.mapper.UserMapper;
import com.cemenghui.common.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 自定义用户详情服务，用于从数据库加载用户数据。
 * 实现了Spring Security的UserDetailsService接口。
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserMapper userMapper;

    /**
     * 根据用户名加载用户详情。
     * @param username 用户名
     * @return UserDetails对象
     * @throws UsernameNotFoundException 如果用户不存在
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 从数据库中根据用户名查找用户
        User user = userMapper.findByUsername(username);
        if (user == null) {
            // 如果用户不存在，抛出UsernameNotFoundException
            throw new UsernameNotFoundException("用户不存在: " + username);
        }

        // 构建权限列表
        List<GrantedAuthority> authorities = new ArrayList<>();
        // 根据用户类型赋予角色。
        // Spring Security推荐角色前缀为"ROLE_"
        if ("ADMIN".equals(user.getUserType())) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        } else if ("ENTERPRISE".equals(user.getUserType())) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ENTERPRISE"));
        } else if ("NORMAL".equals(user.getUserType())) {
            authorities.add(new SimpleGrantedAuthority("ROLE_NORMAL"));
        }
        // 如果您的User实体有permissions字段，可以进一步细化权限，例如：
        // if (user.getPermissions() != null && !user.getPermissions().isEmpty()) {
        //     // 假设permissions是一个逗号分隔的字符串，例如 "news:edit,news:delete"
        //     String[] perms = user.getPermissions().split(",");
        //     for (String perm : perms) {
        //         authorities.add(new SimpleGrantedAuthority(perm.trim()));
        //     }
        // }

        // 返回Spring Security的User对象，其中包含用户名、加密后的密码和权限列表
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(), // 注意：这里密码需要是加密后的
                authorities
        );
    }
}
