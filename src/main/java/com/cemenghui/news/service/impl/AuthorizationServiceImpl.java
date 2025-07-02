package com.cemenghui.news.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cemenghui.common.User;
import com.cemenghui.news.constants.NewsConstants;
import com.cemenghui.news.exception.UnauthorizedException;
import com.cemenghui.news.mapper.NewsMapper;
import com.cemenghui.news.mapper.UserMapper;
import com.cemenghui.news.service.AuthorizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class AuthorizationServiceImpl implements AuthorizationService {

    private final UserMapper userMapper;
    private final NewsMapper newsMapper;

    @Override
    public boolean isOwner(Long userId, Long newsId) {
        if (userId == null || newsId == null) {
            return false;
        }
        // 检查新闻是否存在且属于该用户
        return newsMapper.existsByIdAndUserId(newsId, userId);
    }

    @Override
    public boolean canEditNews(Long userId, Long newsId) {
        if (userId == null || newsId == null) {
            return false;
        }

        String userType = userMapper.getUserType(userId);
        if (!StringUtils.hasText(userType)) {
            return false;
        }

        // 管理员可以编辑所有新闻
        if (NewsConstants.ROLE_ADMIN.equals(userType)) {
            return true;
        }

        // 企业用户只能编辑自己的新闻
        if (NewsConstants.ROLE_ENTERPRISE.equals(userType)) {
            return isOwner(userId, newsId); // 检查是否是所有者
        }

        // 普通用户不能编辑新闻
        return false;
    }

    @Override
    public boolean canDeleteNews(Long userId, Long newsId) {
        if (userId == null || newsId == null) {
            return false;
        }

        String userType = userMapper.getUserType(userId);
        if (!StringUtils.hasText(userType)) {
            return false;
        }

        // 管理员可以删除所有新闻
        if (NewsConstants.ROLE_ADMIN.equals(userType)) {
            return true;
        }

        // 企业用户只能删除自己的新闻
        if (NewsConstants.ROLE_ENTERPRISE.equals(userType)) {
            return isOwner(userId, newsId); // 检查是否是所有者
        }

        // 普通用户不能删除新闻
        return false;
    }

    @Override
    public void checkAdminPermission(Long userId) {
        if (userId == null) {
            throw new UnauthorizedException("用户未登录");
        }

        String userType = userMapper.getUserType(userId);
        if (!NewsConstants.ROLE_ADMIN.equals(userType)) {
            throw new UnauthorizedException("需要管理员权限");
        }
    }

    @Override
    public void checkEnterprisePermission(Long userId) {
        if (userId == null) {
            throw new UnauthorizedException("用户未登录");
        }

        String userType = userMapper.getUserType(userId);
        if (StringUtils.hasText(userType) &&
                (NewsConstants.ROLE_ENTERPRISE.equals(userType) || NewsConstants.ROLE_ADMIN.equals(userType))) {
            return;
        }
        throw new UnauthorizedException("需要企业用户或管理员权限");
    }

    @Override
    public boolean hasPermission(Long userId, String operation, Long resourceId) {
        if (userId == null) {
            return false;
        }

        String userType = userMapper.getUserType(userId);
        if (!StringUtils.hasText(userType)) {
            return false;
        }

        // 管理员拥有所有权限
        if (NewsConstants.ROLE_ADMIN.equals(userType)) {
            return true;
        }

        // 根据具体操作和用户类型判断权限
        switch (operation) {
            case NewsConstants.PERMISSION_EDIT:
            case NewsConstants.PERMISSION_DELETE:
                return canEditNews(userId, resourceId); // 复用canEditNews和canDeleteNews逻辑
            case NewsConstants.PERMISSION_CREATE:
            case NewsConstants.PERMISSION_PUBLISH:
                return NewsConstants.ROLE_ENTERPRISE.equals(userType);
            case NewsConstants.PERMISSION_AUDIT:
                return NewsConstants.ROLE_ADMIN.equals(userType);
            case NewsConstants.PERMISSION_VIEW:
                return true; // 所有用户都可以查看（这里通常还需要考虑新闻状态）
            default:
                return false;
        }
    }

    @Override
    public Long getCurrentUserId() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() &&
                    !"anonymousUser".equals(authentication.getPrincipal())) {

                // 方式1: 如果principal直接是User对象 (假设User对象包含id)
                if (authentication.getPrincipal() instanceof User) {
                    return ((User) authentication.getPrincipal()).getId();
                }

                // 方式2: 如果principal是UserDetails对象 (例如Spring Security的User)
                if (authentication.getPrincipal() instanceof UserDetails) {
                    String username = ((UserDetails) authentication.getPrincipal()).getUsername();
                    User user = userMapper.selectByUsername(username); // 你需要有这个方法
                    return user != null ? user.getId() : null;
                }

                // 方式3: 如果principal是用户名字符串
                if (authentication.getPrincipal() instanceof String) {
                    String username = (String) authentication.getPrincipal();
                    User user = userMapper.selectByUsername(username); // 你需要有这个方法
                    return user != null ? user.getId() : null;
                }
            }
            return null; // 未登录或无法获取到用户ID
        } catch (Exception e) {
            // 记录日志，例如使用 slf4j
            // log.warn("Failed to get current user ID: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 检查用户是否可以发布新闻
     */
    public boolean canPublishNews(Long userId) {
        if (userId == null) {
            return false;
        }
        String userType = userMapper.getUserType(userId);
        return NewsConstants.ROLE_ADMIN.equals(userType) ||
                NewsConstants.ROLE_ENTERPRISE.equals(userType);
    }

    /**
     * 检查用户是否可以审核新闻
     */
    public boolean canAuditNews(Long userId) {
        if (userId == null) {
            return false;
        }
        String userType = userMapper.getUserType(userId);
        return NewsConstants.ROLE_ADMIN.equals(userType);
    }

    /**
     * 获取用户类型
     */
    public String getUserType(Long userId) {
        if (userId == null) {
            return null;
        }
        return userMapper.getUserType(userId);
    }

    // 实现 AuthorizationService 接口新增的方法
    @Override
    public boolean isAdmin(Long userId) {
        if (userId == null) return false;
        String userType = userMapper.getUserType(userId);
        return NewsConstants.ROLE_ADMIN.equals(userType);
    }

    @Override
    public boolean isEnterprise(Long userId) {
        if (userId == null) return false;
        String userType = userMapper.getUserType(userId);
        return NewsConstants.ROLE_ENTERPRISE.equals(userType);
    }
}