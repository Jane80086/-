# 会议管理系统

一个基于Vue 3 + Spring Boot的现代化会议管理系统，支持多用户权限管理和完整的会议生命周期管理。

## 功能特性

### 🎯 核心功能
- **会议创建**: 管理员和企业用户可以创建新会议
- **会议编辑**: 创建者或管理员可以修改会议信息
- **会议审核**: 企业用户创建的会议需要管理员审核
- **会议删除**: 创建者或管理员可以删除会议（需确认）
- **会议浏览**: 所有用户可以浏览会议列表
- **会议详情**: 查看会议的完整信息

### 👥 用户权限管理
- **管理员 (ADMIN)**: 拥有所有权限
  - 可以查看所有会议（包括待审核、已通过、已拒绝）
  - 可以审核会议
  - 可以创建、编辑、删除任何会议
  - 可以查看审核记录

- **企业用户 (ENTERPRISE)**: 有限权限
  - 可以查看自己创建的会议和所有已通过的会议
  - 可以创建会议（需要审核）
  - 可以编辑、删除自己创建的会议
  - 可以查看自己的申请记录

- **普通用户 (NORMAL)**: 只读权限
  - 只能查看已通过的会议
  - 无法创建、编辑、删除会议

### 🔍 搜索和筛选
- 按会议名称搜索
- 按状态筛选（管理员可查看所有状态）
- 按日期筛选
- 分页显示

### 📊 审核管理
- 待审核会议列表
- 审核操作（通过/拒绝）
- 审核记录查看
- 审核意见记录

## 技术栈

### 前端
- **Vue 3**: 渐进式JavaScript框架
- **Vue Router**: 官方路由管理器
- **Axios**: HTTP客户端
- **Vite**: 构建工具

### 后端
- **Spring Boot**: Java应用框架
- **MyBatis**: 持久层框架
- **MySQL**: 数据库
- **JWT**: 身份认证

## 快速开始

### 环境要求
- Node.js 16+
- Java 8+
- MySQL 5.7+

### 前端启动
```bash
cd city1
npm install
npm run dev
```

### 后端启动
```bash
cd demo
mvn spring-boot:run
```

### 数据库配置
1. 创建MySQL数据库
2. 修改 `demo/src/main/resources/application.yml` 中的数据库配置
3. 运行 `demo/src/main/resources/sql/init_data.sql` 初始化数据

## 使用说明

### 1. 用户登录
- 访问 `http://localhost:5173/login`
- 使用预设账号登录：
  - 管理员: admin/admin123
  - 企业用户: enterprise/enterprise123
  - 普通用户: user/user123

### 2. 会议管理
- **创建会议**: 点击"创建新会议"按钮
- **查看会议**: 点击会议卡片查看详情
- **编辑会议**: 在会议卡片上点击"编辑"按钮
- **删除会议**: 在会议卡片上点击"删除"按钮

### 3. 审核管理
- **查看待审核**: 管理员可以点击"审核管理"进入审核页面
- **审核操作**: 在待审核会议列表中进行审核
- **查看记录**: 在"审核记录"标签页查看历史记录

### 4. 搜索筛选
- 使用搜索框按会议名称搜索
- 使用状态筛选器筛选不同状态的会议
- 使用日期选择器按日期筛选

## API接口

### 会议相关
- `POST /api/meeting/create` - 创建会议
- `POST /api/meeting/update` - 更新会议
- `POST /api/meeting/delete` - 删除会议
- `POST /api/meeting/list` - 获取会议列表
- `POST /api/meeting/detail` - 获取会议详情
- `POST /api/meeting/review` - 审核会议
- `POST /api/meeting/pending` - 获取待审核会议

### 审核记录
- `POST /api/meeting/review/records/by-reviewer` - 获取审核人的审核记录
- `POST /api/meeting/review/records/by-creator` - 获取创建人的申请记录
- `POST /api/meeting/review/records/by-meeting` - 获取会议的审核记录

### 用户相关
- `POST /api/user/login` - 用户登录
- `POST /api/user/register` - 用户注册
- `POST /api/user/info` - 获取用户信息

## 数据库设计

### 用户表 (user_info)
- id: 主键
- username: 用户名
- password: 密码（MD5加密）
- real_name: 真实姓名
- email: 邮箱
- phone: 电话
- user_type: 用户类型（ADMIN/ENTERPRISE/NORMAL）
- status: 状态（1启用/0禁用）

### 会议表 (meeting_info)
- id: 主键
- meeting_name: 会议名称
- start_time: 开始时间
- end_time: 结束时间
- creator: 创建人
- meeting_content: 会议内容
- meeting_cover: 会议封面
- status: 状态（0待审核/1已通过/2已拒绝/3已删除）
- reviewer: 审核人
- review_time: 审核时间
- review_comment: 审核意见
- create_time: 创建时间
- update_time: 更新时间

### 审核记录表 (meeting_review_record)
- id: 主键
- meeting_id: 会议ID
- meeting_name: 会议名称
- creator: 创建人
- reviewer: 审核人
- status: 审核状态
- review_comment: 审核意见
- review_time: 审核时间

## 部署说明

### 前端部署
```bash
npm run build
# 将dist目录部署到Web服务器
```

### 后端部署
```bash
mvn clean package
# 运行生成的jar文件
java -jar target/demo-0.0.1-SNAPSHOT.jar
```

## 测试

访问 `http://localhost:5173/test` 查看API测试页面，验证系统功能是否正常。

## 许可证

MIT License
