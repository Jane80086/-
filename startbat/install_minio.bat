@echo off
echo ========================================
echo MinIO 安装脚本
echo ========================================
echo.

REM 检查是否已安装MinIO
where minio >nul 2>&1
if %errorlevel% equ 0 (
    echo MinIO已安装，版本信息：
    minio --version
    echo.
    echo 是否重新安装？(Y/N)
    set /p choice=
    if /i "%choice%" neq "Y" (
        echo 取消安装
        pause
        exit /b 0
    )
)

echo 正在下载MinIO...
echo.

REM 创建临时目录
if not exist "temp" mkdir temp
cd temp

REM 下载MinIO（Windows版本）
echo 正在从官方源下载MinIO...
powershell -Command "Invoke-WebRequest -Uri 'https://dl.min.io/server/minio/release/windows-amd64/minio.exe' -OutFile 'minio.exe'"

if exist "minio.exe" (
    echo MinIO下载成功！
    echo.
    echo 正在安装MinIO...
    
    REM 尝试复制到系统目录（需要管理员权限）
    copy "minio.exe" "C:\Windows\System32\minio.exe" >nul 2>&1
    if %errorlevel% equ 0 (
        echo ✅ MinIO安装成功！
        echo MinIO已安装到系统路径
    ) else (
        echo ⚠️  需要管理员权限安装到系统路径
        echo 正在安装到当前目录...
        copy "minio.exe" "..\minio.exe" >nul 2>&1
        if %errorlevel% equ 0 (
            echo ✅ MinIO安装成功！
            echo MinIO已安装到项目目录
        ) else (
            echo ❌ MinIO安装失败
            pause
            exit /b 1
        )
    )
    
    echo.
    echo MinIO安装完成！
    echo 使用方法：
    echo   minio server minio-data --console-address ":9001"
    echo.
    echo 或者使用项目中的启动脚本：
    echo   start_minio.bat
    echo.
) else (
    echo ❌ MinIO下载失败，请检查网络连接
    pause
    exit /b 1
)

REM 清理临时目录
cd ..
rmdir /s /q temp

echo 按任意键退出...
pause 