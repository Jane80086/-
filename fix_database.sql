-- 修复数据库表结构
-- 将level字段重命名为course_level

-- 1. 重命名level字段为course_level
ALTER TABLE courses RENAME COLUMN level TO course_level;

-- 2. 更新索引（如果存在）
DROP INDEX IF EXISTS idx_courses_level;
CREATE INDEX idx_courses_course_level ON courses(course_level);

-- 3. 验证修改
SELECT column_name, data_type, is_nullable, column_default 
FROM information_schema.columns 
WHERE table_name = 'courses' 
ORDER BY ordinal_position; 