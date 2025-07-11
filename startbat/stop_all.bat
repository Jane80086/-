@echo off
REM 停止所有相关进程
for %%p in (minio.exe java.exe python.exe) do (
    taskkill /im %%p /f >nul 2>nul
)
echo 所有服务已停止！
pause 