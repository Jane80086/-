# 课程学习平台

一个基于Spring Boot + Vue 3 + Element Plus的现代化课程学习平台，支持三种用户角色：普通用户、企业用户和超级管理员。

## 功能特性

### 🎯 核心功能
- **多角色用户系统**：支持普通用户、企业用户、超级管理员三种角色
- **智能推荐系统**：基于AI算法的个性化课程推荐
- **课程管理**：完整的课程CRUD操作，支持文件上传
- **学习进度跟踪**：实时监控学习进度和完成情况
- **数据分析**：丰富的统计图表和数据分析功能

### 👥 用户角色功能

#### 普通用户 (NORMAL)
- 浏览和搜索课程
- 个人学习进度管理
- AI智能推荐课程
- 收藏喜欢的课程
- 个人资料管理

#### 企业用户 (ENTERPRISE)
- 企业员工管理
- 企业课程创建和管理
- 员工学习数据分析
- 培训计划制定
- 学习报告生成
- AI洞察和建议

#### 超级管理员 (ADMIN)
- 用户管理（增删改查、批量操作）
- 课程审核和管理
- 系统监控和日志
- AI配置管理
- 数据备份和恢复
- 权限管理

### 🤖 AI功能
- **个性化推荐**：基于用户学习历史和偏好
- **热门推荐**：基于课程热度和评分
- **趋势分析**：识别热门和新兴课程
- **智能洞察**：为企业提供学习优化建议
- **课程优化**：AI辅助课程标题和描述优化

## 技术栈

### 后端
- **Spring Boot 2.7+**：主框架
- **MyBatis Plus**：ORM框架
- **KingbaseES**：数据库
- **MinIO**：对象存储
- **Spring Security**：安全认证
- **Swagger**：API文档

### 前端
- **Vue 3**：前端框架
- **Element Plus**：UI组件库
- **Vue Router**：路由管理
- **Axios**：HTTP客户端
- **ECharts**：图表库

### AI服务
- **Python MCP**：AI服务框架
- **推荐算法**：协同过滤、内容推荐
- **数据分析**：用户行为分析、课程热度分析

## 项目结构

```
├── course_manager/          # 后端项目
│   ├── src/main/java/      # Java源码
│   ├── src/main/resources/ # 配置文件
│   ├── ai/                 # AI服务
│   └── sql/                # 数据库脚本
├── fronted/                # 前端项目
│   ├── src/
│   │   ├── components/     # 组件
│   │   ├── views/          # 页面
│   │   ├── api/            # API接口
│   │   └── router/         # 路由配置
│   └── package.json
└── minio/                  # 文件存储
```

## 快速开始

### 环境要求
- JDK 8+
- Node.js 16+
- Python 3.8+
- KingbaseES 数据库
- MinIO 对象存储

### 1. 启动数据库
```sql
-- 创建数据库
CREATE DATABASE course_db;

-- 执行初始化脚本
\i course_manager/sql/database_schema.sql
```

### 2. 启动MinIO
```bash
# 启动MinIO服务
cd course_manager/minio
./start-minio.bat
```

### 3. 启动后端
```bash
cd course_manager
mvn spring-boot:run
```

### 4. 启动前端
```bash
cd fronted
npm install
npm run dev
```

### 5. 启动AI服务（可选）
```bash
cd course_manager/ai
python manage.py start_all
```

## 访问地址

- **前端应用**：http://localhost:5173
- **后端API**：http://localhost:8080
- **API文档**：http://localhost:8080/swagger-ui.html
- **MinIO控制台**：http://localhost:9001

## 用户角色切换

在系统右上角可以切换不同的用户角色来体验不同功能：

1. **普通用户**：体验个人学习功能
2. **企业用户**：体验企业管理功能
3. **超级管理员**：体验系统管理功能

## 主要功能模块

### 课程管理
- 课程创建、编辑、删除
- 课程分类管理
- 课程搜索和筛选
- 课程推荐算法

### 用户管理
- 用户注册、登录
- 用户信息管理
- 权限控制
- 用户行为分析

### 学习管理
- 学习进度跟踪
- 学习历史记录
- 学习统计报告
- 学习效果评估

### 企业功能
- 员工管理
- 培训计划
- 学习分析
- 绩效评估

### 系统管理
- 系统监控
- 日志管理
- 数据备份
- 配置管理

## 开发指南

### 添加新功能
1. 在后端添加Controller、Service、DAO层
2. 在前端添加对应的页面和组件
3. 更新路由配置
4. 添加API接口调用

### 自定义AI推荐
1. 修改 `ai/mcp_course_recommendation_server.py`
2. 调整推荐算法参数
3. 重启AI服务

### 部署说明
1. 打包前端：`npm run build`
2. 打包后端：`mvn clean package`
3. 部署到服务器
4. 配置数据库连接
5. 启动服务

## 贡献指南

1. Fork 项目
2. 创建功能分支
3. 提交代码
4. 创建 Pull Request

## 许可证

MIT License

## 联系方式

如有问题或建议，请提交 Issue 或联系开发团队。