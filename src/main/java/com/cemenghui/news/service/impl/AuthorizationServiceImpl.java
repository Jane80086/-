package com.cemenghui.news.service.impl;

import com.cemenghui.common.User;
import com.cemenghui.news.constants.NewsConstants;
import com.cemenghui.news.exception.UnauthorizedException;
import com.cemenghui.news.mapper.NewsMapper;
import com.cemenghui.course.dao.UserDao;
import com.cemenghui.news.service.AuthorizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthorizationServiceImpl implements AuthorizationService {

    private final UserDao userMapper;
    private final NewsMapper newsMapper;

    @Override
    public boolean isOwner(Long userId, Long newsId) {
        if (userId == null || newsId == null) {
            return false;
        }
        return newsMapper.existsByIdAndUserId(newsId, userId);
    }

    @Override
    public boolean canEditNews(Long userId, Long newsId) {
        if (userId == null || newsId == null) {
            return false;
        }

        // 管理员可以编辑所有新闻
        User user = userMapper.selectById(userId);
        if (user != null && NewsConstants.ROLE_ADMIN.equals(user.getUserType())) {
            return true;
        }

        // 新闻所有者可以编辑自己的新闻
        return isOwner(userId, newsId);
    }

    @Override
    public boolean canDeleteNews(Long userId, Long newsId) {
        if (userId == null || newsId == null) {
            return false;
        }

        // 管理员可以删除所有新闻
        User user = userMapper.selectById(userId);
        if (user != null && NewsConstants.ROLE_ADMIN.equals(user.getUserType())) {
            return true;
        }

        // 新闻所有者可以删除自己的新闻
        return isOwner(userId, newsId);
    }

    @Override
    public void checkAdminPermission(Long userId) {
        if (userId == null) {
            throw new UnauthorizedException("用户未登录");
        }

        User user = userMapper.selectById(userId);
        if (user == null || !NewsConstants.ROLE_ADMIN.equals(user.getUserType())) {
            throw new UnauthorizedException("需要管理员权限");
        }
    }

    @Override
    public void checkEnterprisePermission(Long userId) {
        if (userId == null) {
            throw new UnauthorizedException("用户未登录");
        }

        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new UnauthorizedException("用户不存在");
        }

        if (!NewsConstants.ROLE_ENTERPRISE.equals(user.getUserType()) &&
                !NewsConstants.ROLE_ADMIN.equals(user.getUserType())) {
            throw new UnauthorizedException("需要企业用户或管理员权限");
        }
    }

    @Override
    public boolean hasPermission(Long userId, String operation, Long resourceId) {
        if (userId == null) {
            return false;
        }

        User user = userMapper.selectById(userId);
        if (user == null) {
            return false;
        }

        // 管理员拥有所有权限
        if (NewsConstants.ROLE_ADMIN.equals(user.getUserType())) {
            return true;
        }

        // 根据具体操作和资源判断权限
        switch (operation) {
            case "EDIT":
            case "DELETE":
                return canEditNews(userId, resourceId);
            case "PUBLISH":
                return NewsConstants.ROLE_ENTERPRISE.equals(user.getUserType());
            default:
                return false;
        }
    }

    @Override
    public Long getCurrentUserId() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                // 这里需要根据实际的认证实现来获取用户ID
                // 假设principal中存储的是用户名，需要查询获取用户ID
                String username = authentication.getName();
                User user = userMapper.selectOne(
                        new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<User>()
                                .eq("username", username)
                );
                return user != null ? user.getId() : null;
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}