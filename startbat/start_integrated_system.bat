@echo off
echo ========================================
echo 课程管理系统 - 集成启动脚本
echo ========================================

echo.
echo 1. 检查数据库服务状态...
call start_database.bat

echo.
echo 2. 启动 MinIO 对象存储服务...
start "MinIO Server" cmd /k "call start_minio.bat"

echo.
echo 3. 启动 MCP AI 服务器...
start "MCP Server" cmd /k "call start_mcp_server.py"

echo.
echo 4. 启动 Spring Boot 应用...
start "Spring Boot" cmd /k "mvn spring-boot:run"

echo.
echo 等待服务启动...
timeout /t 30 /nobreak > nul

echo.
echo ========================================
echo 服务启动完成！
echo ========================================
echo.
echo 访问地址：
echo - 课程管理系统: http://localhost:8080
echo - MinIO 管理界面: http://localhost:9000 (用户名/密码: minioadmin/minioadmin)
echo - 健康检查: http://localhost:8080/api/health/db
echo - 系统状态: http://localhost:8080/api/health/system
echo - 测试页面: http://localhost:8080/static/cors-test.html
echo - MinIO测试: http://localhost:8080/static/minio-test.html
echo.
echo API 端点：
echo - 课程管理: http://localhost:8080/api/courses
echo - 文件管理: http://localhost:8080/api/file
echo - MCP AI: http://localhost:8080/api/mcp
echo.
echo 按任意键退出...
pause > nul 