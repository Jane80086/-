# 前端与后端API集成修改总结

## 概述
根据后端Spring Boot项目的API结构，对前端Vue.js代码进行了相应的修改，确保前后端数据交互的正确性。

## 主要修改内容

### 1. 服务层修改 (meetingService.js)

#### API响应格式处理
- 修改了响应拦截器，统一处理后端`ApiResponse`格式
- 移除了对旧版本`success`字段的支持，专注于`code`字段处理

#### 登录接口调整
- 修改登录成功后的数据处理逻辑
- 正确提取`response.data.data.token`和`response.data.data.user`
- 确保token和用户信息正确存储到localStorage

#### API端点调整
- **删除会议**: 参数名从`id`改为`meeting_id`，`confirmDelete`改为`confirm_delete`
- **获取会议详情**: 从GET请求改为POST请求，参数通过请求体传递
- **获取待审核会议**: 从GET请求改为POST请求
- **审核记录接口**: 统一使用POST请求，路径调整为`/by-reviewer`、`/by-creator`、`/by-meeting`

#### 图片上传处理
- 保持FormData格式不变
- 确保正确处理后端返回的图片URL

### 2. 登录组件修改 (LoginView.vue)

#### 登录响应处理
- 增强了对后端`ApiResponse`格式的处理
- 添加了数据格式验证，确保token和用户信息存在
- 改进了错误处理，提供更详细的错误信息

### 3. 会议列表组件修改 (MeetingList.vue)

#### 图片上传响应处理
- 修改图片上传成功后的数据处理逻辑
- 正确提取`response.data.data`作为图片URL
- 改进了错误处理，提供更具体的错误信息

### 4. 主页面组件修改 (HomeView.vue)

#### 图片上传处理
- 与MeetingList组件保持一致的修改
- 确保新建会议时的图片上传功能正常工作

## 后端API结构对应关系

### 用户相关
- **登录**: `POST /api/auth/login`
- **获取用户信息**: `GET /api/auth/profile`
- **登出**: `POST /api/auth/logout`

### 会议相关
- **创建会议**: `POST /api/meeting/create`
- **更新会议**: `POST /api/meeting/update`
- **删除会议**: `POST /api/meeting/delete`
- **查询会议列表**: `POST /api/meeting/list`
- **获取会议详情**: `POST /api/meeting/detail`
- **获取待审核会议**: `POST /api/meeting/pending`
- **审核会议**: `POST /api/meeting/review`
- **上传会议图片**: `POST /api/meeting/uploadImage`

### 审核记录相关
- **获取审核人记录**: `POST /api/meeting/review/records/by-reviewer`
- **获取创建人记录**: `POST /api/meeting/review/records/by-creator`
- **获取会议审核记录**: `POST /api/meeting/review/records/by-meeting`

## 数据格式对应

### 后端ApiResponse格式
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    // 具体数据
  }
}
```

### 登录响应格式
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "token": "jwt_token_string",
    "refreshToken": "refresh_token_string",
    "user": {
      "id": 1,
      "username": "admin",
      "realName": "管理员",
      "userType": "ADMIN",
      // 其他用户信息
    }
  }
}
```

### 会议对象格式
```json
{
  "id": 1,
  "meetingName": "会议名称",
  "startTime": "2024-01-01T10:00:00",
  "endTime": "2024-01-01T12:00:00",
  "creator": "admin",
  "meetingContent": "会议内容",
  "status": 0,
  "reviewer": "admin",
  "reviewTime": "2024-01-01T09:00:00",
  "reviewComment": "审核意见",
  "imageUrl": "http://localhost:9000/bucket/image.jpg",
  "createTime": "2024-01-01T08:00:00",
  "updateTime": "2024-01-01T08:00:00"
}
```

## 权限控制

### 用户类型
- **ADMIN**: 管理员，可以审核、删除所有会议
- **ENTERPRISE**: 企业用户，可以创建、编辑、删除自己的会议
- **NORMAL**: 普通用户，只能查看会议

### 会议状态
- **0**: 待审核
- **1**: 已通过
- **2**: 已拒绝

## 注意事项

1. **CORS配置**: 后端已配置允许前端域名访问
2. **Token认证**: 所有需要认证的接口都通过Authorization头传递JWT token
3. **错误处理**: 统一处理后端返回的错误信息
4. **文件上传**: 支持图片文件上传，大小限制10MB
5. **时间格式**: 使用ISO 8601格式处理时间

## 测试建议

1. 测试不同用户类型的登录和权限
2. 测试会议的增删改查功能
3. 测试审核流程
4. 测试图片上传功能
5. 测试错误处理和异常情况 