# 课程播放功能说明

## 功能概述

你的项目现在已经具备了完整的课程播放功能，包括：

### ✅ 已实现的功能

1. **基础视频播放**
   - HTML5 原生视频播放器
   - 支持多种视频格式（MP4、WebM、OGV）
   - 自动播放和进度恢复
   - 全屏播放支持

2. **播放控制**
   - 播放/暂停控制
   - 前进/后退10秒
   - 全屏切换
   - 键盘快捷键支持（空格键播放/暂停，左右箭头快进/快退，F键全屏）

3. **学习进度管理**
   - 实时进度跟踪
   - 自动保存学习进度
   - 进度条显示
   - 断点续播功能

4. **课程大纲**
   - 章节列表显示
   - 章节跳转功能
   - 当前章节高亮
   - 章节进度显示

5. **学习笔记**
   - 添加笔记功能
   - 编辑和删除笔记
   - 时间戳标记
   - 笔记列表管理

6. **相关课程推荐**
   - 相关课程展示
   - 一键跳转功能
   - 课程信息预览

7. **播放行为记录**
   - 播放行为统计
   - 学习时长记录
   - 完成状态跟踪

## 文件结构

```
fronted/
├── src/
│   ├── views/
│   │   ├── CoursePlayer.vue          # 课程播放页面
│   │   ├── CourseDetail.vue          # 课程详情页面（已更新）
│   │   └── ...
│   ├── components/
│   │   ├── CourseCard.vue            # 课程卡片（已更新）
│   │   └── ...
│   └── router/
│       └── index.js                  # 路由配置（已更新）
└── public/
    └── test-video.mp4                # 测试视频文件

course_manager/
└── src/main/java/com/cemenghui/course/
    ├── controller/
    │   └── CourseController.java     # 课程控制器（已更新）
    ├── service/
    │   ├── CourseService.java        # 课程服务接口
    │   └── CourseHistoryService.java # 学习历史服务
    └── entity/
        └── Course.java               # 课程实体类
```

## 使用方法

### 1. 访问课程播放页面

```
http://localhost:3000/course/{课程ID}/play
```

### 2. 从课程详情页面进入

在课程详情页面点击"开始学习"按钮

### 3. 从课程卡片进入

在课程列表中点击课程卡片的"开始学习"按钮

## API接口

### 获取学习进度
```
GET /api/course/{id}/progress
```

### 保存学习进度
```
POST /api/course/{id}/progress
{
  "currentTime": 1200,
  "progress": 35.5
}
```

### 记录播放行为
```
POST /api/course/{id}/record
{
  "action": "play",
  "currentTime": 1200,
  "timestamp": 1640995200000
}
```

### 获取课程笔记
```
GET /api/course/{id}/notes
```

### 添加笔记
```
POST /api/course/{id}/notes
{
  "timestamp": 300,
  "content": "笔记内容"
}
```

## 键盘快捷键

- **空格键**: 播放/暂停
- **左箭头**: 后退10秒
- **右箭头**: 前进10秒
- **F键**: 全屏/退出全屏

## 视频文件要求

### 支持的格式
- MP4 (H.264编码)
- WebM
- OGV

### 推荐规格
- 分辨率: 1920x1080 或 1280x720
- 编码: H.264
- 音频: AAC
- 文件大小: 建议不超过500MB

## 部署说明

### 1. 视频文件存储

推荐使用MinIO对象存储：
```yaml
minio:
  endpoint: http://localhost:9000
  access-key: minioadmin
  secret-key: minioadmin
  bucket: course-files
```

### 2. 数据库表结构

确保数据库中有以下表：
- `courses` - 课程信息表
- `course_history` - 学习历史表
- `course_notes` - 学习笔记表

### 3. 前端配置

确保前端代理配置正确：
```javascript
// vite.config.js
export default {
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
}
```

## 扩展功能建议

### 1. 高级播放器
- 集成Video.js或DPlayer等专业播放器
- 支持弹幕功能
- 支持倍速播放
- 支持画中画模式

### 2. 学习分析
- 学习行为分析
- 学习效果评估
- 个性化推荐
- 学习报告生成

### 3. 互动功能
- 实时评论
- 学习讨论
- 在线答疑
- 学习小组

### 4. 移动端适配
- 响应式设计
- 触摸手势支持
- 离线播放
- 推送通知

## 故障排除

### 1. 视频无法播放
- 检查视频文件格式是否支持
- 检查视频文件路径是否正确
- 检查浏览器是否支持HTML5视频

### 2. 进度无法保存
- 检查后端API是否正常运行
- 检查数据库连接是否正常
- 检查网络请求是否成功

### 3. 播放器样式异常
- 检查CSS样式是否正确加载
- 检查Element Plus组件是否正确引入
- 检查浏览器兼容性

## 总结

你的项目现在已经具备了完整的课程播放功能，包括：

✅ **基础播放功能** - 视频播放、控制、进度管理
✅ **学习辅助功能** - 笔记、大纲、相关推荐
✅ **用户体验优化** - 快捷键、全屏、响应式设计
✅ **后端API支持** - 进度保存、行为记录、数据管理

这是一个功能完整、用户体验良好的课程播放系统，可以满足在线教育平台的基本需求。 