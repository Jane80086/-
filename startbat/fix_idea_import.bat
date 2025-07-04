@echo off
echo ========================================
echo IDEA导入问题修复脚本
echo ========================================

echo.
echo 步骤1: 清理项目...
call mvn clean

echo.
echo 步骤2: 重新编译项目...
call mvn compile

echo.
echo 步骤3: 下载依赖...
call mvn dependency:resolve

echo.
echo 步骤4: 验证项目结构...
call mvn validate

echo.
echo ========================================
echo 修复完成！
echo ========================================
echo.
echo 请在IDEA中执行以下操作：
echo 1. File -> Invalidate Caches and Restart
echo 2. 重新导入Maven项目
echo 3. Build -> Rebuild Project
echo.
pause 