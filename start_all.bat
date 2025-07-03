@echo off
echo 启动前后端服务...
echo.
echo 1. 启动后端服务器...
start "Backend Server" cmd /k "cd course_manager && mvn spring-boot:run"
echo 等待后端启动...
timeout /t 10 /nobreak > nul
echo.
echo 2. 启动前端开发服务器...
start "Frontend Server" cmd /k "cd fronted && npm run dev"
echo.
echo 前后端服务启动完成！
echo 前端地址: http://localhost:3000
echo 后端地址: http://localhost:8080
echo.
pause 