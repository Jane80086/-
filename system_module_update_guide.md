# System模块更新指南

## 更新概述

System模块已成功更新，现在使用main-app模块的统一User实体，同时保留了所有system模块的核心功能。

## 已完成的更新

### 1. 数据库表结构更新

#### 新增的system模块专用表：
- `system_enterprise` - 企业信息表
- `system_user_template` - 用户模板表
- `system_third_party_account` - 第三方账户表
- `system_user_modify_history` - 用户修改历史表
- `system_config` - 系统配置表
- `system_log` - 系统日志表

#### 表关联关系：
- 所有涉及用户的表都通过`user_id`字段关联到main-app的`users`表
- 外键约束确保数据一致性

### 2. 实体类更新

#### 保留的实体类：
- `Enterprise` - 企业信息实体（已添加MyBatis-Plus注解）
- `UserTemplate` - 用户模板实体（已添加MyBatis-Plus注解）
- `ThirdPartyAccount` - 第三方账户实体（已添加MyBatis-Plus注解）
- `UserModifyHistory` - 用户修改历史实体（已添加MyBatis-Plus注解）

#### 删除的实体类：
- `User` - 已删除，使用main-app的User实体
- `AdminUser` - 已删除，使用main-app的User实体
- `EnterpriseUser` - 已删除，使用main-app的User实体

### 3. 服务层更新

#### UserManagementService接口更新：
- 所有方法参数中的`EnterpriseUser`类型改为`User`类型
- 用户ID类型从`String`改为`Long`
- 保持了所有原有的业务方法

### 4. 数据访问层更新

#### UserMapper更新：
- 移除了EnterpriseUser和AdminUser相关方法
- 添加了使用main-app User实体的方法
- 更新了ThirdPartyAccount相关方法

#### UserManagementMapper更新：
- 所有方法返回类型从`EnterpriseUser`改为`User`
- 用户ID参数类型从`String`改为`Long`
- 更新了SQL语句以使用main-app的users表

#### 删除的Mapper：
- `AdminUserMapper` - 已删除
- `EnterpriseUserMapper` - 已删除

### 5. 控制器层更新

#### UserManagementController更新：
- 所有方法参数中的`EnterpriseUser`类型改为`User`类型
- 路径参数中的用户ID类型从`String`改为`Long`
- 保持了所有原有的API接口

### 6. DTO更新

#### UserListDTO更新：
- 返回的用户列表类型从`List<EnterpriseUser>`改为`List<User>`

## 使用注意事项

### 1. 导入User实体
在需要使用User实体的地方，请使用以下导入：
```java
import com.cemenghui.entity.User;
```

### 2. 用户ID类型
所有涉及用户ID的地方都使用`Long`类型，而不是`String`类型。

### 3. 数据库操作
- 用户相关操作直接操作main-app的`users`表
- system模块专用功能操作对应的system_*表
- 通过外键约束确保数据一致性

### 4. 权限管理
- 用户权限管理通过main-app的User实体进行
- system模块负责用户模板和权限模板的管理

## 核心功能保留

### 1. 企业信息管理
- 企业工商信息查询和管理
- 企业状态验证
- 统一社会信用代码验证

### 2. 用户模板管理
- 用户模板创建和管理
- 基于企业类型的模板分配
- 预设权限集合管理

### 3. 第三方账户管理
- 第三方平台账户绑定
- 第三方登录支持
- 账户关联管理

### 4. 用户修改历史
- 用户信息修改记录
- 操作审计追踪
- 历史数据查询

### 5. 系统配置管理
- 系统参数配置
- 配置项管理
- 配置历史记录

### 6. 系统日志
- 用户操作日志
- 系统行为追踪
- 日志查询和分析

## 数据库脚本

### 1. 独立脚本
- `system_database_schema.sql` - system模块专用表结构

### 2. 统一脚本
- `optimized_unified_database.sql` - 包含system模块表的完整数据库脚本

## 下一步操作

1. 确保main-app模块已正确配置并运行
2. 执行数据库脚本创建system模块表
3. 测试system模块的各项功能
4. 验证与main-app模块的集成

## 注意事项

- 所有用户相关操作都需要通过main-app的User实体进行
- 确保数据库外键约束正确设置
- 测试第三方账户绑定功能
- 验证用户模板功能是否正常 