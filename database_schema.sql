-- 金仓数据库表结构
-- 用户表
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(20),
    user_type VARCHAR(20) NOT NULL DEFAULT 'NORMAL',
    status INTEGER DEFAULT 1,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
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
CREATE TABLE courses (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    instructor_id BIGINT REFERENCES users(id),
    price DECIMAL(10,2) DEFAULT 0.00,
    duration INTEGER DEFAULT 0,
    level VARCHAR(20) DEFAULT 'BEGINNER',
    category VARCHAR(50),
    status VARCHAR(20) DEFAULT 'DRAFT',
    cover_image VARCHAR(255),
    video_url VARCHAR(255),
    like_count INTEGER DEFAULT 0,
    favorite_count INTEGER DEFAULT 0,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 课程历史表
CREATE TABLE course_history (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    course_id BIGINT REFERENCES courses(id) ON DELETE CASCADE,
    progress INTEGER DEFAULT 0,
    status VARCHAR(20) DEFAULT 'IN_PROGRESS',
    start_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_access_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completion_time TIMESTAMP,
    UNIQUE(user_id, course_id)
);

-- 课程问答表
CREATE TABLE questions (
    id BIGSERIAL PRIMARY KEY,
    course_id BIGINT REFERENCES courses(id) ON DELETE CASCADE,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    question TEXT NOT NULL,
    ai_answer TEXT,
    manual_answer TEXT,
    like_count INTEGER DEFAULT 0,
    accept_answer_type VARCHAR(20),
    accept_answer_content TEXT,
    report_count INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 评论表
CREATE TABLE reviews (
    id BIGSERIAL PRIMARY KEY,
    course_id BIGINT REFERENCES courses(id) ON DELETE CASCADE,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    rating INTEGER CHECK (rating >= 1 AND rating <= 5),
    content TEXT,
    status VARCHAR(20) DEFAULT 'PENDING',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 精选课程表
CREATE TABLE featured_courses (
    id BIGSERIAL PRIMARY KEY,
    course_id BIGINT REFERENCES courses(id) ON DELETE CASCADE,
    featured_order INTEGER DEFAULT 0,
    start_date DATE,
    end_date DATE,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 新增课程评论表
CREATE TABLE IF NOT EXISTS comments (
    id BIGSERIAL PRIMARY KEY,
    course_id BIGINT REFERENCES courses(id) ON DELETE CASCADE,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    content TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    like_count INTEGER DEFAULT 0,
    status VARCHAR(20) DEFAULT 'NORMAL'
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
CREATE INDEX idx_reviews_course ON reviews(course_id);
CREATE INDEX idx_reviews_user ON reviews(user_id);
CREATE INDEX idx_reviews_rating ON reviews(rating);
CREATE INDEX idx_reviews_status ON reviews(status);
CREATE INDEX idx_reviews_created_time ON reviews(created_time);

-- 精选课程表索引
CREATE INDEX idx_featured_courses_order ON featured_courses(featured_order);
CREATE INDEX idx_featured_courses_start_date ON featured_courses(start_date);
CREATE INDEX idx_featured_courses_end_date ON featured_courses(end_date);

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
INSERT INTO featured_courses (course_id, featured_order) VALUES
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
INSERT INTO reviews (course_id, user_id, rating, content, status) VALUES
(1, 3, 5, '非常棒的课程，讲解很详细', 'APPROVED'),
(2, 3, 4, '内容很实用，推荐学习', 'APPROVED');

-- 会议管理子系统数据库初始化脚本
-- 包含用户表和会议表的完整测试数据

-- ========================================
-- 1. 创建用户信息表
-- ========================================
CREATE TABLE IF NOT EXISTS user_info (
                                         id BIGSERIAL PRIMARY KEY,
                                         username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    real_name VARCHAR(100),
    email VARCHAR(200),
    phone VARCHAR(20),
    user_type VARCHAR(20) NOT NULL, -- ADMIN, ENTERPRISE, NORMAL
    status INTEGER DEFAULT 1, -- 0: 禁用, 1: 启用
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_user_username ON user_info(username);
CREATE INDEX IF NOT EXISTS idx_user_type ON user_info(user_type);
CREATE INDEX IF NOT EXISTS idx_user_status ON user_info(status);

-- ========================================
-- 2. 创建会议信息表
-- ========================================
CREATE TABLE IF NOT EXISTS meeting_info (
                                            id BIGSERIAL PRIMARY KEY,
                                            meeting_name VARCHAR(200) NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    creator VARCHAR(100) NOT NULL,
    meeting_content VARCHAR(2000),
    image_url VARCHAR(500),
    status INTEGER DEFAULT 0, -- 0: 待审核, 1: 已通过, 2: 已拒绝, 3: 已删除
    reviewer VARCHAR(100),
    review_time TIMESTAMP,
    review_comment VARCHAR(500),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_meeting_creator ON meeting_info(creator);
CREATE INDEX IF NOT EXISTS idx_meeting_status ON meeting_info(status);
CREATE INDEX IF NOT EXISTS idx_meeting_start_time ON meeting_info(start_time);
CREATE INDEX IF NOT EXISTS idx_meeting_create_time ON meeting_info(create_time);

-- ========================================
-- 2.1 创建会议审核记录表
-- ========================================
CREATE TABLE IF NOT EXISTS meeting_review_record (
                                                     id BIGSERIAL PRIMARY KEY,
                                                     meeting_id BIGINT NOT NULL,
                                                     meeting_name VARCHAR(200) NOT NULL,
    creator VARCHAR(100) NOT NULL,
    reviewer VARCHAR(100) NOT NULL,
    status INTEGER NOT NULL, -- 1: 通过, 2: 拒绝
    review_comment VARCHAR(500),
    review_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

CREATE INDEX IF NOT EXISTS idx_review_meeting_id ON meeting_review_record(meeting_id);
CREATE INDEX IF NOT EXISTS idx_review_reviewer ON meeting_review_record(reviewer);
CREATE INDEX IF NOT EXISTS idx_review_creator ON meeting_review_record(creator);

-- ========================================
-- 3. 插入用户测试数据
-- ========================================

-- 管理员用户 (密码都是123456的MD5值: e10adc3949ba59abbe56e057f20f883e)
INSERT INTO user_info (username, password, real_name, email, phone, user_type, status) VALUES
                                                                                           ('admin', 'e10adc3949ba59abbe56e057f20f883e', '系统管理员', 'admin@company.com', '13800138001', 'ADMIN', 1),
                                                                                           ('admin001', 'e10adc3949ba59abbe56e057f20f883e', '张管理员', 'admin001@company.com', '13800138002', 'ADMIN', 1),
                                                                                           ('admin002', 'e10adc3949ba59abbe56e057f20f883e', '李管理员', 'admin002@company.com', '13800138003', 'ADMIN', 1),
                                                                                           ('admin003', 'e10adc3949ba59abbe56e057f20f883e', '王管理员', 'admin003@company.com', '13800138004', 'ADMIN', 1),
                                                                                           ('admin004', 'e10adc3949ba59abbe56e057f20f883e', '赵管理员', 'admin004@company.com', '13800138005', 'ADMIN', 1);

-- 企业用户
INSERT INTO user_info (username, password, real_name, email, phone, user_type, status) VALUES
                                                                                           ('enterprise1', 'e10adc3949ba59abbe56e057f20f883e', '腾讯科技', 'contact@tencent.com', '13800138006', 'ENTERPRISE', 1),
                                                                                           ('enterprise2', 'e10adc3949ba59abbe56e057f20f883e', '阿里巴巴', 'contact@alibaba.com', '13800138007', 'ENTERPRISE', 1),
                                                                                           ('enterprise3', 'e10adc3949ba59abbe56e057f20f883e', '百度公司', 'contact@baidu.com', '13800138008', 'ENTERPRISE', 1),
                                                                                           ('enterprise4', 'e10adc3949ba59abbe56e057f20f883e', '字节跳动', 'contact@bytedance.com', '13800138009', 'ENTERPRISE', 1),
                                                                                           ('enterprise5', 'e10adc3949ba59abbe56e057f20f883e', '美团点评', 'contact@meituan.com', '13800138010', 'ENTERPRISE', 1),
                                                                                           ('enterprise6', 'e10adc3949ba59abbe56e057f20f883e', '京东集团', 'contact@jd.com', '13800138011', 'ENTERPRISE', 1),
                                                                                           ('enterprise7', 'e10adc3949ba59abbe56e057f20f883e', '网易公司', 'contact@netease.com', '13800138012', 'ENTERPRISE', 1),
                                                                                           ('enterprise8', 'e10adc3949ba59abbe56e057f20f883e', '小米科技', 'contact@mi.com', '13800138013', 'ENTERPRISE', 1),
                                                                                           ('enterprise9', 'e10adc3949ba59abbe56e057f20f883e', '华为技术', 'contact@huawei.com', '13800138014', 'ENTERPRISE', 1),
                                                                                           ('enterprise10', 'e10adc3949ba59abbe56e057f20f883e', '联想集团', 'contact@lenovo.com', '13800138015', 'ENTERPRISE', 1);

-- 普通用户
INSERT INTO user_info (username, password, real_name, email, phone, user_type, status) VALUES
                                                                                           ('user1', 'e10adc3949ba59abbe56e057f20f883e', '张三', 'zhangsan@email.com', '13800138016', 'NORMAL', 1),
                                                                                           ('user2', 'e10adc3949ba59abbe56e057f20f883e', '李四', 'lisi@email.com', '13800138017', 'NORMAL', 1),
                                                                                           ('user3', 'e10adc3949ba59abbe56e057f20f883e', '王五', 'wangwu@email.com', '13800138018', 'NORMAL', 1),
                                                                                           ('user4', 'e10adc3949ba59abbe56e057f20f883e', '赵六', 'zhaoliu@email.com', '13800138019', 'NORMAL', 1),
                                                                                           ('user5', 'e10adc3949ba59abbe56e057f20f883e', '钱七', 'qianqi@email.com', '13800138020', 'NORMAL', 1),
                                                                                           ('user6', 'e10adc3949ba59abbe56e057f20f883e', '孙八', 'sunba@email.com', '13800138021', 'NORMAL', 1),
                                                                                           ('user7', 'e10adc3949ba59abbe56e057f20f883e', '周九', 'zhoujiu@email.com', '13800138022', 'NORMAL', 1),
                                                                                           ('user8', 'e10adc3949ba59abbe56e057f20f883e', '吴十', 'wushi@email.com', '13800138023', 'NORMAL', 1),
                                                                                           ('user9', 'e10adc3949ba59abbe56e057f20f883e', '郑十一', 'zhengshiyi@email.com', '13800138024', 'NORMAL', 1),
                                                                                           ('user10', 'e10adc3949ba59abbe56e057f20f883e', '王十二', 'wangshier@email.com', '13800138025', 'NORMAL', 1);

-- 禁用用户（用于测试）
INSERT INTO user_info (username, password, real_name, email, phone, user_type, status) VALUES
    ('disabled_user', 'e10adc3949ba59abbe56e057f20f883e', '禁用用户', 'disabled@email.com', '13800138026', 'NORMAL', 0);

-- ========================================
-- 4. 插入会议测试数据
-- ========================================

-- 管理员创建的会议（直接通过）
INSERT INTO meeting_info (meeting_name, start_time, end_time, creator, meeting_content, image_url, status, create_time) VALUES
                                                                                                                            ('2024年第一季度工作总结会议', '2024-01-15 09:00:00', '2024-01-15 11:00:00', 'admin', '回顾第一季度工作成果，分析存在的问题，制定第二季度工作计划。', 'https://example.com/cover1.jpg', 1, '2024-01-10 10:00:00'),
                                                                                                                            ('技术架构评审会议', '2024-01-20 14:00:00', '2024-01-20 16:00:00', 'admin', '评审新系统的技术架构设计，确保系统的可扩展性和稳定性。', 'https://example.com/cover2.jpg', 1, '2024-01-12 14:30:00'),
                                                                                                                            ('年度战略规划会议', '2024-02-01 09:00:00', '2024-02-01 17:00:00', 'admin001', '制定公司年度发展战略，确定重点业务方向和资源配置。', 'https://example.com/cover3.jpg', 1, '2024-01-25 09:00:00'),
                                                                                                                            ('产品发布会', '2024-02-15 10:00:00', '2024-02-15 12:00:00', 'admin002', '发布新产品，介绍产品特性和市场定位。', 'https://example.com/cover4.jpg', 1, '2024-02-01 15:00:00'),
                                                                                                                            ('团队建设活动', '2024-02-28 13:00:00', '2024-02-28 18:00:00', 'admin003', '组织团队建设活动，增强团队凝聚力和协作能力。', 'https://example.com/cover5.jpg', 1, '2024-02-15 11:00:00');

-- 企业用户创建的会议（已通过审核）
INSERT INTO meeting_info (meeting_name, start_time, end_time, creator, meeting_content, image_url, status, reviewer, review_time, review_comment, create_time) VALUES
                                                                                                                                                                   ('腾讯云技术分享会', '2024-01-18 14:00:00', '2024-01-18 16:00:00', 'enterprise1', '分享腾讯云最新技术成果和应用案例，探讨云计算发展趋势。', 'https://example.com/cover6.jpg', 1, 'admin', '2024-01-16 10:00:00', '内容符合要求，审核通过', '2024-01-15 16:00:00'),
                                                                                                                                                                   ('阿里云解决方案研讨会', '2024-01-25 09:00:00', '2024-01-25 11:00:00', 'enterprise2', '探讨阿里云在企业数字化转型中的解决方案和应用实践。', 'https://example.com/cover7.jpg', 1, 'admin001', '2024-01-23 14:30:00', '会议内容有价值，同意举办', '2024-01-22 09:00:00'),
                                                                                                                                                                   ('百度AI技术交流会', '2024-02-05 15:00:00', '2024-02-05 17:00:00', 'enterprise3', '交流百度在人工智能领域的最新研究成果和技术应用。', 'https://example.com/cover8.jpg', 1, 'admin002', '2024-02-03 11:00:00', '技术交流很有意义', '2024-02-02 10:00:00'),
                                                                                                                                                                   ('字节跳动产品创新论坛', '2024-02-12 10:00:00', '2024-02-12 12:00:00', 'enterprise4', '探讨产品创新理念和方法，分享成功案例和经验教训。', 'https://example.com/cover9.jpg', 1, 'admin003', '2024-02-10 16:00:00', '创新主题很好', '2024-02-09 14:00:00'),
                                                                                                                                                                   ('美团本地生活服务峰会', '2024-02-20 13:00:00', '2024-02-20 15:00:00', 'enterprise5', '探讨本地生活服务行业发展趋势和商业模式创新。', 'https://example.com/cover10.jpg', 1, 'admin004', '2024-02-18 09:30:00', '行业峰会很有价值', '2024-02-17 15:30:00');

-- 企业用户创建的会议（待审核）
INSERT INTO meeting_info (meeting_name, start_time, end_time, creator, meeting_content, image_url, status, create_time) VALUES
                                                                                                                            ('京东电商技术峰会', '2024-03-01 09:00:00', '2024-03-01 11:00:00', 'enterprise6', '探讨电商技术发展趋势，分享京东在电商领域的技术创新。', 'https://example.com/cover11.jpg', 0, '2024-02-25 10:00:00'),
                                                                                                                            ('网易游戏开发者大会', '2024-03-08 14:00:00', '2024-03-08 16:00:00', 'enterprise7', '游戏开发者技术交流，分享游戏开发经验和最新技术。', 'https://example.com/cover12.jpg', 0, '2024-02-28 16:00:00'),
                                                                                                                            ('小米IoT生态大会', '2024-03-15 10:00:00', '2024-03-15 12:00:00', 'enterprise8', '探讨IoT生态建设，展示小米在智能家居领域的最新成果。', 'https://example.com/cover13.jpg', 0, '2024-03-05 11:00:00'),
                                                                                                                            ('华为5G技术研讨会', '2024-03-22 15:00:00', '2024-03-22 17:00:00', 'enterprise9', '探讨5G技术发展趋势，分享华为在5G领域的技术优势。', 'https://example.com/cover14.jpg', 0, '2024-03-12 14:00:00'),
                                                                                                                            ('联想数字化转型论坛', '2024-03-29 13:00:00', '2024-03-29 15:00:00', 'enterprise10', '探讨企业数字化转型策略，分享联想在数字化转型中的实践经验。', 'https://example.com/cover15.jpg', 0, '2024-03-19 09:00:00');

-- 企业用户创建的会议（已拒绝）
INSERT INTO meeting_info (meeting_name, start_time, end_time, creator, meeting_content, image_url, status, reviewer, review_time, review_comment, create_time) VALUES
                                                                                                                                                                   ('被拒绝的会议1', '2024-01-30 09:00:00', '2024-01-30 11:00:00', 'enterprise1', '这个会议内容不符合要求，应该被拒绝。', 'https://example.com/cover16.jpg', 2, 'admin', '2024-01-28 10:00:00', '会议内容不符合平台规范，拒绝通过', '2024-01-27 14:00:00'),
                                                                                                                                                                   ('被拒绝的会议2', '2024-02-10 14:00:00', '2024-02-10 16:00:00', 'enterprise2', '另一个不符合要求的会议。', 'https://example.com/cover17.jpg', 2, 'admin001', '2024-02-08 15:30:00', '时间安排不合理，建议重新规划', '2024-02-07 16:00:00');

-- 已删除的会议
INSERT INTO meeting_info (meeting_name, start_time, end_time, creator, meeting_content, image_url, status, create_time) VALUES
    ('已删除的会议', '2024-01-10 09:00:00', '2024-01-10 11:00:00', 'enterprise3', '这个会议已经被删除。', 'https://example.com/cover18.jpg', 3, '2024-01-05 10:00:00');

-- ========================================
-- 5. 插入审核记录测试数据
-- ========================================

-- 已通过会议的审核记录
INSERT INTO meeting_review_record (meeting_id, meeting_name, creator, reviewer, status, review_comment, review_time) VALUES
                                                                                                                         (6, '腾讯云技术分享会', 'enterprise1', 'admin', 1, '内容符合要求，审核通过', '2024-01-16 10:00:00'),
                                                                                                                         (7, '阿里云解决方案研讨会', 'enterprise2', 'admin001', 1, '会议内容有价值，同意举办', '2024-01-23 14:30:00'),
                                                                                                                         (8, '百度AI技术交流会', 'enterprise3', 'admin002', 1, '技术交流很有意义', '2024-02-03 11:00:00'),
                                                                                                                         (9, '字节跳动产品创新论坛', 'enterprise4', 'admin003', 1, '创新主题很好', '2024-02-10 16:00:00'),
                                                                                                                         (10, '美团本地生活服务峰会', 'enterprise5', 'admin004', 1, '行业峰会很有价值', '2024-02-18 09:30:00');

-- 已拒绝会议的审核记录
INSERT INTO meeting_review_record (meeting_id, meeting_name, creator, reviewer, status, review_comment, review_time) VALUES
                                                                                                                         (16, '被拒绝的会议1', 'enterprise1', 'admin', 2, '会议内容不符合平台规范，拒绝通过', '2024-01-28 10:00:00'),
                                                                                                                         (17, '被拒绝的会议2', 'enterprise2', 'admin001', 2, '时间安排不合理，建议重新规划', '2024-02-08 15:30:00');

-- ========================================
-- 6. 数据统计
-- ========================================
-- 用户统计
-- 管理员: 5个
-- 企业用户: 10个
-- 普通用户: 10个
-- 禁用用户: 1个
-- 总计: 26个用户

-- 会议统计
-- 已通过: 10个
-- 待审核: 5个
-- 已拒绝: 2个
-- 已删除: 1个
-- 总计: 18个会议

-- 创建news表
CREATE TABLE news (
                      id BIGSERIAL PRIMARY KEY,
                      title VARCHAR(200) NOT NULL COMMENT '动态标题',
                      image TEXT COMMENT '新闻图片路径（支持多图片，JSON格式存储）',
                      content LONGTEXT NOT NULL COMMENT '动态内容（富文本HTML）',
                      summary TEXT COMMENT '新闻简介',
                      author VARCHAR(100) NOT NULL COMMENT '作者',
                      user_id BIGINT NOT NULL COMMENT '发布者用户ID',
                      status TINYINT DEFAULT 0 COMMENT '审核状态（0:待审核，1:已通过，2:已拒绝，3:草稿）',
                      view_count INT DEFAULT 0 COMMENT '浏览次数',
                      is_deleted TINYINT DEFAULT 0 COMMENT '软删除标记（0:未删除，1:已删除）',
                      audit_comment TEXT COMMENT '审核意见',
                      audit_user_id BIGINT COMMENT '审核人ID',
                      audit_time DATETIME COMMENT '审核时间',
                      deleted_time DATETIME COMMENT '删除时间',
                      create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                      update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
);

-- 添加外键约束
ALTER TABLE news ADD CONSTRAINT fk_news_user_id
    FOREIGN KEY (user_id) REFERENCES users(id);

ALTER TABLE news ADD CONSTRAINT fk_news_audit_user_id
    FOREIGN KEY (audit_user_id) REFERENCES users(id);

-- 创建索引
CREATE INDEX idx_news_user_id ON news(user_id);
CREATE INDEX idx_news_status ON news(status);
CREATE INDEX idx_news_create_time ON news(create_time);
CREATE INDEX idx_news_title ON news(title);
CREATE INDEX idx_news_author ON news(author);
CREATE INDEX idx_news_is_deleted ON news(is_deleted);

-- 创建user_view_log表
CREATE TABLE user_view_log (
                               id BIGSERIAL PRIMARY KEY COMMENT '记录主键ID',
                               user_id BIGINT COMMENT '用户ID（可为空，支持匿名浏览）',
                               news_id BIGINT NOT NULL COMMENT '动态ID',
                               ip_address VARCHAR(50) COMMENT '访问IP地址',
                               user_agent VARCHAR(500) COMMENT '浏览器信息',
                               session_id VARCHAR(100) COMMENT '会话ID',
                               view_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '浏览时间'
);

-- 添加外键约束
ALTER TABLE user_view_log ADD CONSTRAINT fk_user_view_log_news_id
    FOREIGN KEY (news_id) REFERENCES news(id);

-- 创建索引
CREATE INDEX idx_user_view_log_news_id ON user_view_log(news_id);
CREATE INDEX idx_user_view_log_user_id ON user_view_log(user_id);
CREATE INDEX idx_user_view_log_view_time ON user_view_log(view_time);
CREATE INDEX idx_user_view_log_session_news ON user_view_log(session_id, news_id);

-- 创建user_operation_log表
CREATE TABLE user_operation_log (
                                    id BIGSERIAL PRIMARY KEY COMMENT '日志主键ID',
                                    user_id BIGINT NOT NULL COMMENT '操作用户ID',
                                    operation_type VARCHAR(50) NOT NULL COMMENT '操作类型（PUBLISH:发布，EDIT:编辑，DELETE:删除，AUDIT:审核）',
                                    resource_type VARCHAR(50) NOT NULL DEFAULT 'NEWS' COMMENT '资源类型',
                                    resource_id BIGINT NOT NULL COMMENT '资源ID（动态ID）',
                                    operation_desc VARCHAR(500) COMMENT '操作描述',
                                    old_value TEXT COMMENT '操作前的值（JSON格式）',
                                    new_value TEXT COMMENT '操作后的值（JSON格式）',
                                    ip_address VARCHAR(50) COMMENT '操作IP地址',
                                    operation_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
                                    operation_result TINYINT DEFAULT 1 COMMENT '操作结果（0:失败，1:成功）'
);

-- 添加外键约束
ALTER TABLE user_operation_log ADD CONSTRAINT fk_user_operation_log_user_id
    FOREIGN KEY (user_id) REFERENCES users(id);

-- 创建索引
CREATE INDEX idx_user_operation_log_user_time ON user_operation_log(user_id, operation_time);
CREATE INDEX idx_user_operation_log_resource ON user_operation_log(resource_type, resource_id);
CREATE INDEX idx_user_operation_log_operation_type ON user_operation_log(operation_type);