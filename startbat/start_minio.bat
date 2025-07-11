@echo off
echo 正在启动MinIO服务...
echo.

REM 创建MinIO数据目录
if not exist "minio-data" mkdir minio-data

REM 启动MinIO服务
echo 启动MinIO服务，访问地址: http://localhost:9000
echo 控制台地址: http://localhost:9001
echo 默认用户名: minioadmin
echo 默认密码: minioadmin
echo.
echo 按 Ctrl+C 停止服务
echo.

"D:\Microsoft\minio.exe" server minio-data --console-address ":9001"

timeout /t 30

pause 