# 数据库配置说明

## 问题描述

如果遇到以下错误：
```
FATAL: 对不起, 已经有太多的客户
com.kingbase8.util.KSQLException
```

这表示数据库连接数已达到最大限制。

## 解决方案

### 1. 检查数据库服务状态

运行 `start_database.bat` 脚本检查数据库服务是否正常运行。

### 2. 数据库连接配置

当前配置：
```yaml
spring:
  datasource:
    url: jdbc:kingbase8://localhost:54321/course_management?characterEncoding=UTF-8&useUnicode=true
    username: SYSTEM
    password: 123456
    driver-class-name: com.kingbase8.Driver
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
      leak-detection-threshold: 60000
      connection-test-query: SELECT 1
      validation-timeout: 5000
```

### 3. 连接池参数说明

- `maximum-pool-size`: 最大连接池大小（默认10）
- `minimum-idle`: 最小空闲连接数（默认5）
- `connection-timeout`: 连接超时时间（30秒）
- `idle-timeout`: 空闲连接超时时间（10分钟）
- `max-lifetime`: 连接最大生命周期（30分钟）
- `leak-detection-threshold`: 连接泄漏检测阈值（60秒）

### 4. 数据库服务管理

#### 启动数据库服务
```bash
# 使用KingBase8管理工具启动
# 或使用命令行
ksql -h localhost -p 54321 -U SYSTEM -d course_management
```

#### 检查数据库连接
```bash
# 测试连接
curl http://localhost:8080/api/health/db
```

#### 查看数据库状态
```bash
# 系统健康检查
curl http://localhost:8080/api/health/system
```

### 5. 常见问题解决

#### 问题1: 连接数过多
**解决方案：**
1. 减少连接池大小：`maximum-pool-size: 5`
2. 增加数据库最大连接数
3. 检查是否有连接泄漏

#### 问题2: 编码问题
**解决方案：**
1. 在URL中添加编码参数：`?characterEncoding=UTF-8&useUnicode=true`
2. 确保数据库使用UTF-8编码

#### 问题3: 连接超时
**解决方案：**
1. 增加连接超时时间：`connection-timeout: 60000`
2. 检查网络连接
3. 检查数据库服务状态

### 6. 性能优化建议

1. **连接池调优**
   - 根据并发用户数调整 `maximum-pool-size`
   - 设置合适的 `minimum-idle` 值

2. **连接生命周期管理**
   - 设置合理的 `max-lifetime` 值
   - 启用连接泄漏检测

3. **监控和日志**
   - 使用健康检查端点监控数据库状态
   - 查看应用日志中的连接池信息

### 7. 故障排除

#### 检查数据库是否运行
```bash
netstat -ano | findstr :54321
```

#### 检查应用日志
```bash
# 查看Spring Boot启动日志
tail -f logs/application.log
```

#### 手动测试数据库连接
```bash
# 使用JDBC测试
java -cp "kingbase8-8.6.0.jar" TestConnection
```

## 相关文件

- `src/main/resources/application.yml` - 主配置文件
- `src/main/java/com/cemenghui/course/config/DataSourceConfig.java` - 数据源配置
- `src/main/java/com/cemenghui/course/controller/HealthController.java` - 健康检查
- `start_database.bat` - 数据库启动脚本 