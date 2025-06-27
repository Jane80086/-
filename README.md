# 智能课程管理与推荐系统

## 项目简介
本项目为一套基于 Spring Boot + MCP 智能推荐 + MinIO 文件存储的课程管理平台，支持课程管理、智能推荐、问答区AI答疑、用户体系、管理后台等全流程。

---

## 主要功能模块
- **课程管理**：课程创建、编辑、审核、发布、收藏、点赞、评论等
- **智能推荐系统**：课程热度分析、个性化推荐、首页榜单、推荐理由生成
- **问答区**：支持AI自动答疑、人工回复、点赞、采纳、举报、分页等
- **文件/MinIO**：课程资料上传、下载、预签名URL、MinIO健康检查
- **用户体系**：单表继承，支持普通用户、企业用户、管理员
- **管理后台**：用户/课程/评论/问答审核、封禁、分页等

---

## 关键API接口说明（部分示例）

### 课程相关
- `POST /api/course/{id}/favorite` 课程收藏/取消收藏
- `POST /api/course/{id}/like` 课程点赞/取消点赞
- `POST /api/course/{id}/comment` 课程发表评论
- `GET /api/course/{id}/comments` 课程评论分页

### 推荐系统
- `POST /api/recommend/analyzeCourseHeat` 课程热度分析
- `POST /api/recommend/generateRecommendations` 生成个性化推荐
- `POST /api/recommend/updateFeaturedCourses` 更新首页推荐

### 问答区
- `POST /api/qna/ask` 提交问题
- `GET /api/qna/course/{courseId}/page` 分页获取问答
- `POST /api/qna/{id}/like` 点赞
- `POST /api/qna/{id}/accept` 采纳答案
- `POST /api/qna/{id}/report` 举报
- `POST /api/qna/{id}/manual-reply` 人工回复

### 用户体系
- `POST /user/add` 添加用户
- `POST /user/update` 更新用户
- `GET /api/admin/users` 用户分页
- `POST /api/admin/user/{id}/ban` 封禁用户

### 管理后台
- `GET /api/admin/courses` 课程分页
- `POST /api/admin/course/{id}/review` 课程审核
- `POST /api/admin/comment/{id}/review` 评论审核
- `POST /api/admin/qna/{id}/review` 问答审核

### 文件/MinIO
- `POST /api/file/upload` 上传文件
- `GET /api/file/download` 下载文件
- `GET /api/file/presigned-url` 获取预签名URL

---

## 部署与运行
1. 配置数据库（见 `DATABASE_SETUP.md`）并执行 `database_schema.sql`。
2. 配置 MinIO 并启动（见 `MINIO_CONFIGURATION.md`）。
3. 修改 `application.yml` 数据库/MinIO等参数。
4. 编译并运行：
   ```sh
   mvn clean install
   mvn spring-boot:run
   ```
5. 访问接口文档或前端页面。

---

## 数据库结构简要
- `users`：单表继承，区分普通/企业/管理员
- `courses`、`questions`、`comments`、`course_history`、`featured_courses` 等
- 详见 `database_schema.sql`

---

## 常见问题
- **表缺失/字段缺失**：请同步执行最新 `database_schema.sql` 或手动补字段
- **MinIO/数据库连接失败**：检查配置和服务状态
- **接口404/401**：检查权限、路径、Token

---

## 联系方式
如有问题或建议，请联系项目维护者或提交 Issue。