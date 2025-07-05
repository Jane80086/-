# SQL文件清理说明

## 概述
在完成数据库整合后，可以清理旧的分散SQL文件，只保留统一的 `complete_unified_database.sql` 文件。

## 已整合的SQL文件

### 1. 主要架构文件
- ✅ `course_manager_schema.sql` - 已整合到 `complete_unified_database.sql`
- ✅ `unified_database_schema.sql` - 已整合到 `complete_unified_database.sql`

### 2. 数据迁移文件
- ✅ `data_migration_script.sql` - 已整合到 `complete_unified_database.sql`

### 3. 子系统初始化文件
- ✅ `sql/meeting_database_init.sql` - 已整合到 `complete_unified_database.sql`
- ✅ `sql/news_database_init.sql` - 已整合到 `complete_unified_database.sql`
- ✅ `sql/admin_user.sql` - 已整合到 `complete_unified_database.sql`
- ✅ `sql/enterprise_user.sql` - 已整合到 `complete_unified_database.sql`
- ✅ `sql/enterprise.sql` - 已整合到 `complete_unified_database.sql`
- ✅ `sql/user_modify_history.sql` - 已整合到 `complete_unified_database.sql`

### 4. 修复文件
- ✅ `fix_database.sql` - 已整合到 `complete_unified_database.sql`

## 可以删除的文件

### 1. 根目录文件
```bash
# 可以删除的文件
course_manager_schema.sql
unified_database_schema.sql
data_migration_script.sql
fix_database.sql
```

### 2. sql目录文件
```bash
# 可以删除的文件
sql/admin_user.sql
sql/enterprise_user.sql
sql/enterprise.sql
sql/user_modify_history.sql
sql/meeting_database_init.sql
sql/news_database_init.sql
sql/course_database_init.sql
sql/system_database_init.sql
sql/create_unified_database.sql
sql/init_all_databases.sql
sql/init_kingbase_databases.sql
```

## 保留的文件

### 1. 统一数据库文件
- ✅ `complete_unified_database.sql` - **保留**，这是完整的统一数据库脚本

### 2. 文档文件
- ✅ `database_migration_guide.md` - **保留**，迁移指南
- ✅ `DATABASE_INTEGRATION_SUMMARY.md` - **保留**，整合总结
- ✅ `cleanup_old_sql_files.md` - **保留**，本文件

## 清理命令

### Windows PowerShell
```powershell
# 删除根目录的旧SQL文件
Remove-Item course_manager_schema.sql
Remove-Item unified_database_schema.sql
Remove-Item data_migration_script.sql
Remove-Item fix_database.sql

# 删除sql目录的旧文件
Remove-Item sql/admin_user.sql
Remove-Item sql/enterprise_user.sql
Remove-Item sql/enterprise.sql
Remove-Item sql/user_modify_history.sql
Remove-Item sql/meeting_database_init.sql
Remove-Item sql/news_database_init.sql
Remove-Item sql/course_database_init.sql
Remove-Item sql/system_database_init.sql
Remove-Item sql/create_unified_database.sql
Remove-Item sql/init_all_databases.sql
Remove-Item sql/init_kingbase_databases.sql

# 如果sql目录为空，可以删除整个目录
if ((Get-ChildItem sql -File).Count -eq 0) {
    Remove-Item sql -Recurse
}
```

### Linux/Mac
```bash
# 删除根目录的旧SQL文件
rm course_manager_schema.sql
rm unified_database_schema.sql
rm data_migration_script.sql
rm fix_database.sql

# 删除sql目录的旧文件
rm sql/admin_user.sql
rm sql/enterprise_user.sql
rm sql/enterprise.sql
rm sql/user_modify_history.sql
rm sql/meeting_database_init.sql
rm sql/news_database_init.sql
rm sql/course_database_init.sql
rm sql/system_database_init.sql
rm sql/create_unified_database.sql
rm sql/init_all_databases.sql
rm sql/init_kingbase_databases.sql

# 如果sql目录为空，可以删除整个目录
rmdir sql
```

## 清理后的文件结构

```
course_manager/
├── complete_unified_database.sql          # 统一的完整数据库脚本
├── database_migration_guide.md            # 数据库迁移指南
├── DATABASE_INTEGRATION_SUMMARY.md        # 数据库整合总结
├── cleanup_old_sql_files.md               # 本清理说明文件
└── ... (其他项目文件)
```

## 注意事项

1. **备份** - 删除前建议备份所有旧文件
2. **验证** - 确保 `complete_unified_database.sql` 包含所有必要内容
3. **测试** - 使用新的统一脚本测试数据库创建
4. **文档** - 保留相关的文档文件以便参考

## 完成后的优势

1. **文件简化** - 从多个分散文件整合为一个统一文件
2. **维护便利** - 只需要维护一个SQL文件
3. **部署简单** - 只需要执行一个脚本即可完成数据库初始化
4. **版本控制** - 更容易进行版本管理和变更追踪 