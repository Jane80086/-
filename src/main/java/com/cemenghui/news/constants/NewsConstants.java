package com.cemenghui.news.constants;

/**
 * 新闻模块常量类
 */
public class NewsConstants {

    // 用户类型常量
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_ENTERPRISE = "ENTERPRISE";
    public static final String ROLE_NORMAL = "NORMAL";

    // 新闻状态常量
    public static final Integer NEWS_STATUS_DRAFT = 0;      // 草稿
    public static final Integer NEWS_STATUS_PENDING = 1;    // 待审核
    public static final Integer NEWS_STATUS_APPROVED = 2;   // 已审核通过
    public static final Integer NEWS_STATUS_REJECTED = 3;   // 审核不通过
    public static final Integer NEWS_STATUS_PUBLISHED = 4;  // 已发布

    // 操作权限常量
    public static final String PERMISSION_CREATE = "CREATE";
    public static final String PERMISSION_EDIT = "EDIT";
    public static final String PERMISSION_DELETE = "DELETE";
    public static final String PERMISSION_PUBLISH = "PUBLISH";
    public static final String PERMISSION_AUDIT = "AUDIT";
    public static final String PERMISSION_VIEW = "VIEW";

    // 默认分页参数
    public static final Integer DEFAULT_PAGE_SIZE = 10;
    public static final Integer DEFAULT_PAGE_NUM = 1;

    // 私有构造函数，防止实例化
    private NewsConstants() {}
}