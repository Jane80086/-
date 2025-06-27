@echo off
echo ========================================
echo 启动 MinIO 对象存储服务器
echo ========================================

echo.
echo 检查MinIO是否已安装...

where minio >nul 2>nul
if %errorlevel% neq 0 (
    echo MinIO未安装，正在下载...
    echo 请访问: https://min.io/download
    echo 下载Windows版本并安装到系统PATH中
    pause
    exit /b 1
)

echo MinIO已安装，正在启动...
echo.

echo 创建数据目录...
if not exist "minio-data" mkdir minio-data

echo 启动MinIO服务器...
echo 访问地址: http://localhost:9000
echo 用户名: minioadmin
echo 密码: minioadmin
echo.

minio server minio-data --console-address ":9001"

echo.
echo MinIO服务器已停止
pause 