@echo off
echo ========================================
echo 同步数据库结构到 KingBase8
echo ========================================

echo 检查数据库连接...
echo 数据库: course_management
echo 用户: SYSTEM
echo 密码: 123456
echo 端口: 54321

echo.
echo 正在执行数据库结构同步...

REM 使用ksql命令行工具执行SQL文件
REM 注意：需要确保ksql在PATH中，或者使用完整路径
ksql -h localhost -p 54321 -U SYSTEM -d course_management -f database_schema.sql

if %errorlevel% equ 0 (
    echo ✅ 数据库结构同步成功
) else (
    echo ❌ 数据库结构同步失败
    echo.
    echo 请检查：
    echo 1. KingBase8 数据库是否正在运行
    echo 2. 数据库连接信息是否正确
    echo 3. ksql 工具是否可用
    echo 4. database_schema.sql 文件是否存在
)

echo.
echo 按任意键继续...
pause > nul 