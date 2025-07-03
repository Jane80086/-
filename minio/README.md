# MinIO 对象存储服务

## 简介
MinIO 是一个高性能的对象存储服务，兼容 Amazon S3 API。本项目使用 MinIO 来存储会议封面图片等文件。

## 安装步骤

### 1. 下载 MinIO
1. 访问 https://min.io/download
2. 下载 Windows 版本的 MinIO Server
3. 将下载的 `minio.exe` 放到 `minio/` 目录下

### 2. 启动 MinIO 服务
双击运行 `start-minio.bat` 文件，或在命令行中执行：
```bash
cd minio
minio.exe server data --console-address :9001
```

### 3. 访问 MinIO 控制台
- 控制台地址：http://localhost:9001
- 默认账号：minioadmin
- 默认密码：minioadmin

### 4. 创建存储桶
1. 登录 MinIO 控制台
2. 点击 "Create Bucket"
3. 输入存储桶名称：`meeting-files`
4. 点击 "Create Bucket" 完成创建

## 配置说明

### 后端配置
在 `application.yml` 中已配置：
```yaml
minio:
  endpoint: http://localhost:9000
  accessKey: minioadmin
  secretKey: minioadmin
  bucket: meeting-files
```

### 前端配置
前端已集成文件上传组件，支持：
- 拖拽上传
- 文件预览
- 上传进度显示
- 文件类型验证（仅支持图片）
- 文件大小限制（最大 10MB）

## 使用流程

### 1. 启动服务
1. 启动 MinIO 服务：运行 `start-minio.bat`
2. 启动后端服务：在 `demo/` 目录运行 `mvn spring-boot:run`
3. 启动前端服务：在 `city1/` 目录运行 `npm run dev`

### 2. 测试文件上传
1. 访问前端应用：http://localhost:5173
2. 登录系统
3. 创建新会议时，使用文件上传组件上传封面图片
4. 查看上传的文件是否出现在 MinIO 控制台中

## 文件存储结构
```
meeting-files/
├── uuid1.jpg          # 会议封面图片
├── uuid2.png          # 会议封面图片
└── uuid3.gif          # 会议封面图片
```

## 注意事项
1. 确保 MinIO 服务在应用启动前已启动
2. 文件上传需要用户登录并具有有效 token
3. 只支持图片文件格式（jpg、jpeg、png、gif）
4. 单个文件大小限制为 10MB
5. 上传的文件会自动生成唯一文件名，避免冲突

## 故障排除

### 常见问题
1. **MinIO 启动失败**
   - 检查端口 9000 和 9001 是否被占用
   - 确保有足够的磁盘空间

2. **文件上传失败**
   - 检查 MinIO 服务是否正常运行
   - 检查网络连接
   - 检查文件格式和大小是否符合要求

3. **无法访问 MinIO 控制台**
   - 检查防火墙设置
   - 确认端口 9001 未被占用

### 日志查看
- MinIO 日志：查看控制台输出
- 应用日志：查看 `demo/logs/meeting-system.log` 