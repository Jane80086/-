# 数据库整合迁移指南

## 概述
本指南将帮助你将多个独立的数据库整合为一个统一的 `course_manager` 数据库。

## 当前数据库状态
- **course_management** - 课程管理数据库
- **meeting_management** - 会议管理数据库  
- **news_management** - 新闻管理数据库
- **system_management** - 系统管理数据库
- **cemenghui** - 其他数据库
- **course_manager** - 主应用数据库（目标统一数据库）

## 迁移步骤

### 1. 备份现有数据库
```sql
-- 备份所有现有数据库
-- 在数据库管理工具中执行备份操作
```

### 2. 创建统一数据库
```sql
-- 如果 course_manager 数据库不存在，创建它
CREATE DATABASE course_manager;
```

### 3. 执行统一数据库架构脚本
```sql
-- 在 course_manager 数据库中执行
-- unified_database_schema.sql
```

### 4. 数据迁移脚本

#### 4.1 迁移课程数据
```sql
-- 从 course_management 迁移到 course_manager
INSERT INTO course_manager.courses 
SELECT * FROM course_management.courses;

INSERT INTO course_manager.course_review_history
SELECT * FROM course_management.course_review_history;

INSERT INTO course_manager.comments
SELECT * FROM course_management.comments;

INSERT INTO course_manager.course_history
SELECT * FROM course_management.course_history;

INSERT INTO course_manager.review
SELECT * FROM course_management.review;

INSERT INTO course_manager.questions
SELECT * FROM course_management.questions;

INSERT INTO course_manager.featured_course
SELECT * FROM course_management.featured_course;
```

#### 4.2 迁移会议数据
```sql
-- 从 meeting_management 迁移到 course_manager
INSERT INTO course_manager.meeting_info
SELECT * FROM meeting_management.meeting_info;

INSERT INTO course_manager.meeting_review_record
SELECT * FROM meeting_management.meeting_review_record;
```

#### 4.3 迁移新闻数据
```sql
-- 从 news_management 迁移到 course_manager
INSERT INTO course_manager.news
SELECT * FROM news_management.news;
```

#### 4.4 迁移系统用户数据
```sql
-- 从 system_management 迁移到 course_manager
INSERT INTO course_manager.system_user
SELECT * FROM system_management.system_user;
```

#### 4.5 迁移用户数据
```sql
-- 从 cemenghui 或其他数据库迁移用户数据
INSERT INTO course_manager.user_info
SELECT * FROM cemenghui.user_info;
```

### 5. 验证数据迁移
```sql
-- 检查各表数据数量
SELECT 'courses' as table_name, COUNT(*) as count FROM courses
UNION ALL
SELECT 'meeting_info', COUNT(*) FROM meeting_info
UNION ALL
SELECT 'news', COUNT(*) FROM news
UNION ALL
SELECT 'system_user', COUNT(*) FROM system_user
UNION ALL
SELECT 'user_info', COUNT(*) FROM user_info;
```

### 6. 更新应用配置
所有子系统的配置文件已更新为使用统一的 `course_manager` 数据库：

- ✅ `course/src/main/resources/application-course.yml`
- ✅ `meeting/src/main/resources/application-meeting.yml`
- ✅ `news/src/main/resources/application-news.yml`
- ✅ `system/src/main/resources/application-system.yml`
- ✅ 所有 `config.properties` 文件
- ✅ 所有 `application.properties` 文件

### 7. 测试各子系统
1. 启动主应用：`main-app`
2. 启动课程子系统：`course`
3. 启动会议子系统：`meeting`
4. 启动新闻子系统：`news`
5. 启动系统子系统：`system`

### 8. 清理旧数据库（可选）
```sql
-- 确认所有数据迁移成功后，可以删除旧数据库
-- DROP DATABASE course_management;
-- DROP DATABASE meeting_management;
-- DROP DATABASE news_management;
-- DROP DATABASE system_management;
-- DROP DATABASE cemenghui;
```

## 注意事项

### 数据一致性
- 确保在迁移过程中停止所有应用服务
- 验证外键关系的完整性
- 检查序列（自增ID）的正确性

### 性能优化
- 为常用查询字段创建索引
- 考虑分区表（如果数据量很大）
- 优化查询语句

### 备份策略
- 保留原始数据库备份至少一个月
- 定期备份统一数据库
- 建立数据恢复流程

## 故障排除

### 常见问题
1. **序列冲突**：如果遇到ID冲突，需要重新设置序列
2. **外键约束**：确保相关表的数据完整性
3. **字符编码**：确保所有数据使用相同的字符编码

### 回滚方案
如果迁移失败，可以：
1. 恢复原始数据库备份
2. 重新配置应用使用原始数据库
3. 分析失败原因并修复后重新迁移

## 完成后的优势

1. **统一管理**：所有数据在一个数据库中，便于管理
2. **减少复杂性**：不需要维护多个数据库连接
3. **数据一致性**：避免跨数据库的数据不一致问题
4. **性能提升**：减少网络开销和连接管理
5. **备份简化**：只需要备份一个数据库
6. **维护成本降低**：减少数据库维护工作量 