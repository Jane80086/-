-- 新闻管理系统数据库初始化脚本
-- 数据库名称: news_management
-- 使用人大金仓数据库

-- ========================================
-- 1. 创建数据库（如果不存在）
-- ========================================
-- 注意：在人大金仓中，通常需要手动创建数据库
-- 可以使用以下命令创建：
-- CREATE DATABASE news_management;

-- ========================================
-- 2. 创建用户表（如果不存在）
-- ========================================
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(20),
    user_type VARCHAR(20) NOT NULL DEFAULT 'NORMAL',
    status INTEGER DEFAULT 1,
    nickname VARCHAR(50),
    gender INTEGER DEFAULT 0,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 创建用户表索引
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_user_type ON users(user_type);
CREATE INDEX IF NOT EXISTS idx_users_status ON users(status);

-- ========================================
-- 3. 创建新闻表
-- ========================================
CREATE TABLE IF NOT EXISTS news (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    image VARCHAR(500),
    content TEXT NOT NULL,
    summary TEXT,
    author VARCHAR(100) NOT NULL,
    user_id BIGINT NOT NULL,
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

-- 添加外键约束
ALTER TABLE news ADD CONSTRAINT IF NOT EXISTS fk_news_user_id
    FOREIGN KEY (user_id) REFERENCES users(id);

ALTER TABLE news ADD CONSTRAINT IF NOT EXISTS fk_news_audit_user_id
    FOREIGN KEY (audit_user_id) REFERENCES users(id);

-- 创建新闻表索引
CREATE INDEX IF NOT EXISTS idx_news_user_id ON news(user_id);
CREATE INDEX IF NOT EXISTS idx_news_status ON news(status);
CREATE INDEX IF NOT EXISTS idx_news_create_time ON news(create_time);
CREATE INDEX IF NOT EXISTS idx_news_title ON news(title);
CREATE INDEX IF NOT EXISTS idx_news_author ON news(author);
CREATE INDEX IF NOT EXISTS idx_news_is_deleted ON news(is_deleted);

-- ========================================
-- 4. 创建用户浏览日志表
-- ========================================
CREATE TABLE IF NOT EXISTS user_view_log (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT,
    news_id BIGINT NOT NULL,
    ip_address VARCHAR(50),
    user_agent VARCHAR(500),
    session_id VARCHAR(100),
    view_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 添加外键约束
ALTER TABLE user_view_log ADD CONSTRAINT IF NOT EXISTS fk_user_view_log_news_id
    FOREIGN KEY (news_id) REFERENCES news(id);

-- 创建浏览日志表索引
CREATE INDEX IF NOT EXISTS idx_user_view_log_news_id ON user_view_log(news_id);
CREATE INDEX IF NOT EXISTS idx_user_view_log_user_id ON user_view_log(user_id);
CREATE INDEX IF NOT EXISTS idx_user_view_log_view_time ON user_view_log(view_time);
CREATE INDEX IF NOT EXISTS idx_user_view_log_session_news ON user_view_log(session_id, news_id);

-- ========================================
-- 5. 创建用户操作日志表
-- ========================================
CREATE TABLE IF NOT EXISTS user_operation_log (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    operation_type VARCHAR(50) NOT NULL,
    resource_type VARCHAR(50) NOT NULL DEFAULT 'NEWS',
    resource_id BIGINT NOT NULL,
    operation_desc VARCHAR(500),
    old_value TEXT,
    new_value TEXT,
    ip_address VARCHAR(50),
    operation_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    operation_result INTEGER DEFAULT 1
);

-- 添加外键约束
ALTER TABLE user_operation_log ADD CONSTRAINT IF NOT EXISTS fk_user_operation_log_user_id
    FOREIGN KEY (user_id) REFERENCES users(id);

-- 创建操作日志表索引
CREATE INDEX IF NOT EXISTS idx_user_operation_log_user_time ON user_operation_log(user_id, operation_time);
CREATE INDEX IF NOT EXISTS idx_user_operation_log_resource ON user_operation_log(resource_type, resource_id);
CREATE INDEX IF NOT EXISTS idx_user_operation_log_operation_type ON user_operation_log(operation_type);

-- ========================================
-- 6. 插入测试数据
-- ========================================

-- 插入测试用户
INSERT INTO users (username, password, email, user_type, nickname) VALUES 
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'admin@news.com', 'ADMIN', '管理员'),
('editor1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'editor1@news.com', 'ENTERPRISE', '编辑1'),
('reporter1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'reporter1@news.com', 'NORMAL', '记者1')
ON CONFLICT (username) DO NOTHING;

-- 插入测试新闻
INSERT INTO news (title, content, summary, author, user_id, status, view_count) VALUES 
('人工智能技术发展趋势', '人工智能技术正在快速发展，从机器学习到深度学习，再到现在的生成式AI，技术不断突破...', '探讨AI技术的最新发展趋势和应用前景', '张三', 2, 1, 150),
('数字化转型助力企业发展', '在数字化时代，企业必须进行数字化转型才能保持竞争力...', '分析数字化转型对企业发展的重要性', '李四', 3, 1, 89),
('云计算技术应用实践', '云计算技术为企业提供了灵活、可扩展的IT基础设施...', '分享云计算技术的实际应用案例', '王五', 2, 0, 0),
('大数据分析在商业中的应用', '大数据分析技术帮助企业更好地理解客户需求，优化业务流程...', '介绍大数据分析在商业领域的应用', '赵六', 3, 1, 234)
ON CONFLICT DO NOTHING;

-- 插入测试浏览日志
INSERT INTO user_view_log (user_id, news_id, ip_address, session_id) VALUES 
(3, 1, '192.168.1.100', 'session_001'),
(3, 2, '192.168.1.100', 'session_001'),
(2, 1, '192.168.1.101', 'session_002'),
(2, 4, '192.168.1.101', 'session_002')
ON CONFLICT DO NOTHING;

-- 插入测试操作日志
INSERT INTO user_operation_log (user_id, operation_type, resource_id, operation_desc, ip_address) VALUES 
(2, 'PUBLISH', 1, '发布新闻：人工智能技术发展趋势', '192.168.1.102'),
(3, 'PUBLISH', 2, '发布新闻：数字化转型助力企业发展', '192.168.1.103'),
(1, 'AUDIT', 1, '审核通过新闻：人工智能技术发展趋势', '192.168.1.104'),
(1, 'AUDIT', 2, '审核通过新闻：数字化转型助力企业发展', '192.168.1.104')
ON CONFLICT DO NOTHING; 