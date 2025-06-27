# MCP Course Optimizer Server

为课程管理系统提供AI优化标题和描述功能的MCP服务器。

## 🚀 功能特性

- **课程内容优化**: AI优化课程标题和描述，提升吸引力和SEO效果
- **SEO分析**: 分析课程内容的SEO效果，提供改进建议
- **模板生成**: 根据课程类型和目标受众生成标题和描述模板
- **最佳实践**: 提供课程优化的最佳实践指南和示例

## 📋 工具列表

### 1. optimize_course_content
优化课程标题和描述
- **参数**: 
  - `original_title`: 原始课程标题
  - `original_description`: 原始课程描述
  - `target_audience`: 目标受众（可选）
  - `course_type`: 课程类型（可选）
  - `optimization_focus`: 优化重点（title/description/both）
- **返回**: 优化后的标题、描述、改进建议和SEO关键词

### 2. analyze_course_seo
分析课程SEO效果
- **参数**:
  - `title`: 课程标题
  - `description`: 课程描述
- **返回**: SEO评分、长度分析、改进建议

### 3. generate_course_templates
生成课程模板
- **参数**:
  - `course_type`: 课程类型
  - `target_audience`: 目标受众
- **返回**: 标题和描述模板列表

## 📚 资源列表

### 1. course-optimizer://templates
获取课程优化最佳实践指南

### 2. course-optimizer://examples
获取优化示例和对比

## 🛠️ 安装和运行

### 1. 安装依赖
```bash
pip install -r requirements.txt
```

### 2. 配置环境变量
复制 `env.example` 为 `.env` 并配置：
```bash
cp env.example .env
# 编辑 .env 文件，添加你的API密钥
```

### 3. 启动服务器
```bash
# 方式1: 使用启动脚本
python start_mcp_server.py

# 方式2: 直接运行
python mcp_course_optimizer.py

# 方式3: 使用MCP CLI
mcp dev mcp_course_optimizer.py
```

### 4. 集成到Claude Desktop
```bash
mcp install mcp_course_optimizer.py
```

## 🔧 开发模式

设置环境变量启用开发模式：
```bash
export DEBUG=1
python start_mcp_server.py
```

## 📝 使用示例

### 在Claude中调用
```
请帮我优化这个课程：
标题：Python编程课程
描述：学习Python基础语法
目标受众：初学者
课程类型：技术
```

### 直接API调用
```python
import requests

# 优化课程内容
response = requests.post("http://localhost:8000/tools/optimize_course_content", json={
    "original_title": "Python编程课程",
    "original_description": "学习Python基础语法",
    "target_audience": "初学者",
    "course_type": "技术"
})

print(response.json())
```

## 🔗 集成到Spring Boot项目

在你的Spring Boot项目中，可以通过HTTP客户端调用MCP服务器：

```java
@Service
public class CourseOptimizationService {
    
    @Autowired
    private RestTemplate restTemplate;
    
    public CourseOptimizationResult optimizeCourse(Course course) {
        String mcpUrl = "http://localhost:8000/tools/optimize_course_content";
        
        Map<String, Object> request = Map.of(
            "original_title", course.getTitle(),
            "original_description", course.getDescription(),
            "target_audience", "初学者",
            "course_type", "技术"
        );
        
        return restTemplate.postForObject(mcpUrl, request, CourseOptimizationResult.class);
    }
}
```

## 🎯 下一步计划

- [ ] 集成真实的AI服务（OpenAI/Claude）
- [ ] 添加更多课程类型模板
- [ ] 支持批量优化
- [ ] 添加历史记录和对比功能
- [ ] 集成到课程管理系统的前端界面

## �� 许可证

MIT License 