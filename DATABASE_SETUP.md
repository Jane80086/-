# 数据库设置说明

## 金仓数据库配置

### 1. 创建数据库

连接到金仓数据库服务器，执行以下SQL：

```sql
CREATE DATABASE course_management;
```

### 2. 创建表结构

连接到 `course_management` 数据库，执行 `database_schema.sql` 文件中的所有SQL语句。

### 3. 验证配置

确保 `application.yml` 中的数据库连接信息正确：

```yaml
spring:
  datasource:
    url: jdbc:kingbase8://localhost:54321/course_management
    username: SYSTEM
    password: 123456
    driver-class-name: com.kingbase8.Driver
```

### 4. 数据库表结构

项目包含以下主要表：

- **users**: 用户表
- **admin_users**: 管理员用户表
- **enterprise_users**: 企业用户表
- **normal_users**: 普通用户表
- **courses**: 课程表
- **course_history**: 课程历史表
- **questions**: 课程问答表
- **reviews**: 评论表
- **featured_courses**: 精选课程表

### 5. 外键约束

所有表都包含适当的外键约束：

- `questions.course_id` → `courses.id`
- `questions.user_id` → `users.id`
- `courses.instructor_id` → `users.id`
- `course_history.course_id` → `courses.id`
- `course_history.user_id` → `users.id`
- `reviews.course_id` → `courses.id`
- `reviews.user_id` → `users.id`
- `featured_courses.course_id` → `courses.id`

### 6. 索引

为常用查询字段创建了索引：

- 用户表：username, email, user_type, status
- 课程表：instructor_id, status, category, level, title
- 问答表：course_id, user_id, created_at
- 评论表：course_id, user_id, rating, status
- 课程历史表：user_id, course_id, status

### 7. 测试数据

`database_schema.sql` 包含测试数据：

- 3个用户（管理员、教师、学生）
- 3个课程
- 示例问答记录
- 示例评论

### 8. 启动项目

数据库配置完成后，运行：

```bash
mvn spring-boot:run
```

### 常见问题

1. **数据库不存在**: 确保已创建 `course_management` 数据库
2. **连接失败**: 检查端口、用户名、密码是否正确
3. **编码问题**: 确保数据库使用UTF-8编码
4. **权限问题**: 确保用户有足够的数据库权限 