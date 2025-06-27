@echo off
echo ========================================
echo 启动 KingBase8 数据库
echo ========================================

echo 检查数据库服务状态...
netstat -ano | findstr :54321

if %errorlevel% equ 0 (
    echo KingBase8 数据库服务已在运行
) else (
    echo KingBase8 数据库服务未运行，请手动启动
    echo.
    echo 启动步骤：
    echo 1. 打开 KingBase8 管理工具
    echo 2. 启动数据库服务
    echo 3. 确保端口 54321 可用
    echo.
    echo 或者使用命令行启动：
    echo ksql -h localhost -p 54321 -U SYSTEM -d course_management
)

echo.
echo 数据库连接信息：
echo - 主机: localhost
echo - 端口: 54321
echo - 数据库: course_management
echo - 用户名: SYSTEM
echo - 密码: 123456
echo.

echo 按任意键继续...
pause > nul 