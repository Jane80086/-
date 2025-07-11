@echo off
echo ========================================
echo 启动主MCP服务器
echo ========================================
echo.

REM 检查Python是否安装
python --version >nul 2>&1
if %errorlevel% neq 0 (
    echo 错误：未找到Python，请先安装Python
    pause
    exit /b 1
)

echo 正在启动主MCP服务器...
echo.

REM 启动简单MCP服务器（作为主服务器）
python simple_mcp_server.py

echo.
echo 主MCP服务器已停止
echo 按任意键退出...
pause 