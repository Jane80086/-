@echo off
echo ========================================
echo 修复数据库表结构
echo ========================================

echo 正在修复courses表的level字段...

REM 尝试使用ksql执行SQL文件
REM 如果ksql不在PATH中，请手动执行fix_database.sql文件

echo.
echo 请手动执行以下SQL命令：
echo.
echo 1. 连接到KingBase数据库：
echo    ksql -h localhost -p 54321 -U SYSTEM -d course_management
echo.
echo 2. 执行以下SQL：
echo    ALTER TABLE courses RENAME COLUMN level TO course_level;
echo.
echo 3. 或者直接执行fix_database.sql文件：
echo    ksql -h localhost -p 54321 -U SYSTEM -d course_management -f fix_database.sql
echo.

echo 修复完成后，请重新启动Spring Boot应用
echo.
pause 