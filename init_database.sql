-- 金仓数据库初始化脚本
-- 请以管理员身份连接到金仓数据库，然后执行以下命令

-- 1. 创建数据库
CREATE DATABASE course_management;

-- 2. 连接到新创建的数据库
-- \c course_management

-- 3. 创建用户（如果需要）
-- CREATE USER course_user WITH PASSWORD 'course_password';

-- 4. 授权（如果需要）
-- GRANT ALL PRIVILEGES ON DATABASE course_management TO course_user;

-- 5. 设置默认编码（如果需要）
-- ALTER DATABASE course_management SET client_encoding TO 'UTF8';

-- 注意：
-- 1. 执行完此脚本后，再执行 database_schema.sql 创建表结构
-- 2. 确保 application.yml 中的数据库连接信息正确
-- 3. 如果使用不同的用户名和密码，请相应修改 application.yml 