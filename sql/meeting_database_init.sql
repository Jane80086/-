-- 会议管理系统数据库初始化脚本
-- 数据库名称: meeting_management
-- 使用人大金仓数据库

-- ========================================
-- 1. 创建数据库（如果不存在）
-- ========================================
-- 注意：在人大金仓中，通常需要手动创建数据库
-- 可以使用以下命令创建：
-- CREATE DATABASE meeting_management;

-- ========================================
-- 2. 创建用户信息表
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
-- 3. 创建会议信息表
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
-- 4. 创建会议审核记录表
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
-- 5. 插入用户测试数据
-- ========================================

-- 管理员用户 (密码都是123456的MD5值: e10adc3949ba59abbe56e057f20f883e)
INSERT INTO user_info (username, password, real_name, email, phone, user_type, status) VALUES
('admin', 'e10adc3949ba59abbe56e057f20f883e', '系统管理员', 'admin@company.com', '13800138001', 'ADMIN', 1),
('admin001', 'e10adc3949ba59abbe56e057f20f883e', '张管理员', 'admin001@company.com', '13800138002', 'ADMIN', 1),
('admin002', 'e10adc3949ba59abbe56e057f20f883e', '李管理员', 'admin002@company.com', '13800138003', 'ADMIN', 1);

-- 企业用户
INSERT INTO user_info (username, password, real_name, email, phone, user_type, status) VALUES
('enterprise1', 'e10adc3949ba59abbe56e057f20f883e', '腾讯科技', 'contact@tencent.com', '13800138006', 'ENTERPRISE', 1),
('enterprise2', 'e10adc3949ba59abbe56e057f20f883e', '阿里巴巴', 'contact@alibaba.com', '13800138007', 'ENTERPRISE', 1),
('enterprise3', 'e10adc3949ba59abbe56e057f20f883e', '百度公司', 'contact@baidu.com', '13800138008', 'ENTERPRISE', 1);

-- 普通用户
INSERT INTO user_info (username, password, real_name, email, phone, user_type, status) VALUES
('user1', 'e10adc3949ba59abbe56e057f20f883e', '张三', 'zhangsan@email.com', '13800138016', 'NORMAL', 1),
('user2', 'e10adc3949ba59abbe56e057f20f883e', '李四', 'lisi@email.com', '13800138017', 'NORMAL', 1),
('user3', 'e10adc3949ba59abbe56e057f20f883e', '王五', 'wangwu@email.com', '13800138018', 'NORMAL', 1);

-- ========================================
-- 6. 插入会议测试数据
-- ========================================

-- 管理员创建的会议（直接通过）
INSERT INTO meeting_info (meeting_name, start_time, end_time, creator, meeting_content, image_url, status, create_time) VALUES
('2024年第一季度工作总结会议', '2024-01-15 09:00:00', '2024-01-15 11:00:00', 'admin', '回顾第一季度工作成果，分析存在的问题，制定第二季度工作计划。', 'https://example.com/cover1.jpg', 1, '2024-01-10 10:00:00'),
('技术架构评审会议', '2024-01-20 14:00:00', '2024-01-20 16:00:00', 'admin', '评审新系统的技术架构设计，确保系统的可扩展性和稳定性。', 'https://example.com/cover2.jpg', 1, '2024-01-12 14:30:00');

-- 企业用户创建的会议（已通过审核）
INSERT INTO meeting_info (meeting_name, start_time, end_time, creator, meeting_content, image_url, status, reviewer, review_time, review_comment, create_time) VALUES
('腾讯云技术分享会', '2024-01-18 14:00:00', '2024-01-18 16:00:00', 'enterprise1', '分享腾讯云最新技术成果和应用案例，探讨云计算发展趋势。', 'https://example.com/cover6.jpg', 1, 'admin', '2024-01-16 10:00:00', '内容符合要求，审核通过', '2024-01-15 16:00:00'),
('阿里云解决方案研讨会', '2024-01-25 09:00:00', '2024-01-25 11:00:00', 'enterprise2', '探讨阿里云在企业数字化转型中的解决方案和应用实践。', 'https://example.com/cover7.jpg', 1, 'admin001', '2024-01-23 14:30:00', '会议内容有价值，同意举办', '2024-01-22 09:00:00');

-- 企业用户创建的会议（待审核）
INSERT INTO meeting_info (meeting_name, start_time, end_time, creator, meeting_content, image_url, status, create_time) VALUES
('百度AI技术交流会', '2024-02-05 15:00:00', '2024-02-05 17:00:00', 'enterprise3', '交流百度在人工智能领域的最新研究成果和技术应用。', 'https://example.com/cover8.jpg', 0, '2024-02-02 10:00:00');

-- ========================================
-- 7. 插入审核记录测试数据
-- ========================================

-- 已通过会议的审核记录
INSERT INTO meeting_review_record (meeting_id, meeting_name, creator, reviewer, status, review_comment, review_time) VALUES
(3, '腾讯云技术分享会', 'enterprise1', 'admin', 1, '内容符合要求，审核通过', '2024-01-16 10:00:00'),
(4, '阿里云解决方案研讨会', 'enterprise2', 'admin001', 1, '会议内容有价值，同意举办', '2024-01-23 14:30:00');

-- ========================================
-- 8. 数据统计
-- ========================================
-- 用户统计
-- 管理员: 3个
-- 企业用户: 3个  
-- 普通用户: 3个
-- 总计: 9个用户

-- 会议统计
-- 已通过: 4个
-- 待审核: 1个
-- 总计: 5个会议 