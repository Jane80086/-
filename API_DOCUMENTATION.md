# 课程管理系统 API 文档

## 🚀 概述

本系统提供完整的课程管理功能，包括课程CRUD、AI优化、问答系统、推荐管理等。

**基础URL**: `http://localhost:8080`

## 📋 API 接口总览

### 1. 课程管理 API (`/api/course`)
### 2. MCP AI优化 API (`/api/mcp`)
### 3. 问答系统 API (`/api/qna`)
### 4. 推荐课程 API (`/api/featured`)
### 5. 搜索功能 API (`/api/search`)
### 6. 数据统计 API (`/api/stats`)

---

## 1. 课程管理 API

### 1.1 创建课程
**POST** `/api/course/create`

**请求体**:
```json
{
  "title": "Python编程入门",
  "description": "从零开始学习Python编程",
  "instructorId": 1,
  "price": 99.00,
  "duration": 120,
  "level": "BEGINNER",
  "category": "技术",
  "coverImage": "https://example.com/cover.jpg",
  "videoUrl": "https://example.com/video.mp4"
}
```

**响应**:
```json
{
  "code": "0",
  "msg": "课程创建成功",
  "data": {
    "id": 1,
    "title": "Python编程入门",
    "description": "从零开始学习Python编程",
    "instructorId": 1,
    "price": 99.00,
    "duration": 120,
    "level": "BEGINNER",
    "category": "技术",
    "status": "DRAFT",
    "createdTime": "2024-01-01T10:00:00"
  }
}
```

### 1.2 获取课程详情
**GET** `/api/course/{id}`

**响应**:
```json
{
  "code": "0",
  "msg": "操作成功",
  "data": {
    "id": 1,
    "title": "Python编程入门",
    "description": "从零开始学习Python编程",
    "instructorId": 1,
    "price": 99.00,
    "duration": 120,
    "level": "BEGINNER",
    "category": "技术",
    "status": "PUBLISHED",
    "coverImage": "https://example.com/cover.jpg",
    "videoUrl": "https://example.com/video.mp4",
    "createdTime": "2024-01-01T10:00:00",
    "updatedTime": "2024-01-01T10:00:00"
  }
}
```

### 1.3 更新课程
**PUT** `/api/course/{id}`

**请求体**: 同创建课程

### 1.4 删除课程
**DELETE** `/api/course/{id}`

### 1.5 获取课程列表
**GET** `/api/course/list`

### 1.6 搜索课程
**GET** `/api/course/search?keyword=Python`

### 1.7 提交课程审核
**POST** `/api/course/{id}/submit`

### 1.8 获取热门趋势
**GET** `/api/course/trends`

---

## 2. MCP AI优化 API

### 2.1 优化课程内容
**POST** `/api/mcp/optimize-course`

**参数**:
- `courseId` (Long, 必需): 课程ID
- `targetAudience` (String, 可选): 目标受众，默认"初学者"
- `courseType` (String, 可选): 课程类型，默认"技术"

### 2.2 分析课程SEO效果
**POST** `/api/mcp/analyze-seo`

**参数**:
- `title` (String, 必需): 课程标题
- `description` (String, 必需): 课程描述

### 2.3 生成课程模板
**GET** `/api/mcp/templates?courseType=技术&targetAudience=初学者`

### 2.4 直接优化内容
**POST** `/api/mcp/optimize-content`

**参数**:
- `originalTitle` (String, 必需): 原始标题
- `originalDescription` (String, 必需): 原始描述
- `targetAudience` (String, 可选): 目标受众
- `courseType` (String, 可选): 课程类型

### 2.5 获取MCP服务器状态
**GET** `/api/mcp/status`

---

## 3. 问答系统 API

### 3.1 提交问题
**POST** `/api/qna/ask`

**参数**:
- `courseId` (Long, 必需): 课程ID
- `content` (String, 必需): 问题内容
- `userId` (Long, 可选): 用户ID，默认1

### 3.2 获取课程问答列表
**GET** `/api/qna/course/{courseId}`

### 3.3 AI自动回复
**POST** `/api/qna/{id}/auto-reply`

### 3.4 手动回复问题
**POST** `/api/qna/{id}/reply`

**参数**:
- `replyContent` (String, 必需): 回复内容
- `replyUserId` (Long, 可选): 回复用户ID

### 3.5 删除问题
**DELETE** `/api/qna/{id}`

---

## 4. 推荐课程 API

### 4.1 获取推荐课程列表
**GET** `/api/featured/list`

### 4.2 设置课程推荐
**POST** `/api/featured/{courseId}/promote`

**参数**:
- `priority` (int, 可选): 优先级，默认1

### 4.3 取消课程推荐
**DELETE** `/api/featured/{courseId}`

### 4.4 更新推荐优先级
**PUT** `/api/featured/{courseId}/priority`

**参数**:
- `priority` (int, 必需): 新优先级

---

## 5. 搜索功能 API

### 5.1 搜索课程
**GET** `/api/search/courses?keyword=Python`

### 5.2 获取热门关键词
**GET** `/api/search/hot-keywords`

### 5.3 获取搜索建议
**GET** `/api/search/suggestions?query=Python`

### 5.4 获取相关课程推荐
**GET** `/api/search/related/{courseId}`

### 5.5 高级搜索
**GET** `/api/search/advanced?keyword=Python&category=技术&level=BEGINNER&status=PUBLISHED`

---

## 6. 数据统计 API

### 6.1 获取仪表板数据
**GET** `/api/stats/dashboard`

**响应**:
```json
{
  "code": "0",
  "msg": "操作成功",
  "data": {
    "totalCourses": 100,
    "totalViews": 5000,
    "totalUsers": 200,
    "hotTrends": ["Python", "Java", "JavaScript"],
    "recentActivity": []
  }
}
```

### 6.2 获取课程分析数据
**GET** `/api/stats/course/{id}/analytics`

### 6.3 获取用户观看历史
**GET** `/api/stats/user/{id}/history`

### 6.4 获取热门趋势
**GET** `/api/stats/trends`

### 6.5 获取收入统计
**GET** `/api/stats/revenue`

### 6.6 获取系统健康状态
**GET** `/api/stats/health`

---

## 🔧 通用响应格式

### 成功响应
```json
{
  "code": "0",
  "msg": "操作成功",
  "data": {}
}
```

### 失败响应
```json
{
  "code": "-1",
  "msg": "错误信息",
  "data": null
}
```

---

## 🧪 测试示例

### 使用curl测试

```bash
# 1. 创建课程
curl -X POST "http://localhost:8080/api/course/create" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Python编程入门",
    "description": "从零开始学习Python编程",
    "instructorId": 1,
    "price": 99.00,
    "duration": 120,
    "level": "BEGINNER",
    "category": "技术"
  }'

# 2. 获取课程列表
curl "http://localhost:8080/api/course/list"

# 3. 优化课程内容
curl -X POST "http://localhost:8080/api/mcp/optimize-content" \
  -d "originalTitle=Python编程课程" \
  -d "originalDescription=学习Python基础语法"

# 4. 搜索课程
curl "http://localhost:8080/api/search/courses?keyword=Python"

# 5. 获取系统状态
curl "http://localhost:8080/api/stats/health"
```

---

## 🚨 注意事项

1. **错误处理**: 所有接口都包含完整的错误处理
2. **参数验证**: 必需参数会进行验证
3. **响应格式**: 统一使用Result格式
4. **状态码**: 使用标准HTTP状态码
5. **数据格式**: 请求和响应都使用JSON格式

---

## 🔄 下一步计划

- [ ] 添加分页支持
- [ ] 实现文件上传功能
- [ ] 添加缓存机制
- [ ] 实现实时通知
- [ ] 添加API限流
- [ ] 完善错误码体系 