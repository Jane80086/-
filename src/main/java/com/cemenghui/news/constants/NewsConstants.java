package com.cemenghui.news.constants;

public class NewsConstants {

    // 分页常量
    public static final Integer DEFAULT_PAGE_SIZE = 10;
    public static final Integer MAX_PAGE_SIZE = 100;
    public static final Integer DEFAULT_PAGE_NUM = 1;

    // 新闻相关常量
    public static final Integer MAX_TITLE_LENGTH = 200;
    public static final Integer MAX_SUMMARY_LENGTH = 500;
    public static final Integer MAX_AUTHOR_LENGTH = 100;

    // 文件上传常量
    public static final Long MAX_IMAGE_SIZE = 5 * 1024 * 1024L; // 5MB
    public static final String[] ALLOWED_IMAGE_TYPES = {"jpg", "jpeg", "png", "gif", "webp"};
    public static final String UPLOAD_PATH = "/uploads/images/";

    // 默认值
    public static final Integer DEFAULT_VIEW_COUNT = 0;
    public static final Integer DEFAULT_STATUS = 0;
    public static final Integer NOT_DELETED = 0;
    public static final Integer DELETED = 1;

    // 权限相关
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_ENTERPRISE = "ENTERPRISE";
    public static final String ROLE_NORMAL = "NORMAL";
}
