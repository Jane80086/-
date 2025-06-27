# 智能课程管理与推荐系统

## 📚 文档导航
- [README（总览）](README.md)

---

## 1. 项目简介与快速开始
本项目是一个集成了智能推荐、AI问答、课程管理、用户体系、MinIO文件存储、自动化运维的全栈课程平台，支持企业/普通/管理员多角色，适用于高校、培训机构等多场景。

## 主要功能
- 课程智能推荐与热度分析
- AI自动答疑与人工答复分离
- 课程收藏、点赞、评论、榜单推送
- 管理后台：用户/课程/评论/问答审核、封禁、分页
- 单表继承用户体系（Admin/Enterprise/Normal）
- MinIO分布式文件存储
- API接口自动补全与文档
- 一键自动化脚本（环境、启动、测试、推送、数据库同步等）

## 技术栈
- **后端**：Spring Boot (Java)、MyBatis-Plus、JJWT、MinIO SDK
- **智能推荐/AI问答**：Python FastMCP、Uvicorn、AnyIO
- **数据库**：MySQL（或兼容数据库）
- **对象存储**：MinIO
- **自动化/运维**：Windows批处理、Python

## 目录结构
```
course_manager/
  ├─ src/main/java/com/cemenghui/course/    # Java后端
  ├─ python-sdk/                            # Python智能推荐/AI服务
  ├─ run_mcp_server.py                      # MCP推荐服务启动
  ├─ start_simple_server.py                 # MCP HTTP服务启动
  ├─ requirements.txt / pom.xml             # 依赖
  ├─ *.bat / manage.py                      # 自动化脚本
  └─ README.md                              # 项目说明
```

## 快速开始
1. **一键初始化环境**
   ```
   python manage.py setup
   ```
2. **一键启动所有服务**
   ```
   python manage.py start
   ```
3. **访问服务**
   - Java后端API: http://localhost:8080/
   - Python推荐API: http://localhost:8000/mcp
   - MinIO控制台: http://localhost:9000/
4. **一键停止服务**
   ```
   python manage.py stop
   ```
5. **一键测试/推送/数据库同步**
   ```
   python manage.py test
   python manage.py push
   python manage.py syncdb
   ```

## 主要API接口（部分示例）
- `/api/courses/recommend`  智能推荐课程
- `/api/qna/auto-reply`     AI自动答疑
- `/api/courses/like`       课程点赞/收藏
- `/api/comments`           评论增删查
- `/api/admin/users`        用户管理/审核/封禁
- `/mcp`                    Python智能推荐/问答接口

> 详细API请参考 `API_DOCUMENTATION.md` 或 Swagger 文档

## 数据库结构（MySQL示例）
- `users`：单表继承，含所有用户字段
- `courses`：课程表，含likeCount、favoriteCount等
- `comments`：评论表
- `questions`：问答区表
- 详见 `database_schema.sql`

## 环境依赖
- Python 3.8+
- Java 17+
- MySQL 5.7+/8.0+
- MinIO
- pip、maven、git

## 常见问题
- **MinIO无法启动**：请手动下载minio.exe并放到根目录，或检查端口占用。
- **JJWT依赖冲突**：已统一到0.11.5，确保pom.xml一致。
- **数据库缺表/字段**：请用 `python manage.py syncdb` 导出/同步结构。
- **MCP服务端口冲突**：可修改脚本或配置文件端口。

## 自动化脚本说明
| 脚本                | 作用                         |
|---------------------|------------------------------|
| setup_all.bat       | 一键初始化环境               |
| start_all.bat       | 一键启动所有服务             |
| stop_all.bat        | 一键停止所有服务             |
| test_all.bat        | 一键运行所有测试             |
| push_all.bat        | 一键推送代码                 |
| sync_db_schema.bat  | 一键导出数据库结构           |
| manage.py           | 命令行助手（setup/start/stop/test/push/syncdb）|

## 联系方式
- 作者：CEMenghui
- 邮箱：your@email.com
- Issues/PR：欢迎在GitHub提交

---
如需定制化自动化、文档、API补全等，欢迎随时联系！

---

## 2. API接口文档
// ... 粘贴 API_DOCUMENTATION.md 全部内容 ...

---

## 3. 数据库配置与初始化
### 3.1 数据库连接与性能说明
// ... 粘贴 DATABASE_CONFIGURATION.md 全部内容 ...

### 3.2 数据库初始化与表结构
// ... 粘贴 DATABASE_SETUP.md 全部内容 ...

---

## 4. MinIO对象存储配置
// ... 粘贴 MINIO_CONFIGURATION.md 全部内容 ...

---

## 5. 跨域（CORS）配置说明
// ... 粘贴 CORS_CONFIGURATION.md 全部内容 ...

---

## 6. 开发帮助与官方文档
// ... 粘贴 HELP.md 全部内容 ...