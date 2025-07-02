# meeting子系统集成到总系统注意事项

本说明文档总结了测盟会系统 meeting 子系统在合入总系统（如多模块Spring Boot项目）时需要注意的关键点、必须属性及特别注意事项，供开发和运维同事参考。

---

## 1. 必须配置的属性

请确保在总系统的配置文件（如 `application.yml` 或 `application-meeting.yml`）中包含以下属性，并根据实际环境进行调整：

### MinIO 配置
```yaml
minio:
  endpoint: http://localhost:9000
  accessKey: minioadmin
  secretKey: minioadmin
  bucket: meeting-files
  presigned-url-expiration: 3600  # 预签名URL过期时间（秒）
```

### JWT 配置
```yaml
app:
  jwt:
    secret: your-secret-key-here-make-it-long-and-secure-for-production-use
    expiration: 86400000 # 24小时，单位毫秒
    refresh-expiration: 604800000 # 7天，单位毫秒
```

### 数据源配置
请根据实际数据库环境配置：
```yaml
spring:
  datasource:
    url: jdbc:kingbase8://localhost:54321/test?currentSchema=public
    username: system
    password: 123456
    driver-class-name: com.kingbase8.Driver
```

---

## 2. 依赖与版本
- **Spring Boot** 版本需兼容 meeting 子系统代码。
- 需引入如下依赖：
  - `spring-boot-starter-security`
  - `spring-boot-starter-web`
  - `spring-boot-starter-data-redis`（如用到缓存）
  - `mybatis-plus-boot-starter`
  - `io.jsonwebtoken:jjwt-api`, `jjwt-impl`, `jjwt-jackson`
  - `minio`
- 依赖冲突时请优先以总系统为准，必要时可排除重复依赖。

---

## 3. 端口与路由
- meeting 子系统默认端口为 `8080`，如与总系统冲突请调整。
- 路由前缀为 `/api/meeting` 或 `/api/file`，如需统一API网关请在总系统中配置路由转发。

---

## 4. 安全与认证
- meeting 子系统使用 JWT 认证，需保证 `JwtAuthenticationFilter`、`JwtAuthenticationEntryPoint`、`CustomUserDetailsService` 等安全组件在总系统中被正确注册。
- JWT 密钥（`app.jwt.secret`）必须全局唯一且安全，建议生产环境使用复杂随机字符串。
- 认证相关接口如 `/api/auth/**` 需在总系统安全配置中放行。

---

## 5. 文件上传与MinIO
- MinIO 服务需提前部署并保证可访问。
- `minio.bucket` 必须存在，或在系统启动时自动创建。
- 文件上传接口需保证有足够的磁盘空间和网络带宽。

---

## 6. 其他注意事项
- **逻辑删除**：数据库表需有 `deleted` 字段，配合 MyBatis-Plus 逻辑删除功能。
- **时间字段**：如 `create_time`、`update_time`，需自动填充。
- **跨域配置**：如前端与后端分离，需配置 CORS 允许前端域名访问。
- **日志路径**：如有统一日志管理，需调整 `logging.file.name`。
- **Swagger/OpenAPI**：如需统一API文档，需合并 `springdoc` 相关配置。

---

## 7. 常见问题排查
- **Token 无法验证**：请检查 JWT 密钥、token 过期时间、时区设置。
- **MinIO 连接失败**：请检查 endpoint、accessKey、secretKey、bucket 配置。
- **数据库连接异常**：请检查数据源配置、驱动依赖、网络连通性。
- **接口 401/403**：请检查安全配置、角色权限、token 是否正确传递。

---

---

## 9. 会议相关实体及属性说明

### 1. Meeting（会议主表）

| 属性名         | 类型              | 说明                | 是否必须 | 备注                         |
|----------------|-------------------|---------------------|----------|------------------------------|
| id             | Long              | 主键                | 是       | 自增                         |
| meetingName    | String            | 会议名称            | 是       |                              |
| startTime      | LocalDateTime     | 开始时间            | 是       |                              |
| endTime        | LocalDateTime     | 结束时间            | 是       |                              |
| creator        | String            | 创建人用户名        | 是       |                              |
| meetingContent | String            | 会议内容            | 是       |                              |
| status         | Integer           | 状态                | 是       | 0:待审核, 1:已通过, 2:已拒绝, 3:已删除 |
| reviewer       | String            | 审核人用户名        | 否       | 审核后填写                   |
| reviewTime     | LocalDateTime     | 审核时间            | 否       | 审核后填写                   |
| reviewComment  | String            | 审核意见            | 否       | 审核后填写                   |
| createTime     | LocalDateTime     | 创建时间            | 自动填充 |                              |
| updateTime     | LocalDateTime     | 更新时间            | 自动填充 |                              |
| imageUrl       | String            | 会议图片URL         | 否       |                              |
| deleted        | Integer           | 逻辑删除            | 自动填充 | 0:未删除, 1:已删除           |

**注意：**
- meetingName、startTime、endTime、creator、meetingContent、status 为会议功能必备，不能省略。
- deleted 字段为逻辑删除，必须保留，不能合并或省略。

---

### 2. User（用户表）

| 属性名      | 类型              | 说明         | 是否必须 | 备注                    |
|-------------|-------------------|--------------|----------|-------------------------|
| id          | Long              | 主键         | 是       | 自增                    |
| username    | String            | 用户名       | 是       | 唯一                    |
| password    | String            | 密码         | 是       | 加密存储                |
| realName    | String            | 真实姓名     | 否       |                         |
| email       | String            | 邮箱         | 否       |                         |
| phone       | String            | 手机号       | 否       |                         |
| userType    | String            | 用户类型     | 是       | ADMIN, ENTERPRISE, NORMAL|
| status      | Integer           | 状态         | 是       | 0:禁用, 1:启用          |
| createTime  | LocalDateTime     | 创建时间     | 自动填充 |                         |
| updateTime  | LocalDateTime     | 更新时间     | 自动填充 |                         |
| deleted     | Integer           | 逻辑删除     | 自动填充 | 0:未删除, 1:已删除      |

**注意：**
- username、password、userType、status 为必备，不能省略。
- deleted 字段为逻辑删除，必须保留，不能合并或省略。

---

### 3. MeetingReviewRecord（会议审核记录）

| 属性名        | 类型              | 说明         | 是否必须 | 备注           |
|---------------|-------------------|--------------|----------|----------------|
| id            | Long              | 主键         | 是       | 自增           |
| meetingId     | Long              | 会议ID       | 是       |                |
| meetingName   | String            | 会议名称     | 是       |                |
| creator       | String            | 创建人       | 是       |                |
| reviewer      | String            | 审核人       | 是       |                |
| status        | Integer           | 审核状态     | 是       |                |
| reviewComment | String            | 审核意见     | 否       |                |
| reviewTime    | LocalDateTime     | 审核时间     | 否       |                |

**注意：**
- meetingId、meetingName、creator、reviewer、status 为必备，不能省略。

---

### 4. MeetingQuery（会议查询参数）

| 属性名      | 类型          | 说明         | 是否必须 | 备注           |
|-------------|---------------|--------------|----------|----------------|
| meetingName | String        | 会议名称     | 否       | 查询用         |
| creator     | String        | 创建人       | 否       | 查询用         |
| startDate   | LocalDate     | 开始日期     | 否       | 查询用         |
| endDate     | LocalDate     | 结束日期     | 否       | 查询用         |
| status      | Integer       | 状态         | 否       | 查询用         |
| page        | Integer       | 页码         | 否       | 默认1          |
| size        | Integer       | 每页数量     | 否       | 默认10         |

---

## 10. 实体属性合并与保留建议

- 以上实体的主键、逻辑删除（deleted）、状态（status）、创建/更新时间等字段为系统功能必备，**不能被合并或省略**。
- 会议相关的 meetingName、startTime、endTime、creator、meetingContent、status 字段为会议功能核心，**必须保留**。
- 用户的 username、password、userType、status 字段为认证和权限必备，**必须保留**。
- 审核相关的 reviewer、reviewTime、reviewComment 字段为会议审核流程必备，**建议保留**。 