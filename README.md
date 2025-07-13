# 课程管理系统完整说明文档

---

## 📋 项目概述

本系统为多模块课程管理平台，包含注册、登录、用户管理、课程、新闻、会议、系统等子模块，前后端分离，支持统一用户体系和金仓数据库。

### 🎯 核心功能
- **课程管理** - 课程创建、编辑、审核、播放
- **AI问答** - 基于Dify的智能问答系统
- **会议管理** - 会议创建、审核、管理
- **新闻管理** - 新闻发布、管理
- **用户管理** - 统一用户体系，支持多种用户类型
- **文件管理** - MinIO对象存储集成

---

## 🛠️ 技术栈

### 后端技术
- **Java 11+** - 核心开发语言
- **Spring Boot** - 应用框架
- **MyBatis Plus** - ORM框架
- **Maven** - 项目管理
- **KingbaseES** - 金仓数据库
- **Redis** - 缓存服务
- **MinIO** - 对象存储

### 前端技术
- **Vue 3** - 前端框架
- **Element Plus** - UI组件库
- **Vite** - 构建工具
- **Axios** - HTTP客户端

### AI服务
- **Dify** - AI问答服务
- **Python FastAPI** - AI服务后端

---

## 🚀 快速开始

### 1. 环境要求
- JDK 11 及以上
- Node.js 16+、npm/yarn
- KingbaseES（金仓数据库）
- Redis
- MinIO

### 2. 数据库初始化
```sql
-- 创建数据库
CREATE DATABASE course_manager;

-- 执行统一数据库脚本
\i course_manager_kingbase_complete.sql
```

### 3. 启动服务

#### 后端服务
```bash
# 启动主应用
cd main-app && mvn spring-boot:run

# 启动课程模块
cd course && mvn spring-boot:run

# 启动会议模块
cd meeting && mvn spring-boot:run

# 启动新闻模块
cd news && mvn spring-boot:run

# 启动系统模块
cd system && mvn spring-boot:run
```

#### AI服务
```bash
# 启动AI问答服务
cd ai && uvicorn main:app --host 0.0.0.0 --port 8090 --reload
```

#### 前端服务
```bash
# 启动前端开发服务器
cd fronted && npm run dev
```

---

## 📁 项目结构

```
course_manager/
├── main-app/                 # 主应用模块
├── course/                   # 课程管理模块
├── meeting/                  # 会议管理模块
├── news/                     # 新闻管理模块
├── system/                   # 系统管理模块
├── fronted/                  # 前端应用
├── ai/                       # AI问答服务
├── minio/                    # MinIO配置
└── docs/                     # 文档目录
```

---

## 🔧 配置说明

### 数据库配置
所有模块统一使用 `course_manager` 数据库：

```yaml
spring:
  datasource:
    url: jdbc:kingbase8://localhost:54321/course_manager
    username: system
    password: 123456
    driver-class-name: com.kingbase8.Driver
```

### MinIO配置
```yaml
minio:
  endpoint: http://localhost:9000
  accessKey: minioadmin
  secretKey: minioadmin
  bucket: course-files
```

### AI服务配置
```python
# config.py
DIFY_API_KEY = "your-dify-api-key"
DIFY_BASE_URL = "http://localhost:8088/v1"
MCP_API_URL = "http://localhost:6277/ai-qa"
```

---

## 🎯 核心功能详解

### 1. 课程管理
- **课程创建** - 支持视频上传、封面设置
- **课程审核** - 管理员审核流程
- **课程播放** - HTML5视频播放器，支持进度保存
- **AI优化** - 自动优化课程标题和描述
- **学习记录** - 学习进度跟踪和统计

### 2. AI问答系统
- **智能问答** - 基于Dify的课程相关问题回答
- **历史记录** - 问答历史管理
- **多后端支持** - 支持Dify和MCP后端
- **编码优化** - 解决中文编码问题

### 3. 会议管理
- **会议创建** - 支持图片上传和内容编辑
- **审核流程** - 管理员审核机制
- **状态管理** - 待审核、已通过、已拒绝状态
- **历史记录** - 审核历史追踪

### 4. 用户管理
- **统一用户体系** - 支持管理员、企业用户、普通用户
- **权限管理** - 基于用户类型的权限控制
- **企业信息** - 企业工商信息管理
- **第三方账户** - 第三方平台账户绑定

---

## 🔍 API接口

### 课程相关API
- `GET /api/course/list` - 获取课程列表
- `POST /api/course/create` - 创建课程
- `GET /api/course/{id}` - 获取课程详情
- `POST /api/course/{id}/submit-review` - 提交审核

### AI问答API
- `POST /api/ai-questions/ask` - 提交AI问题
- `GET /api/ai-questions/course/{courseId}` - 获取课程问题列表

### 会议相关API
- `GET /api/meeting/list` - 获取会议列表
- `POST /api/meeting/create` - 创建会议
- `POST /api/meeting/{id}/review` - 审核会议

### 文件上传API
- `POST /api/file/upload` - 文件上传
- `GET /api/file/presigned-url` - 获取预签名URL

---

## 🛡️ 安全配置

### JWT认证
```yaml
app:
  jwt:
    secret: your-secret-key-here
    expiration: 86400000
    refresh-expiration: 604800000
```

### 跨域配置
```java
@Configuration
public class CorsConfig {
    @Bean
    public CorsFilter corsFilter() {
        // 允许前端域名访问
    }
}
```

---

## 📊 数据库设计

### 统一数据库架构
- **用户表** - 统一用户信息管理
- **课程表** - 课程基本信息
- **会议表** - 会议信息管理
- **新闻表** - 新闻内容管理
- **审核表** - 统一审核记录
- **日志表** - 操作日志记录

### 表关联关系
- 所有涉及用户的表都通过`user_id`字段关联
- 外键约束确保数据一致性
- 逻辑删除支持数据安全

---

## 🔧 故障排除

### 常见问题

#### 1. 数据库连接失败
- 检查数据库服务是否启动
- 验证连接参数是否正确
- 确认数据库用户权限

#### 2. MinIO连接失败
- 检查MinIO服务是否启动
- 验证accessKey和secretKey
- 确认bucket是否存在

#### 3. AI服务异常
- 检查Dify服务是否可用
- 验证API密钥是否正确
- 确认网络连接正常

#### 4. 前端超时错误
- 检查后端服务是否启动
- 验证代理配置是否正确
- 确认API路径是否正确

### 日志查看
```bash
# 查看应用日志
tail -f logs/application.log

# 查看错误日志
tail -f logs/error.log
```

---

## 📈 性能优化

### 数据库优化
- 为常用查询字段创建索引
- 使用连接池优化数据库连接
- 定期清理无用数据

### 缓存策略
- 使用Redis缓存热点数据
- 实现合理的缓存过期策略
- 避免缓存穿透和雪崩

### 前端优化
- 使用CDN加速静态资源
- 实现懒加载和分页
- 优化图片和视频加载

---

## 🔄 部署指南

### 生产环境部署
1. **环境准备** - 安装JDK、Node.js、数据库等
2. **数据库初始化** - 执行数据库脚本
3. **配置文件** - 修改生产环境配置
4. **服务启动** - 启动所有后端服务
5. **前端部署** - 构建并部署前端应用
6. **监控配置** - 配置日志和监控

### Docker部署
```dockerfile
# 后端服务Dockerfile示例
FROM openjdk:11-jre-slim
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

---

## 📞 技术支持

### 文档资源
- `API_TEST_GUIDE.md` - API测试指南
- `COURSE_PLAYER_README.md` - 课程播放功能说明
- `database_migration_guide.md` - 数据库迁移指南

### 联系方式
- 项目维护者：[维护者信息]
- 技术支持：[技术支持邮箱]
- 问题反馈：[问题反馈地址]

---

## 📝 更新日志

### v1.0.0 (2024-07-13)
- ✅ 完成多模块整合
- ✅ 实现统一用户体系
- ✅ 集成AI问答功能
- ✅ 优化数据库架构
- ✅ 完善前端功能

### 主要更新
- 数据库从多库整合为单库
- System模块使用main-app统一User实体
- 集成Dify AI问答服务
- 优化MinIO文件存储
- 完善课程播放功能

---

## 📄 许可证

本项目采用 [许可证类型] 许可证，详情请查看 LICENSE 文件。

---

> 本文档整合了项目的所有重要信息，包括架构设计、部署指南、故障排除等，作为项目的权威说明文档。


