# Course模块API测试指南

## 📋 测试概述

本测试工具用于自动测试Course模块的所有API接口，包括：

### 🔧 测试模块
1. **课程优化API** - AI驱动的课程标题和描述优化
2. **课程管理API** - 课程的CRUD操作
3. **推荐API** - 基于算法的课程推荐
4. **AI问答API** - 课程相关的AI问答功能
5. **用户课程API** - 用户购买和管理课程

### 🎯 测试接口列表

#### 课程优化API
- `POST /api/course-optimization/optimize-title` - 优化课程标题
- `POST /api/course-optimization/optimize-description` - 优化课程简介
- `POST /api/course-optimization/optimize-course-info` - 批量优化课程信息

#### 课程管理API
- `GET /api/course/list` - 获取课程列表
- `GET /api/course/search` - 搜索课程
- `POST /api/course/create` - 创建课程（带AI优化）
- `POST /api/course/optimize-preview` - AI优化预览
- `GET /api/course/{id}` - 获取课程详情
- `PUT /api/course/{id}` - 更新课程
- `POST /api/course/{id}/submit-review` - 提交审核
- `GET /api/course/{id}/related` - 获取相关课程
- `GET /api/course/{id}/review-history` - 获取审核历史

#### 推荐API
- `GET /api/recommendations/user` - 获取用户推荐课程

#### AI问答API
- `POST /api/ai-questions/ask` - 提交AI问题
- `GET /api/ai-questions/course/{courseId}` - 获取课程问题列表
- `GET /api/ai-questions/user/{userId}` - 获取用户问题列表
- `POST /api/ai-questions/{id}/like` - 点赞问题
- `POST /api/ai-questions/{id}/report` - 举报问题
- `POST /api/ai-questions/{id}/accept-ai` - 接受AI回答
- `POST /api/ai-questions/{id}/accept-manual` - 接受人工回答

#### 用户课程API
- `POST /api/user-course/purchase` - 购买课程
- `GET /api/user-course/list` - 获取已购课程列表
- `DELETE /api/user-course/delete` - 删除已购课程

## 🚀 快速开始

### 方法一：使用批处理文件（推荐）
```bash
# Windows系统
双击运行 run_tests.bat
```

### 方法二：手动运行
```bash
# 1. 安装依赖
pip install -r requirements.txt

# 2. 运行测试
python run_api_tests.py
```

### 方法三：使用详细测试脚本
```bash
# 运行完整测试（包含详细报告）
python test_course_apis.py
```

## ⚙️ 前置条件

### 1. 环境要求
- Python 3.7+
- requests库
- 网络连接

### 2. 服务要求
- Course模块服务运行在 `http://localhost:8081`
- 数据库连接正常
- Dify工作流服务可用（用于AI功能）

### 3. 数据要求
- 数据库中至少有一个课程（ID=1）
- 数据库中至少有一个用户（ID=1）

## 📊 测试结果解读

### 成功标志
- ✅ 表示API调用成功（HTTP 200）
- 响应数据格式正确

### 失败原因分析
- ❌ **连接失败**: 检查服务是否启动
- ❌ **HTTP 404**: 检查API路径是否正确
- ❌ **HTTP 500**: 检查服务器内部错误
- ❌ **参数错误**: 检查请求参数格式

### 常见问题解决

#### 1. 连接被拒绝
```
❌ 优化课程标题: Connection refused
```
**解决方案**: 确保Course模块服务已启动

#### 2. 404 Not Found
```
❌ 获取课程列表: HTTP 404
```
**解决方案**: 检查API路径和控制器映射

#### 3. 500 Internal Server Error
```
❌ 创建课程: HTTP 500
```
**解决方案**: 检查数据库连接和业务逻辑

## 🔧 自定义配置

### 修改服务器地址
编辑 `run_api_tests.py` 文件中的 `base_url` 变量：
```python
base_url = "http://your-server:8081"  # 修改为你的服务器地址
```

### 修改测试数据
在 `run_api_tests.py` 中修改测试参数：
```python
course_data = {
    "title": "你的课程标题",
    "description": "你的课程描述",
    # ... 其他参数
}
```

## 📈 性能测试

### 批量测试
```python
# 在 run_api_tests.py 中添加循环测试
for i in range(10):
    print(f"第 {i+1} 轮测试")
    # 运行测试逻辑
```

### 压力测试
```python
import threading
import time

def stress_test():
    # 并发测试逻辑
    pass

# 启动多个线程进行压力测试
threads = []
for i in range(5):
    t = threading.Thread(target=stress_test)
    threads.append(t)
    t.start()
```

## 📝 测试报告

### 控制台输出
测试完成后会显示：
- 总测试数
- 通过/失败数量
- 成功率百分比
- 失败测试的详细信息

### 详细报告文件
使用 `test_course_apis.py` 会生成JSON格式的详细报告：
```json
{
  "test_name": "优化课程标题",
  "success": true,
  "status_code": 200,
  "response_data": {...},
  "timestamp": "2024-01-01 12:00:00"
}
```

## 🛠️ 故障排除

### 1. 依赖包安装失败
```bash
# 升级pip
python -m pip install --upgrade pip

# 使用国内镜像
pip install -r requirements.txt -i https://pypi.tuna.tsinghua.edu.cn/simple/
```

### 2. 编码问题
```bash
# 设置环境变量
set PYTHONIOENCODING=utf-8
```

### 3. 网络代理问题
```python
# 在代码中设置代理
proxies = {
    'http': 'http://proxy:port',
    'https': 'https://proxy:port'
}
response = requests.get(url, proxies=proxies)
```

## 📞 技术支持

如果遇到问题，请检查：
1. 服务是否正常启动
2. 数据库连接是否正常
3. 网络连接是否正常
4. API路径是否正确
5. 请求参数格式是否正确

## 🔄 持续集成

### GitHub Actions示例
```yaml
name: API Tests
on: [push, pull_request]
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v2
    - name: Set up Python
      uses: actions/setup-python@v2
      with:
        python-version: 3.8
    - name: Install dependencies
      run: pip install -r requirements.txt
    - name: Run tests
      run: python run_api_tests.py
```

---

**注意**: 请确保在运行测试前，所有相关服务都已正常启动并配置正确。 