# 全量自动修正建议

## 1. MinIO 启动命令（Windows）
- 推荐命令：
```powershell
Start-Process -FilePath "D:\Microsoft\minio.exe"
```
或直接运行：
```powershell
D:\Microsoft\minio.exe server D:\minio-data
```

## 2. 前端修正建议
### 2.1 import 路径
- 确保所有 `@/store/user` 的引用都指向 `fronted/src/store/user.js`。
- Meeting 相关页面、AdminLayout、MeetingDetailView、Meeting-HomeView、userStore 的 import 路径全部检查。

### 2.2 路由配置
- `fronted/src/router/index.js`、`meeting-index.js` 路由配置需确保所有 admin/meeting 相关页面路径前缀为 `/admin/meeting`，避免 sidebar/导航丢失。
- Meeting 相关页面建议全部放在 `/admin/meeting` 下，避免角色混用。

### 2.3 store/user 状态
- 所有 layout、Meeting 相关页面、AdminLayout、MeetingDetailView、Meeting-HomeView 页面挂载时调用 `userStore.initUser()`，保证刷新后用户状态恢复。

### 2.4 axios 超时
- `fronted/src/utils/request.js`、`api/*.js` 统一超时建议 60000ms。

### 2.5 el-upload 参数
- `UploadMedia.vue` 组件 `:action`、`:headers`、`:data` 参数需与后端接口一致。
- 上传接口建议统一为 `/api/file/upload`，后端返回 url 字段。

### 2.6 组件引用
- 检查所有 `MeetingDetailView.vue`、`Meeting-HomeView.vue`、`AdminLayout.vue`、`UserDashboard.vue`、`CoursePlay.vue` 等组件的 import 路径。

### 2.7 导航跳转
- Meeting 相关页面返回按钮统一跳转 `/admin/meeting`。
- AdminLayout 菜单跳转全部以 `/admin/` 开头。

## 3. 后端修正建议
### 3.1 MinIO 配置
- application.yml/application.properties 中 MinIO endpoint、accessKey、secretKey、bucketName 配置正确。
- MinIO 端口与前端一致（如 9000）。

### 3.2 文件上传接口
- Spring Boot Controller 路径建议为 `/api/file/upload`，接收 `MultipartFile`，返回 url 字段。
- 跨域配置允许前端端口（如 5173/8080）。

### 3.3 数据库字段
- 数据库 video_url、image_url 字段与 MinIO 返回的 url 保持一致。

### 3.4 Meeting 相关接口
- MeetingController 路径建议 `/api/meeting`，所有 meeting 相关接口统一。

---

## 4. 其他建议
- 前后端接口路径、参数、返回值字段保持一致。
- 所有页面/组件挂载时自动恢复用户状态。
- 所有 axios 请求自动带 token。
- 所有 401/403/500 错误自动跳转登录或弹窗提示。

---

如需自动批量修正，可根据本建议逐项排查和修正。遇到具体报错可进一步定位和修复。 