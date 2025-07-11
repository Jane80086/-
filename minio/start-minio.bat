@echo off
echo 启动MinIO服务...
echo 服务地址: http://localhost:9000
echo 控制台地址: http://localhost:9001
echo 默认账号: minioadmin
echo 默认密码: minioadmin
echo.

set MINIO_ROOT_USER=minioadmin
set MINIO_ROOT_PASSWORD=minioadmin

minio.exe server data --console-address :9001
pause
