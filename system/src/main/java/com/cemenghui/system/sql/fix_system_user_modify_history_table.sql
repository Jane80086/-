-- ========================================
-- 修复 system_user_modify_history 表缺失问题
-- 数据库类型: 人大金仓数据库 (KingbaseES V8)
-- 创建时间: 2025年7月11日
-- 描述: 添加缺失的 system_user_modify_history 表
-- ========================================

-- 系统用户修改历史表
CREATE TABLE IF NOT EXISTS system_user_modify_history (
    id SERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    modify_type VARCHAR(50) NOT NULL,
    field_name VARCHAR(100),
    old_value TEXT,
    new_value TEXT,
    modify_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    operator_id BIGINT
);

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_system_user_modify_history_user ON system_user_modify_history(user_id);
CREATE INDEX IF NOT EXISTS idx_system_user_modify_history_time ON system_user_modify_history(modify_time);
CREATE INDEX IF NOT EXISTS idx_system_user_modify_history_operator ON system_user_modify_history(operator_id);

-- 插入一些测试数据（可选）
INSERT INTO system_user_modify_history (user_id, modify_type, field_name, old_value, new_value, modify_time, operator_id)
VALUES 
(1, 'UPDATE', 'realName', 'old_name', 'new_name', CURRENT_TIMESTAMP, 1),
(1, 'UPDATE', 'phone', 'old_phone', 'new_phone', CURRENT_TIMESTAMP, 1),
(1, 'UPDATE', 'email', 'old_email', 'new_email', CURRENT_TIMESTAMP, 1)
ON CONFLICT DO NOTHING;

-- 验证表创建成功
SELECT 'system_user_modify_history table created successfully' as status; 

-- 修复system_user_modify_history表结构
-- 添加缺失的field_name字段

-- 检查字段是否存在，如果不存在则添加
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'system_user_modify_history' 
        AND column_name = 'field_name'
    ) THEN
        ALTER TABLE system_user_modify_history ADD COLUMN field_name VARCHAR(255);
        RAISE NOTICE '成功添加field_name字段到system_user_modify_history表';
    ELSE
        RAISE NOTICE 'field_name字段已存在，无需添加';
    END IF;
END $$;

-- 验证表结构
SELECT column_name, data_type, is_nullable 
FROM information_schema.columns 
WHERE table_name = 'system_user_modify_history' 
ORDER BY ordinal_position; 