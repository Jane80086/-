package com.cemenghui.news.service;

public interface AuthorizationService {

    /**
     * 检查是否为新闻所有者
     */
    boolean isOwner(Long userId, Long newsId);

    /**
     * 检查是否可以编辑新闻
     */
    boolean canEditNews(Long userId, Long newsId);

    /**
     * 检查是否可以删除新闻
     */
    boolean canDeleteNews(Long userId, Long newsId);

    /**
     * 检查管理员权限
     */
    void checkAdminPermission(Long userId);

    /**
     * 检查企业用户权限
     */
    void checkEnterprisePermission(Long userId);

    /**
     * 检查权限
     */
    boolean hasPermission(Long userId, String operation, Long resourceId);

    /**
     * 获取当前用户ID
     */
    Long getCurrentUserId();
}
