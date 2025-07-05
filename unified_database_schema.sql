-- 统一数据库架构脚本
-- 数据库名称: course_manager
-- 包含所有子系统的表结构

-- ========================================
-- 课程模块（course）
-- ========================================
CREATE TABLE IF NOT EXISTS courses (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    instructor_id BIGINT,
    price DECIMAL(10,2) DEFAULT 0,
    duration INTEGER DEFAULT 0,
    course_level VARCHAR(50) DEFAULT 'BEGINNER',
    category VARCHAR(100),
    status VARCHAR(50) DEFAULT 'DRAFT',
    cover_image VARCHAR(500),
    video_url VARCHAR(500),
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    like_count INTEGER DEFAULT 0,
    favorite_count INTEGER DEFAULT 0
);

-- 课程审核历史表
CREATE TABLE IF NOT EXISTS course_review_history (
    id SERIAL PRIMARY KEY,
    course_id BIGINT NOT NULL,
    admin_id BIGINT NOT NULL,
    action VARCHAR(50),
    reason VARCHAR(255),
    review_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 评论表
CREATE TABLE IF NOT EXISTS comments (
    id SERIAL PRIMARY KEY,
    course_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    content TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    like_count INTEGER DEFAULT 0,
    status VARCHAR(20) DEFAULT 'NORMAL'
);

-- 课程历史表
CREATE TABLE IF NOT EXISTS course_history (
    course_history_id SERIAL PRIMARY KEY,
    course_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    viewed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0
);

-- 课程审核表
CREATE TABLE IF NOT EXISTS review (
    review_id SERIAL PRIMARY KEY,
    course_id BIGINT NOT NULL,
    reviewer_id BIGINT NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING',
    comment TEXT,
    reviewed_at TIMESTAMP,
    deleted INTEGER DEFAULT 0
);

-- 课程问答表
CREATE TABLE IF NOT EXISTS questions (
    id SERIAL PRIMARY KEY,
    course_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    question TEXT,
    ai_answer TEXT,
    manual_answer TEXT,
    like_count INTEGER DEFAULT 0,
    accept_answer_type VARCHAR(20),
    accept_answer_content TEXT,
    report_count INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0
);

-- 精品课程表
CREATE TABLE IF NOT EXISTS featured_course (
    featured_course_id SERIAL PRIMARY KEY,
    course_id BIGINT NOT NULL,
    promoted_by BIGINT,
    promoted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    priority INTEGER DEFAULT 0,
    deleted INTEGER DEFAULT 0
);

-- ========================================
-- 会议模块（meeting）
-- ========================================
CREATE TABLE IF NOT EXISTS meeting_info (
    id SERIAL PRIMARY KEY,
    meeting_name VARCHAR(255) NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP,
    creator VARCHAR(100),
    meeting_content TEXT,
    status INTEGER,
    reviewer VARCHAR(100),
    review_time TIMESTAMP,
    review_comment TEXT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    image_url VARCHAR(500),
    deleted INTEGER DEFAULT 0
);

CREATE TABLE IF NOT EXISTS meeting_review_record (
    id SERIAL PRIMARY KEY,
    meeting_id INTEGER NOT NULL,
    meeting_name VARCHAR(255),
    creator VARCHAR(100),
    reviewer VARCHAR(100),
    status INTEGER,
    review_comment TEXT,
    review_time TIMESTAMP
);

-- ========================================
-- 用户模块（user）
-- ========================================
CREATE TABLE IF NOT EXISTS user_info (
    id SERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    real_name VARCHAR(100),
    email VARCHAR(255),
    phone VARCHAR(20),
    user_type VARCHAR(20),
    status INTEGER,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0
);

-- ========================================
-- 新闻模块（news）
-- ========================================
CREATE TABLE IF NOT EXISTS news (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    image VARCHAR(500),
    content TEXT,
    summary VARCHAR(500),
    author VARCHAR(100),
    user_id BIGINT,
    status INTEGER DEFAULT 0,
    view_count INTEGER DEFAULT 0,
    is_deleted INTEGER DEFAULT 0,
    audit_comment TEXT,
    audit_user_id BIGINT,
    audit_time TIMESTAMP,
    deleted_time TIMESTAMP,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ========================================
-- 系统模块（system）
-- ========================================
CREATE TABLE IF NOT EXISTS system_user (
    user_id VARCHAR(64) PRIMARY KEY,
    account VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50),
    is_remembered BOOLEAN,
    nickname VARCHAR(100),
    phone VARCHAR(20),
    email VARCHAR(255),
    real_name VARCHAR(100),
    enterprise_id VARCHAR(64),
    department VARCHAR(100),
    dynamic_code VARCHAR(20)
);

-- ========================================
-- 索引创建
-- ========================================

-- 课程相关索引
CREATE INDEX IF NOT EXISTS idx_courses_instructor_id ON courses(instructor_id);
CREATE INDEX IF NOT EXISTS idx_courses_status ON courses(status);
CREATE INDEX IF NOT EXISTS idx_courses_category ON courses(category);
CREATE INDEX IF NOT EXISTS idx_course_review_history_course_id ON course_review_history(course_id);
CREATE INDEX IF NOT EXISTS idx_comments_course_id ON comments(course_id);
CREATE INDEX IF NOT EXISTS idx_comments_user_id ON comments(user_id);
CREATE INDEX IF NOT EXISTS idx_course_history_course_id ON course_history(course_id);
CREATE INDEX IF NOT EXISTS idx_course_history_user_id ON course_history(user_id);
CREATE INDEX IF NOT EXISTS idx_review_course_id ON review(course_id);
CREATE INDEX IF NOT EXISTS idx_questions_course_id ON questions(course_id);
CREATE INDEX IF NOT EXISTS idx_questions_user_id ON questions(user_id);
CREATE INDEX IF NOT EXISTS idx_featured_course_course_id ON featured_course(course_id);

-- 会议相关索引
CREATE INDEX IF NOT EXISTS idx_meeting_info_creator ON meeting_info(creator);
CREATE INDEX IF NOT EXISTS idx_meeting_info_status ON meeting_info(status);
CREATE INDEX IF NOT EXISTS idx_meeting_info_start_time ON meeting_info(start_time);
CREATE INDEX IF NOT EXISTS idx_meeting_review_record_meeting_id ON meeting_review_record(meeting_id);

-- 用户相关索引
CREATE INDEX IF NOT EXISTS idx_user_info_username ON user_info(username);
CREATE INDEX IF NOT EXISTS idx_user_info_user_type ON user_info(user_type);
CREATE INDEX IF NOT EXISTS idx_user_info_email ON user_info(email);

-- 新闻相关索引
CREATE INDEX IF NOT EXISTS idx_news_user_id ON news(user_id);
CREATE INDEX IF NOT EXISTS idx_news_status ON news(status);
CREATE INDEX IF NOT EXISTS idx_news_create_time ON news(create_time);

-- 系统用户相关索引
CREATE INDEX IF NOT EXISTS idx_system_user_account ON system_user(account);
CREATE INDEX IF NOT EXISTS idx_system_user_role ON system_user(role);
CREATE INDEX IF NOT EXISTS idx_system_user_enterprise_id ON system_user(enterprise_id);

-- ========================================
-- 数据迁移说明
-- ========================================
-- 1. 备份现有数据库
-- 2. 创建新的统一数据库 course_manager
-- 3. 执行此脚本创建表结构
-- 4. 迁移各子系统数据到统一数据库
-- 5. 更新各子系统的数据源配置
-- 6. 测试各子系统功能

-- ========================================
-- 数据迁移脚本示例
-- ========================================
/*
-- 从 course_management 迁移课程数据
INSERT INTO course_manager.courses 
SELECT * FROM course_management.courses;

-- 从 meeting_management 迁移会议数据  
INSERT INTO course_manager.meeting_info
SELECT * FROM meeting_management.meeting_info;

-- 从 news_management 迁移新闻数据
INSERT INTO course_manager.news
SELECT * FROM news_management.news;

-- 从 system_management 迁移系统用户数据
INSERT INTO course_manager.system_user
SELECT * FROM system_management.system_user;
*/ 