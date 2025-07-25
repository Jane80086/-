@echo off
echo ========================================
echo 启动所有MCP服务器（基础版）
echo ========================================
echo.

REM 检查Python是否安装
python --version >nul 2>&1
if %errorlevel% neq 0 (
    echo 错误：未找到Python，请先安装Python
    pause
    exit /b 1
)

echo 正在启动主要MCP服务器...
echo.

REM 启动简单MCP服务器
echo [1/4] 启动简单MCP服务器...
start "Simple MCP Server" cmd /k "python simple_mcp_server.py"

REM 等待2秒
timeout /t 2 /nobreak >nul

REM 启动课程MCP服务器
echo [2/4] 启动课程MCP服务器...
start "Course MCP Server" cmd /k "python mcp_course_server.py"

REM 等待2秒
timeout /t 2 /nobreak >nul

REM 启动问答MCP服务器
echo [3/4] 启动问答MCP服务器...
start "QA MCP Server" cmd /k "python mcp_qa_server.py"

REM 等待2秒
timeout /t 2 /nobreak >nul

REM 启动课程优化MCP服务器
echo [4/4] 启动课程优化MCP服务器...
start "Course Optimizer MCP Server" cmd /k "python ai\mcp_course_optimizer.py"

echo.
echo ========================================
echo 主要MCP服务器启动完成！
echo ========================================
echo.
echo 服务器地址：
echo - 简单MCP服务器: http://localhost:6277
echo - 课程MCP服务器: http://localhost:6278
echo - 问答MCP服务器: http://localhost:6279
echo - 课程优化MCP服务器: http://localhost:6281
echo.
echo 按任意键退出...
pause 