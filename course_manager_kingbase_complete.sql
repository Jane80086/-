-- ========================================
-- 课程管理系统 - 金仓数据库完整脚本
-- 数据库名称: course_manager
-- 数据库类型: 人大金仓数据库 (KingbaseES V8)
-- 创建时间: 2024年7月5日
-- 描述: 整合所有子系统的统一数据库脚本，适配金仓数据库语法
-- ========================================

-- ========================================
-- 1. 数据库创建（需要手动执行）
-- ========================================
-- 在人大金仓数据库管理工具中执行：
-- CREATE DATABASE course_manager;

-- ========================================
-- 2. 统一用户模块（users）
-- ========================================

-- 统一用户信息表（整合所有用户类型）
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    real_name VARCHAR(100),
    email VARCHAR(255),
    phone VARCHAR(20),
    user_type VARCHAR(20) NOT NULL, -- ADMIN, ENTERPRISE, NORMAL, SYSTEM
    status INTEGER DEFAULT 1, -- 0: 禁用, 1: 启用
    department VARCHAR(100),
    nickname VARCHAR(100),
    avatar VARCHAR(500),
    is_remembered BOOLEAN DEFAULT FALSE,
    enterprise_id VARCHAR(64),
    dynamic_code VARCHAR(20),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0
);

-- 企业信息表
CREATE TABLE IF NOT EXISTS enterprise (
    enterprise_id VARCHAR(30) PRIMARY KEY,
    enterprise_name VARCHAR(100) NOT NULL,
    credit_code VARCHAR(30) UNIQUE,
    register_address VARCHAR(200),
    legal_representative VARCHAR(50),
    registration_date DATE,
    enterprise_type VARCHAR(50),
    registered_capital VARCHAR(50),
    business_scope TEXT,
    establishment_date DATE,
    business_term VARCHAR(100),
    registration_authority VARCHAR(100),
    approval_date DATE,
    enterprise_status VARCHAR(30),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ========================================
-- 3. 统一审核模块（audit）
-- ========================================

-- 统一审核记录表（整合所有审核功能）
CREATE TABLE IF NOT EXISTS audit_records (
    id SERIAL PRIMARY KEY,
    resource_type VARCHAR(50) NOT NULL, -- COURSE, MEETING, NEWS, USER
    resource_id BIGINT NOT NULL,
    resource_name VARCHAR(255),
    action VARCHAR(50) NOT NULL, -- APPROVE, REJECT, MODIFY, DELETE
    reviewer_id BIGINT NOT NULL,
    reviewer_name VARCHAR(100),
    status VARCHAR(20) NOT NULL, -- PENDING, APPROVED, REJECTED
    comment TEXT,
    old_value TEXT,
    new_value TEXT,
    audit_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ========================================
-- 4. 统一日志模块（log）
-- ========================================

-- 统一用户浏览日志表（整合所有浏览记录）
CREATE TABLE IF NOT EXISTS user_view_logs (
    id SERIAL PRIMARY KEY,
    user_id BIGINT,
    resource_type VARCHAR(50) NOT NULL, -- COURSE, NEWS, MEETING
    resource_id BIGINT NOT NULL,
    resource_title VARCHAR(255),
    ip_address VARCHAR(50),
    user_agent VARCHAR(500),
    session_id VARCHAR(100),
    view_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 统一用户操作日志表（整合所有操作记录）
CREATE TABLE IF NOT EXISTS user_operation_logs (
    id SERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    operation_type VARCHAR(50) NOT NULL, -- CREATE, UPDATE, DELETE, PUBLISH, AUDIT
    resource_type VARCHAR(50) NOT NULL, -- COURSE, NEWS, MEETING, USER
    resource_id BIGINT NOT NULL,
    resource_title VARCHAR(255),
    operation_desc VARCHAR(500),
    old_value TEXT,
    new_value TEXT,
    ip_address VARCHAR(50),
    operation_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    operation_result INTEGER DEFAULT 1 -- 0: 失败, 1: 成功
);

-- ========================================
-- 5. 统一历史记录模块（history）
-- ========================================

-- 统一历史记录表（整合所有历史记录）
CREATE TABLE IF NOT EXISTS history_records (
    id SERIAL PRIMARY KEY,
    resource_type VARCHAR(50) NOT NULL, -- COURSE, USER, ENTERPRISE
    resource_id BIGINT NOT NULL,
    action VARCHAR(50) NOT NULL, -- VIEW, MODIFY, DELETE
    user_id BIGINT,
    old_value TEXT,
    new_value TEXT,
    record_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ========================================
-- 6. 课程模块（course）
-- ========================================

-- 课程表
CREATE TABLE IF NOT EXISTS courses (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    instructor_id BIGINT NOT NULL,
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
    favorite_count INTEGER DEFAULT 0,
    deleted INTEGER DEFAULT 0
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
    status VARCHAR(20) DEFAULT 'NORMAL',
    deleted INTEGER DEFAULT 0
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
-- 7. 会议模块（meeting）
-- ========================================

-- 会议信息表
CREATE TABLE IF NOT EXISTS meetings (
    id SERIAL PRIMARY KEY,
    meeting_name VARCHAR(255) NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP,
    creator_id BIGINT NOT NULL,
    creator_name VARCHAR(100),
    meeting_content TEXT,
    status INTEGER DEFAULT 0, -- 0: 待审核, 1: 已通过, 2: 已拒绝
    reviewer_id BIGINT,
    reviewer_name VARCHAR(100),
    review_time TIMESTAMP,
    review_comment TEXT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    image_url VARCHAR(500),
    deleted INTEGER DEFAULT 0
);

-- 会议审核记录表
CREATE TABLE IF NOT EXISTS meeting_review_record (
    id SERIAL PRIMARY KEY,
    meeting_id INTEGER NOT NULL,
    meeting_name VARCHAR(255),
    creator_name VARCHAR(100),
    reviewer_name VARCHAR(100),
    status INTEGER,
    review_comment TEXT,
    review_time TIMESTAMP
);

-- ========================================
-- 8. 新闻模块（news）
-- ========================================

-- 新闻表
CREATE TABLE IF NOT EXISTS news (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    image VARCHAR(500),
    content TEXT,
    summary VARCHAR(500),
    author VARCHAR(100),
    user_id BIGINT,
    status INTEGER DEFAULT 0, -- 0: 待审核, 1: 已发布, 2: 已拒绝
    view_count INTEGER DEFAULT 0,
    is_deleted INTEGER DEFAULT 0,
    audit_comment TEXT,
    audit_user_id BIGINT,
    audit_time TIMESTAMP,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ========================================
-- 9. 系统模块（system）
-- ========================================

-- 系统企业表
CREATE TABLE IF NOT EXISTS system_enterprise (
    enterprise_id VARCHAR(30) PRIMARY KEY,
    enterprise_name VARCHAR(100) NOT NULL,
    credit_code VARCHAR(30) UNIQUE,
    register_address VARCHAR(200),
    legal_representative VARCHAR(50),
    registration_date DATE,
    enterprise_type VARCHAR(50),
    registered_capital VARCHAR(50),
    business_scope TEXT,
    establishment_date DATE,
    business_term VARCHAR(100),
    registration_authority VARCHAR(100),
    approval_date DATE,
    enterprise_status VARCHAR(30),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 系统用户模板表
CREATE TABLE IF NOT EXISTS system_user_template (
    template_id SERIAL PRIMARY KEY,
    enterprise_type VARCHAR(50) NOT NULL,
    role VARCHAR(50) NOT NULL,
    permissions TEXT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 系统第三方账户表
CREATE TABLE IF NOT EXISTS system_third_party_account (
    id SERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    platform VARCHAR(50) NOT NULL, -- WECHAT, QQ, ALIPAY
    open_id VARCHAR(100) NOT NULL,
    union_id VARCHAR(100),
    nickname VARCHAR(100),
    avatar VARCHAR(500),
    bind_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status INTEGER DEFAULT 1 -- 0: 解绑, 1: 绑定
);

-- 系统用户修改历史表
CREATE TABLE IF NOT EXISTS system_user_modify_history (
    id SERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    modify_type VARCHAR(50) NOT NULL,
    old_value TEXT,
    new_value TEXT,
    modify_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    operator_id BIGINT
);

-- 系统配置表
CREATE TABLE IF NOT EXISTS system_config (
    config_id SERIAL PRIMARY KEY,
    config_key VARCHAR(100) NOT NULL UNIQUE,
    config_value TEXT,
    config_desc VARCHAR(255),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 系统日志表
CREATE TABLE IF NOT EXISTS system_log (
    log_id SERIAL PRIMARY KEY,
    user_id BIGINT,
    operation_type VARCHAR(50) NOT NULL,
    operation_desc VARCHAR(500),
    ip_address VARCHAR(50),
    user_agent VARCHAR(500),
    operation_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    operation_result INTEGER DEFAULT 1 -- 0: 失败, 1: 成功
);

-- ========================================
-- 10. 创建索引
-- ========================================

-- 用户表索引
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_user_type ON users(user_type);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_status ON users(status);
CREATE INDEX IF NOT EXISTS idx_users_enterprise_id ON users(enterprise_id);

-- 企业表索引
CREATE INDEX IF NOT EXISTS idx_enterprise_credit_code ON enterprise(credit_code);
CREATE INDEX IF NOT EXISTS idx_enterprise_name ON enterprise(enterprise_name);

-- 审核记录表索引
CREATE INDEX IF NOT EXISTS idx_audit_records_resource ON audit_records(resource_type, resource_id);
CREATE INDEX IF NOT EXISTS idx_audit_records_reviewer ON audit_records(reviewer_id);
CREATE INDEX IF NOT EXISTS idx_audit_records_status ON audit_records(status);
CREATE INDEX IF NOT EXISTS idx_audit_records_time ON audit_records(audit_time);

-- 用户浏览日志表索引
CREATE INDEX IF NOT EXISTS idx_user_view_logs_user ON user_view_logs(user_id);
CREATE INDEX IF NOT EXISTS idx_user_view_logs_resource ON user_view_logs(resource_type, resource_id);
CREATE INDEX IF NOT EXISTS idx_user_view_logs_time ON user_view_logs(view_time);
CREATE INDEX IF NOT EXISTS idx_user_view_logs_session ON user_view_logs(session_id, resource_id);

-- 用户操作日志表索引
CREATE INDEX IF NOT EXISTS idx_user_operation_logs_user ON user_operation_logs(user_id);
CREATE INDEX IF NOT EXISTS idx_user_operation_logs_resource ON user_operation_logs(resource_type, resource_id);
CREATE INDEX IF NOT EXISTS idx_user_operation_logs_type ON user_operation_logs(operation_type);
CREATE INDEX IF NOT EXISTS idx_user_operation_logs_time ON user_operation_logs(operation_time);

-- 历史记录表索引
CREATE INDEX IF NOT EXISTS idx_history_records_resource ON history_records(resource_type, resource_id);
CREATE INDEX IF NOT EXISTS idx_history_records_user ON history_records(user_id);
CREATE INDEX IF NOT EXISTS idx_history_records_time ON history_records(record_time);

-- 课程表索引
CREATE INDEX IF NOT EXISTS idx_courses_instructor_id ON courses(instructor_id);
CREATE INDEX IF NOT EXISTS idx_courses_status ON courses(status);
CREATE INDEX IF NOT EXISTS idx_courses_category ON courses(category);
CREATE INDEX IF NOT EXISTS idx_courses_course_level ON courses(course_level);
CREATE INDEX IF NOT EXISTS idx_course_review_history_course_id ON course_review_history(course_id);
CREATE INDEX IF NOT EXISTS idx_comments_course_id ON comments(course_id);
CREATE INDEX IF NOT EXISTS idx_comments_user_id ON comments(user_id);
CREATE INDEX IF NOT EXISTS idx_course_history_course_id ON course_history(course_id);
CREATE INDEX IF NOT EXISTS idx_course_history_user_id ON course_history(user_id);
CREATE INDEX IF NOT EXISTS idx_review_course_id ON review(course_id);
CREATE INDEX IF NOT EXISTS idx_questions_course_id ON questions(course_id);
CREATE INDEX IF NOT EXISTS idx_questions_user_id ON questions(user_id);
CREATE INDEX IF NOT EXISTS idx_featured_course_course_id ON featured_course(course_id);

-- 会议表索引
CREATE INDEX IF NOT EXISTS idx_meetings_creator_id ON meetings(creator_id);
CREATE INDEX IF NOT EXISTS idx_meetings_status ON meetings(status);
CREATE INDEX IF NOT EXISTS idx_meetings_start_time ON meetings(start_time);
CREATE INDEX IF NOT EXISTS idx_meetings_create_time ON meetings(create_time);
CREATE INDEX IF NOT EXISTS idx_meeting_review_record_meeting_id ON meeting_review_record(meeting_id);
CREATE INDEX IF NOT EXISTS idx_meeting_review_record_reviewer ON meeting_review_record(reviewer_name);
CREATE INDEX IF NOT EXISTS idx_meeting_review_record_creator ON meeting_review_record(creator_name);

-- 新闻表索引
CREATE INDEX IF NOT EXISTS idx_news_user_id ON news(user_id);
CREATE INDEX IF NOT EXISTS idx_news_status ON news(status);
CREATE INDEX IF NOT EXISTS idx_news_create_time ON news(create_time);
CREATE INDEX IF NOT EXISTS idx_news_title ON news(title);
CREATE INDEX IF NOT EXISTS idx_news_author ON news(author);
CREATE INDEX IF NOT EXISTS idx_news_is_deleted ON news(is_deleted);

-- 系统表索引
CREATE INDEX IF NOT EXISTS idx_system_enterprise_credit_code ON system_enterprise(credit_code);
CREATE INDEX IF NOT EXISTS idx_system_enterprise_name ON system_enterprise(enterprise_name);
CREATE INDEX IF NOT EXISTS idx_system_enterprise_status ON system_enterprise(enterprise_status);
CREATE INDEX IF NOT EXISTS idx_system_user_template_type ON system_user_template(enterprise_type);
CREATE INDEX IF NOT EXISTS idx_system_user_template_role ON system_user_template(role);
CREATE INDEX IF NOT EXISTS idx_system_third_party_user ON system_third_party_account(user_id);
CREATE INDEX IF NOT EXISTS idx_system_third_party_platform ON system_third_party_account(platform);
CREATE INDEX IF NOT EXISTS idx_system_third_party_openid ON system_third_party_account(open_id);
CREATE INDEX IF NOT EXISTS idx_system_user_modify_history_user ON system_user_modify_history(user_id);
CREATE INDEX IF NOT EXISTS idx_system_user_modify_history_time ON system_user_modify_history(modify_time);
CREATE INDEX IF NOT EXISTS idx_system_user_modify_history_operator ON system_user_modify_history(operator_id);
CREATE INDEX IF NOT EXISTS idx_system_log_user ON system_log(user_id);
CREATE INDEX IF NOT EXISTS idx_system_log_time ON system_log(operation_time);
CREATE INDEX IF NOT EXISTS idx_system_log_type ON system_log(operation_type);

-- ========================================
-- 11. 插入初始数据
-- ========================================

-- 插入默认管理员用户
INSERT INTO users (username, password, real_name, email, user_type, status, nickname, create_time, update_time)
VALUES 
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', '系统管理员', 'admin@example.com', 'ADMIN', 1, '管理员', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (username) DO NOTHING;

-- 插入默认企业信息
INSERT INTO enterprise (enterprise_id, enterprise_name, credit_code, enterprise_status, create_time, update_time)
VALUES 
('ENT001', '示例企业', '91110000100000000X', '正常', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (enterprise_id) DO NOTHING;

-- 插入系统配置
INSERT INTO system_config (config_key, config_value, config_desc, create_time, update_time)
VALUES 
('SYSTEM_NAME', '课程管理系统', '系统名称', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('SYSTEM_VERSION', '1.0.0', '系统版本', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('MAX_FILE_SIZE', '10485760', '最大文件上传大小（字节）', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('ALLOWED_FILE_TYPES', 'jpg,jpeg,png,gif,pdf,doc,docx,xls,xlsx', '允许上传的文件类型', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (config_key) DO NOTHING;

-- ========================================
-- 12. 创建视图
-- ========================================

-- 用户统计视图
CREATE OR REPLACE VIEW user_statistics AS
SELECT 
    user_type,
    COUNT(*) as total_count,
    COUNT(CASE WHEN status = 1 THEN 1 END) as enabled_count,
    COUNT(CASE WHEN status = 0 THEN 1 END) as disabled_count
FROM users 
WHERE deleted = 0
GROUP BY user_type;

-- 课程统计视图
CREATE OR REPLACE VIEW course_statistics AS
SELECT 
    status,
    COUNT(*) as total_count,
    AVG(price) as avg_price,
    SUM(like_count) as total_likes,
    SUM(favorite_count) as total_favorites
FROM courses 
WHERE deleted = 0
GROUP BY status;

-- 会议统计视图
CREATE OR REPLACE VIEW meeting_statistics AS
SELECT 
    status,
    COUNT(*) as total_count,
    COUNT(CASE WHEN start_time > CURRENT_TIMESTAMP THEN 1 END) as upcoming_count,
    COUNT(CASE WHEN start_time <= CURRENT_TIMESTAMP AND end_time >= CURRENT_TIMESTAMP THEN 1 END) as ongoing_count
FROM meetings 
WHERE deleted = 0
GROUP BY status;

-- ========================================
-- 13. 创建存储过程
-- ========================================

-- 用户登录记录存储过程
CREATE OR REPLACE FUNCTION log_user_login(
    p_user_id BIGINT,
    p_ip_address VARCHAR(50),
    p_user_agent VARCHAR(500)
) RETURNS VOID AS $$
BEGIN
    INSERT INTO system_log (user_id, operation_type, operation_desc, ip_address, user_agent, operation_time, operation_result)
    VALUES (p_user_id, 'LOGIN', '用户登录', p_ip_address, p_user_agent, CURRENT_TIMESTAMP, 1);
END;
$$ LANGUAGE plpgsql;

-- 记录用户浏览存储过程
CREATE OR REPLACE FUNCTION log_user_view(
    p_user_id BIGINT,
    p_resource_type VARCHAR(50),
    p_resource_id BIGINT,
    p_resource_title VARCHAR(255),
    p_ip_address VARCHAR(50),
    p_user_agent VARCHAR(500),
    p_session_id VARCHAR(100)
) RETURNS VOID AS $$
BEGIN
    INSERT INTO user_view_logs (user_id, resource_type, resource_id, resource_title, ip_address, user_agent, session_id, view_time)
    VALUES (p_user_id, p_resource_type, p_resource_id, p_resource_title, p_ip_address, p_user_agent, p_session_id, CURRENT_TIMESTAMP);
END;
$$ LANGUAGE plpgsql;

-- ========================================
-- 14. 创建触发器
-- ========================================

-- 用户表更新时间触发器
CREATE OR REPLACE FUNCTION update_user_update_time()
RETURNS TRIGGER AS $$
BEGIN
    NEW.update_time = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_user_update_time
    BEFORE UPDATE ON users
    FOR EACH ROW
    EXECUTE FUNCTION update_user_update_time();

-- 课程表更新时间触发器
CREATE OR REPLACE FUNCTION update_course_update_time()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_time = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_course_update_time
    BEFORE UPDATE ON courses
    FOR EACH ROW
    EXECUTE FUNCTION update_course_update_time();

-- ========================================
-- 15. 权限设置
-- ========================================

-- 为应用用户授予必要权限
-- 注意：需要根据实际数据库用户调整
-- GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO course_manager_user;
-- GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO course_manager_user;

-- ========================================
-- 脚本执行完成
-- ========================================
-- 数据库初始化完成！
-- 包含以下模块：
-- 1. 统一用户模块 (users, enterprise)
-- 2. 统一审核模块 (audit_records)
-- 3. 统一日志模块 (user_view_logs, user_operation_logs)
-- 4. 统一历史记录模块 (history_records)
-- 5. 课程模块 (courses, comments, questions, featured_course等)
-- 6. 会议模块 (meetings, meeting_review_record)
-- 7. 新闻模块 (news)
-- 8. 系统模块 (system_enterprise, system_config等)
-- 9. 完整的索引、视图、存储过程、触发器
-- 10. 初始数据 