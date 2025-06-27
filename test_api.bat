@echo off
echo ========================================
echo 课程管理系统 API 测试脚本
echo ========================================

set BASE_URL=http://localhost:8080

echo.
echo 1. 测试系统健康状态...
curl -s "%BASE_URL%/api/stats/health"
echo.

echo.
echo 2. 测试MCP服务器状态...
curl -s "%BASE_URL%/api/mcp/status"
echo.

echo.
echo 3. 测试获取课程列表...
curl -s "%BASE_URL%/api/course/list"
echo.

echo.
echo 4. 测试创建课程...
curl -X POST "%BASE_URL%/api/course/create" ^
  -H "Content-Type: application/json" ^
  -d "{\"title\":\"测试课程\",\"description\":\"这是一个测试课程\",\"instructorId\":1,\"price\":99.00,\"duration\":120,\"level\":\"BEGINNER\",\"category\":\"技术\"}"
echo.

echo.
echo 5. 测试搜索课程...
curl -s "%BASE_URL%/api/search/courses?keyword=Python"
echo.

echo.
echo 6. 测试获取热门关键词...
curl -s "%BASE_URL%/api/search/hot-keywords"
echo.

echo.
echo 7. 测试MCP内容优化...
curl -X POST "%BASE_URL%/api/mcp/optimize-content" ^
  -d "originalTitle=Python编程课程" ^
  -d "originalDescription=学习Python基础语法"
echo.

echo.
echo 8. 测试获取推荐课程...
curl -s "%BASE_URL%/api/featured/list"
echo.

echo.
echo ========================================
echo API 测试完成！
echo ========================================
echo.
echo 如果看到JSON响应，说明API正常工作。
echo 如果看到连接错误，请确保Spring Boot应用正在运行。
echo.
pause 