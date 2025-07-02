package com.cemenghui.news.service;

public interface AuthorizationService {

    boolean isOwner(Long userId, Long newsId);

    boolean canEditNews(Long userId, Long newsId);

    boolean canDeleteNews(Long userId, Long newsId);

    void checkAdminPermission(Long userId);

    void checkEnterprisePermission(Long userId);

    boolean hasPermission(Long userId, String operation, Long resourceId);

    Long getCurrentUserId();

    boolean canPublishNews(Long userId);

    boolean canAuditNews(Long userId);

    String getUserType(Long userId);

    // 新增这两个方法到接口中
    boolean isAdmin(Long userId);
    boolean isEnterprise(Long userId);
}
