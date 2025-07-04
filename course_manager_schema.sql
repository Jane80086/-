-- 课程模块（course）
CREATE TABLE courses (
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

-- 会议模块（meeting）
CREATE TABLE meeting_info (
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

CREATE TABLE meeting_review_record (
    id SERIAL PRIMARY KEY,
    meeting_id INTEGER NOT NULL,
    meeting_name VARCHAR(255),
    creator VARCHAR(100),
    reviewer VARCHAR(100),
    status INTEGER,
    review_comment TEXT,
    review_time TIMESTAMP
);

CREATE TABLE user_info (
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

-- 新闻模块（news）
CREATE TABLE news (
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

-- 系统模块（system）
CREATE TABLE system_user (
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

-- 课程审核历史表
CREATE TABLE course_review_history (
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

-- 表结构修正补丁
ALTER TABLE comments ADD COLUMN IF NOT EXISTS like_count INTEGER DEFAULT 0;
ALTER TABLE comments ADD COLUMN IF NOT EXISTS status VARCHAR(20) DEFAULT 'NORMAL';

ALTER TABLE course_history ADD COLUMN IF NOT EXISTS deleted INTEGER DEFAULT 0;
-- 如需主键名统一，可重命名（可选）：
-- ALTER TABLE course_history RENAME COLUMN id TO course_history_id;

ALTER TABLE course_review_history ADD COLUMN IF NOT EXISTS reason VARCHAR(255); 