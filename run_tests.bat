@echo off
chcp 65001 >nul
echo ========================================
echo Course模块API自动测试工具
echo ========================================
echo.

echo 正在检查Python环境...
python --version >nul 2>&1
if errorlevel 1 (
    echo ❌ 错误: 未找到Python环境，请先安装Python
    pause
    exit /b 1
)

echo ✅ Python环境检查通过
echo.

echo 正在安装依赖包...
pip install -r requirements.txt
if errorlevel 1 (
    echo ❌ 错误: 依赖包安装失败
    pause
    exit /b 1
)

echo ✅ 依赖包安装完成
echo.

echo 正在启动API测试...
echo 请确保course模块服务已启动在 http://localhost:8081
echo.
python run_api_tests.py

echo.
echo 测试完成！按任意键退出...
pause >nul 