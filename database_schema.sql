-- 金仓数据库表结构
-- 用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(20),
    user_type VARCHAR(20),
    status INTEGER,
    created_time TIMESTAMP,
    updated_time TIMESTAMP,
    admin_level VARCHAR(20),
    permissions TEXT,
    business_license VARCHAR(100),
    company_address TEXT,
    company_name VARCHAR(100),
    real_name VARCHAR(50),
    avatar VARCHAR(255),
    bio TEXT,
    contact_person VARCHAR(50),
    contact_phone VARCHAR(20)
);

-- 管理员用户表
CREATE TABLE admin_users (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    admin_level VARCHAR(20) DEFAULT 'ADMIN',
    permissions TEXT,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 企业用户表
CREATE TABLE enterprise_users (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    company_name VARCHAR(100) NOT NULL,
    business_license VARCHAR(100),
    contact_person VARCHAR(50),
    contact_phone VARCHAR(20),
    company_address TEXT,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 普通用户表
CREATE TABLE normal_users (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    real_name VARCHAR(50),
    avatar VARCHAR(255),
    bio TEXT,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 课程表
CREATE TABLE IF NOT EXISTS courses (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255),
    description TEXT,
    price NUMERIC,
    duration INTEGER,
    "level" VARCHAR(50),
    category VARCHAR(50),
    status VARCHAR(20),
    cover_image VARCHAR(255),
    video_url VARCHAR(255),
    created_time TIMESTAMP,
    updated_time TIMESTAMP,
    instructor_id BIGINT,
    favorite_count INTEGER,
    like_count INTEGER
);

-- 课程历史表
CREATE TABLE IF NOT EXISTS course_history (
    course_history_id BIGSERIAL PRIMARY KEY,
    user_id BIGINT,
    course_id BIGINT,
    progress INTEGER,
    status VARCHAR(20),
    start_time TIMESTAMP,
    last_access_time TIMESTAMP,
    completion_time TIMESTAMP,
    viewed_at TIMESTAMP
);

-- 课程问答表
CREATE TABLE IF NOT EXISTS questions (
    id BIGSERIAL PRIMARY KEY,
    course_id BIGINT,
    user_id BIGINT,
    question TEXT,
    ai_answer TEXT,
    created_at TIMESTAMP,
    accept_answer_content TEXT,
    accept_answer_type VARCHAR(20),
    like_count INTEGER,
    manual_answer TEXT,
    report_count INTEGER
);

-- 评论表
CREATE TABLE IF NOT EXISTS comments (
    id BIGSERIAL PRIMARY KEY,
    course_id BIGINT,
    user_id BIGINT,
    content TEXT,
    created_at TIMESTAMP,
    like_count INTEGER,
    status VARCHAR(20)
);

-- 精选课程表
CREATE TABLE IF NOT EXISTS featured_course (
    featured_course_id BIGSERIAL PRIMARY KEY,
    course_id BIGINT,
    promoted_by BIGINT,
    promoted_at TIMESTAMP,
    priority INTEGER
);

-- 审核表
CREATE TABLE IF NOT EXISTS review (
    review_id BIGSERIAL PRIMARY KEY,
    course_id BIGINT,
    reviewer_id BIGINT,
    status VARCHAR(20),
    comment VARCHAR(255),
    reviewed_at TIMESTAMP
);

-- 创建索引
-- 用户表索引
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_user_type ON users(user_type);
CREATE INDEX idx_users_status ON users(status);
CREATE INDEX idx_users_created_time ON users(created_time);

-- 课程表索引
CREATE INDEX idx_courses_instructor ON courses(instructor_id);
CREATE INDEX idx_courses_status ON courses(status);
CREATE INDEX idx_courses_category ON courses(category);
CREATE INDEX idx_courses_level ON courses(level);
CREATE INDEX idx_courses_created_time ON courses(created_time);
CREATE INDEX idx_courses_title ON courses(title);

-- 课程历史表索引
CREATE INDEX idx_course_history_user ON course_history(user_id);
CREATE INDEX idx_course_history_course ON course_history(course_id);
CREATE INDEX idx_course_history_status ON course_history(status);
CREATE INDEX idx_course_history_start_time ON course_history(start_time);

-- 问答表索引
CREATE INDEX idx_questions_course ON questions(course_id);
CREATE INDEX idx_questions_user ON questions(user_id);
CREATE INDEX idx_questions_created_at ON questions(created_at);

-- 评论表索引
CREATE INDEX idx_reviews_course ON review(course_id);
CREATE INDEX idx_reviews_user ON review(reviewer_id);
CREATE INDEX idx_reviews_status ON review(status);
CREATE INDEX idx_reviews_reviewed_at ON review(reviewed_at);

-- 精选课程表索引
CREATE INDEX idx_featured_courses_course ON featured_course(course_id);
CREATE INDEX idx_featured_courses_promoted_by ON featured_course(promoted_by);
CREATE INDEX idx_featured_courses_promoted_at ON featured_course(promoted_at);

-- 新增课程评论表索引
CREATE INDEX IF NOT EXISTS idx_comments_course ON comments(course_id);
CREATE INDEX IF NOT EXISTS idx_comments_user ON comments(user_id);
CREATE INDEX IF NOT EXISTS idx_comments_status ON comments(status);
CREATE INDEX IF NOT EXISTS idx_comments_created_at ON comments(created_at);

-- 插入测试数据
INSERT INTO users (username, password, email, user_type) VALUES 
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'admin@example.com', 'ADMIN'),
('teacher1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'teacher1@example.com', 'ENTERPRISE'),
('student1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'student1@example.com', 'NORMAL');

INSERT INTO admin_users (user_id, admin_level) VALUES (1, 'SUPER_ADMIN');
INSERT INTO enterprise_users (user_id, company_name, contact_person) VALUES (2, '教育科技有限公司', '张老师');
INSERT INTO normal_users (user_id, real_name) VALUES (3, '李同学');

-- 插入示例课程
INSERT INTO courses (title, description, instructor_id, price, level, category, status) VALUES
('Java基础入门', 'Java编程语言基础课程，适合初学者', 2, 99.00, 'BEGINNER', '编程语言', 'PUBLISHED'),
('Spring Boot实战', 'Spring Boot框架开发实战课程', 2, 199.00, 'INTERMEDIATE', '框架开发', 'PUBLISHED'),
('数据库设计原理', '数据库设计理论与实践', 2, 149.00, 'INTERMEDIATE', '数据库', 'PUBLISHED');

-- 插入精选课程
INSERT INTO featured_course (course_id, promoted_by) VALUES
(1, 1),
(2, 2);

-- 插入课程历史记录
INSERT INTO course_history (user_id, course_id, progress, status) VALUES
(3, 1, 0, 'IN_PROGRESS'),
(3, 2, 0, 'IN_PROGRESS');

-- 插入问答记录
INSERT INTO questions (course_id, user_id, question, ai_answer) VALUES
(1, 3, 'Java中的多态是什么？', '多态是面向对象编程的三大特性之一，指同一个接口可以有多种不同的实现方式。'),
(1, 3, '如何理解封装？', '封装是面向对象编程的三大特性之一，指将数据和操作数据的方法封装在一起，形成一个独立的单元。'),
(2, 3, 'Spring Boot的优势有哪些？', 'Spring Boot的优势包括：1. 自动配置 2. 内嵌服务器 3. 起步依赖 4. 生产就绪功能 5. 无需XML配置');

-- 插入评论
INSERT INTO review (course_id, reviewer_id, status, comment) VALUES
(1, 3, 'APPROVED', '非常棒的课程，讲解很详细'),
(2, 3, 'APPROVED', '内容很实用，推荐学习'); 