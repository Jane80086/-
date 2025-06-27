# MinIO 对象存储配置文档

## 🚀 概述

MinIO是一个高性能的对象存储服务，用于存储课程文件、图片、视频等。本系统已集成MinIO，提供完整的文件管理功能。

## 📋 配置内容

### 1. 依赖配置 (`pom.xml`)

```xml
<!-- MinIO对象存储 -->
<dependency>
    <groupId>io.minio</groupId>
    <artifactId>minio</artifactId>
    <version>8.5.7</version>
</dependency>
<!-- 文件上传支持 -->
<dependency>
    <groupId>commons-fileupload</groupId>
    <artifactId>commons-fileupload</artifactId>
    <version>1.5</version>
</dependency>
```

### 2. 应用配置 (`application.yml`)

```yaml
minio:
  endpoint: http://localhost:9000
  access-key: minioadmin
  secret-key: minioadmin
  bucket: course-files
```

### 3. MinIO配置类 (`MinioConfig.java`)

- 自动创建MinIO客户端
- 自动初始化存储桶
- 支持连接检查和状态监控

## 🔧 安装和启动

### 1. 下载MinIO

访问 [MinIO官网](https://min.io/download) 下载Windows版本。

### 2. 安装MinIO

1. 下载 `minio.exe`
2. 将 `minio.exe` 放到系统PATH目录中（如 `C:\Windows\System32`）
3. 或者创建环境变量指向MinIO安装目录

### 3. 启动MinIO

#### 方法1: 使用启动脚本
```bash
start_minio.bat
```

#### 方法2: 手动启动
```bash
# 创建数据目录
mkdir minio-data

# 启动MinIO服务器
minio server minio-data --console-address ":9001"
```

### 4. 访问MinIO

- **API地址**: http://localhost:9000
- **控制台地址**: http://localhost:9001
- **默认用户名**: minioadmin
- **默认密码**: minioadmin

## 📁 存储桶结构

```
course-files/
├── course-covers/     # 课程封面图片
├── course-videos/     # 课程视频文件
├── user-avatars/      # 用户头像
└── documents/         # 文档文件
```

## 🎯 功能特性

### 1. 文件上传
- ✅ 支持拖拽上传
- ✅ 支持多文件上传
- ✅ 自动生成唯一文件名
- ✅ 文件类型验证
- ✅ 上传进度显示

### 2. 文件管理
- ✅ 文件下载
- ✅ 文件删除
- ✅ 文件列表
- ✅ 文件存在性检查
- ✅ 预签名URL生成

### 3. 业务集成
- ✅ 课程封面上传
- ✅ 课程视频上传
- ✅ 用户头像上传
- ✅ 文档文件上传

## 🧪 测试方法

### 1. 使用测试页面
访问: `http://localhost:8080/minio-test.html`

功能包括：
- 拖拽文件上传
- 课程封面上传
- 课程视频上传
- 用户头像上传
- 文档上传
- MinIO状态检查
- 文件列表查看

### 2. 使用API接口

#### 上传课程封面
```bash
curl -X POST "http://localhost:8080/api/file/upload/course-cover" \
  -F "file=@cover.jpg"
```

#### 上传课程视频
```bash
curl -X POST "http://localhost:8080/api/file/upload/course-video" \
  -F "file=@video.mp4"
```

#### 检查MinIO状态
```bash
curl "http://localhost:8080/api/file/status"
```

#### 获取文件列表
```bash
curl "http://localhost:8080/api/file/list?folder=course-covers"
```

### 3. 使用JavaScript

```javascript
// 上传文件
const formData = new FormData();
formData.append('file', fileInput.files[0]);

fetch('http://localhost:8080/api/file/upload/course-cover', {
    method: 'POST',
    body: formData
})
.then(response => response.json())
.then(data => console.log(data));
```

## 📋 API接口列表

### 文件上传接口
- `POST /api/file/upload/course-cover` - 上传课程封面
- `POST /api/file/upload/course-video` - 上传课程视频
- `POST /api/file/upload/user-avatar` - 上传用户头像
- `POST /api/file/upload/document` - 上传文档
- `POST /api/file/upload` - 通用文件上传

### 文件管理接口
- `GET /api/file/download` - 下载文件
- `DELETE /api/file/delete` - 删除文件
- `GET /api/file/list` - 获取文件列表
- `GET /api/file/exists` - 检查文件是否存在
- `GET /api/file/presigned-url` - 获取预签名URL
- `GET /api/file/status` - 获取MinIO状态

## 🔒 安全配置

### 1. 生产环境配置
```yaml
minio:
  endpoint: https://your-minio-server.com
  access-key: your-access-key
  secret-key: your-secret-key
  bucket: your-bucket-name
```

### 2. 访问控制
- 设置适当的存储桶策略
- 配置CORS规则
- 使用预签名URL进行临时访问

### 3. 文件验证
- 文件大小限制
- 文件类型验证
- 文件名安全检查

## 🚨 常见问题

### 1. MinIO连接失败
**问题**: 无法连接到MinIO服务器
**解决**: 
- 检查MinIO是否正在运行
- 验证端口9000是否被占用
- 检查防火墙设置

### 2. 文件上传失败
**问题**: 文件上传返回错误
**解决**:
- 检查存储桶是否存在
- 验证访问权限
- 检查文件大小限制

### 3. 跨域问题
**问题**: 前端无法上传文件
**解决**:
- 确保已配置CORS
- 检查API接口路径
- 验证请求头设置

## 📊 性能优化

### 1. 文件分片上传
- 支持大文件分片上传
- 断点续传功能
- 上传进度监控

### 2. 缓存策略
- 静态文件缓存
- CDN集成
- 预签名URL缓存

### 3. 存储优化
- 文件压缩
- 图片缩略图生成
- 存储桶生命周期管理

## 🔄 监控和维护

### 1. 健康检查
```bash
curl "http://localhost:8080/api/file/status"
```

### 2. 存储桶监控
- 存储空间使用情况
- 文件数量统计
- 访问日志分析

### 3. 备份策略
- 定期数据备份
- 跨区域复制
- 灾难恢复计划

## 📚 相关文档

- [MinIO官方文档](https://docs.min.io/)
- [MinIO Java SDK](https://docs.min.io/docs/java-client-api-reference.html)
- [Spring Boot文件上传](https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-file-upload)

## 🎯 最佳实践

1. **文件命名**: 使用UUID生成唯一文件名
2. **目录结构**: 按业务类型组织文件目录
3. **访问控制**: 使用预签名URL进行临时访问
4. **错误处理**: 完善的异常处理和日志记录
5. **性能监控**: 定期检查上传下载性能
6. **安全防护**: 文件类型验证和大小限制 