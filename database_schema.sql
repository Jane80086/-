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