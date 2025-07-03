@echo off
echo ========================================
echo 会议管理系统完整启动脚本
echo ========================================

echo.
echo 本脚本将完整启动会议管理系统：
echo 1. 检查并启动 MinIO 对象存储
echo 2. 编译并打包项目
echo 3. 启动会议管理系统（指定启动类）
echo.

cd /d "%~dp0"

echo ========================================
echo 步骤1：检查并启动 MinIO 对象存储
echo ========================================

echo 检查 MinIO 服务状态...
netstat -ano | findstr :9000 >nul

if %errorlevel% equ 0 (
    echo ✓ MinIO 服务正在运行
    echo 访问地址: http://localhost:9000
    echo 控制台: http://localhost:9001
    echo 用户名: minioadmin
    echo 密码: minioadmin
    goto compile_project
) else (
    echo MinIO 服务未运行，准备启动...
)

echo.
echo 检查 MinIO 可执行文件...
if not exist "minio\minio.exe" (
    echo ✗ 错误：找不到 minio\minio.exe
    echo 请确保 minio 目录中包含 minio.exe 文件
    echo.
    echo 跳过 MinIO 启动，继续启动会议系统...
    echo 注意：文件上传功能将不可用
    goto compile_project
)

echo ✓ 找到 MinIO 可执行文件

echo.
echo 创建 MinIO 数据目录...
if not exist "minio-data" mkdir minio-data

echo.
echo 启动 MinIO 服务...
echo 注意：MinIO 将在新窗口中启动
echo 请保持 MinIO 窗口运行，然后在此窗口继续
echo.

set MINIO_ROOT_USER=minioadmin
set MINIO_ROOT_PASSWORD=minioadmin
start "MinIO Server" /B minio\minio.exe server minio-data --console-address :9001

echo 等待 MinIO 启动...
timeout /t 8 /nobreak >nul

echo 检查 MinIO 是否启动成功...
netstat -ano | findstr :9000 >nul

if %errorlevel% equ 0 (
    echo ✓ MinIO 服务启动成功
    echo 访问地址: http://localhost:9000
    echo 控制台: http://localhost:9001
    echo 用户名: minioadmin
    echo 密码: minioadmin
) else (
    echo ⚠ MinIO 服务启动可能失败
    echo 继续启动会议系统，文件上传功能可能不可用
    echo 如需 MinIO，请手动启动：minio\minio.exe server minio-data --console-address :9001
)

echo.
echo ========================================
echo 步骤2：编译并打包项目
echo ========================================

:compile_project

echo 清理并编译项目...
call mvn clean compile -DskipTests

if %errorlevel% neq 0 (
    echo ✗ 编译失败，请检查错误信息
    echo.
    echo 可能的原因：
    echo 1. Java 版本不正确（需要 Java 17+）
    echo 2. Maven 配置问题
    echo 3. 网络连接问题
    echo.
    pause
    exit /b 1
)

echo ✓ 编译成功

echo.
echo 打包项目...
call mvn package -DskipTests

if %errorlevel% neq 0 (
    echo ✗ 打包失败，请检查错误信息
    pause
    exit /b 1
)

echo ✓ 打包成功

echo.
echo ========================================
echo 步骤3：启动会议管理系统
echo ========================================

echo 启动会议管理系统...
echo 端口: 8081
echo 配置文件: application-meeting.yml
echo 启动类: com.cemenghui.meeting.MeetingApplication
echo 数据库: meeting_management
echo.

echo 注意：系统将自动：
echo - 连接到 KingBase8 数据库
echo - 创建 meeting_management 数据库（如果不存在）
echo - 执行数据库初始化脚本
echo - 创建表结构和测试数据
echo.

echo 使用 Java 启动会议系统...
java -jar target/course-0.0.1-SNAPSHOT.jar --spring.profiles.active=meeting --spring-boot.run.main-class=com.cemenghui.meeting.MeetingApplication

if %errorlevel% neq 0 (
    echo.
    echo ✗ Java 启动失败
    echo 尝试使用 Maven 启动...
    echo.
    call mvn spring-boot:run -Dspring-boot.run.mainClass=com.cemenghui.meeting.MeetingApplication -Dspring-boot.run.profiles=meeting
    
    if %errorlevel% neq 0 (
        echo.
        echo ✗ Maven 启动也失败
        echo 请检查：
        echo 1. Java 版本是否正确（需要 Java 17+）
        echo 2. 数据库连接是否正常
        echo 3. 端口 8081 是否被占用
        echo 4. 项目编译是否成功
        echo.
        pause
        exit /b 1
    )
)

echo.
echo ========================================
echo 启动完成！
echo ========================================

echo.
echo 服务信息：
echo - 会议管理系统: http://localhost:8081
echo - MinIO 服务: http://localhost:9000
echo - MinIO 控制台: http://localhost:9001
echo.
echo 测试账号：
echo - 管理员: admin / 123456
echo - 企业用户: enterprise1 / 123456
echo - 普通用户: user1 / 123456
echo.
echo API 测试：运行 test_meeting_api_standalone.bat
echo.
echo 手动测试：
echo - 会议列表: http://localhost:8081/api/meeting/list
echo - 用户登录: http://localhost:8081/api/user/login
echo - 数据库测试: http://localhost:8081/api/database/test
echo.

pause 