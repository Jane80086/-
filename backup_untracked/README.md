# 课程管理系统说明文档

---

## 一、项目简介

本系统为多模块课程管理平台，包含注册、登录、用户管理、课程、新闻、会议、系统等子模块，前后端分离，支持统一用户体系和金仓数据库。

---

## 二、技术栈
- 后端：Java 11+, Spring Boot, MyBatis, Maven
- 前端：Vue 3, Element Plus, Vite
- 数据库：KingbaseES（金仓数据库）

---

## 三、环境依赖
- JDK 11 及以上
- Node.js 16+、npm/yarn
- KingbaseES（金仓数据库）

---

## 四、数据库设置与初始化

### 1. 创建数据库

建议使用金仓数据库管理工具，或执行：
```sql
CREATE DATABASE course_manager;
```

### 2. 导入数据库结构

1. 连接到`course_manager`数据库
2. 执行`course_manager_kingbase_complete.sql`脚本

### 3. 启动步骤

1. 创建数据库
2. 导入表结构
3. 运行`mvn clean compile`编译项目
4. 启动后端服务

### 4. 注意事项
- 确保金仓数据库服务已启动
- 数据库连接参数正确（host: localhost, port: 54321）
- 用户权限足够

---

## 五、数据库表结构分析与优化

### 1. 优化目标
- 减少表数量（11→6）
- 统一用户、审核、日志、历史记录表
- 提高可维护性和扩展性

### 2. 优化前后对比
| 模块 | 优化前 | 优化后 |
|------|--------|--------|
| 用户 | user_info, admin_user, enterprise_user, system_user | users, enterprise |
| 审核 | course_review_history, meeting_review_record, review | audit_records |
| 日志 | user_view_log, user_operation_log | user_view_logs, user_operation_logs |
| 历史 | course_history, user_modify_history | history_records |

### 3. 统一表结构示例

#### users 表
```sql
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    real_name VARCHAR(100),
    email VARCHAR(255),
    phone VARCHAR(20),
    user_type VARCHAR(20) NOT NULL, -- ADMIN, ENTERPRISE, NORMAL, SYSTEM
    status INTEGER DEFAULT 1,
    department VARCHAR(100),
    nickname VARCHAR(100),
    is_remembered BOOLEAN DEFAULT FALSE,
    enterprise_id VARCHAR(64),
    dynamic_code VARCHAR(20),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0
);
```

#### audit_records 表
```sql
CREATE TABLE audit_records (
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
```

#### user_view_logs 表
```sql
CREATE TABLE user_view_logs (
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
```

#### user_operation_logs 表
```sql
CREATE TABLE user_operation_logs (
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
    operation_result INTEGER DEFAULT 1
);
```

#### history_records 表
```sql
CREATE TABLE history_records (
    id SERIAL PRIMARY KEY,
    resource_type VARCHAR(50) NOT NULL, -- COURSE, USER, ENTERPRISE
    resource_id BIGINT NOT NULL,
    action VARCHAR(50) NOT NULL, -- VIEW, MODIFY, DELETE
    user_id BIGINT,
    old_value TEXT,
    new_value TEXT,
    record_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

---

## 六、数据库优化实施指南

### 1. 备份与测试
- 备份原有数据库
- 在测试环境验证新表结构和迁移脚本

### 2. 数据迁移
- 按照分析报告中的迁移SQL，将旧表数据迁移到新表
- 验证迁移后数据完整性

### 3. 功能测试
- 验证用户、课程、会议、新闻等主要功能
- 检查审核、日志、历史记录等功能

### 4. 生产部署
- 停止应用，备份生产库，执行迁移，重启应用

---

## 七、常见问题与解决方案

### 1. 数据库不存在
- 检查数据库是否已创建，连接参数是否正确

### 2. 方言或驱动错误
- 确认JPA配置和依赖已正确设置为Kingbase/PostgreSQL

### 3. 迁移后数据缺失
- 检查迁移脚本执行日志，核对数据量

---

## 八、附录

- 详细表结构、索引、视图、存储过程请见 `course_manager_kingbase_complete.sql`
- 如需回滚，使用备份文件恢复

---

> 本文档整合了所有数据库相关说明、分析、实施和常见问题，作为唯一权威说明文档。


