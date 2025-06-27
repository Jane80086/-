# MCP课程推荐系统

## 🎯 功能概述

本系统为课程管理平台提供了基于AI的课程热度分析和智能推荐功能，通过MCP（Model Context Protocol）服务器实现。

## 🚀 主要功能

### 1. 课程热度分析
- **实时热度计算**: 基于观看历史、用户行为等数据计算课程热度分数
- **趋势分析**: 识别课程热度上升、下降或稳定趋势
- **多维度评估**: 考虑价格、难度等级、时间权重等因素

### 2. 智能课程推荐
- **热门推荐**: 基于热度分数推荐最受欢迎的课程
- **个性化推荐**: 根据用户历史行为生成个性化推荐
- **趋势推荐**: 推荐近期热度上升的课程
- **分类推荐**: 支持按课程分类进行推荐

### 3. 首页榜单管理
- **自动更新**: 基于热度算法自动更新首页推荐课程
- **手动管理**: 支持手动指定推荐课程
- **优先级控制**: 支持设置推荐课程的优先级

## 📋 API接口

### 课程热度分析
```python
# 分析所有课程热度
analyze_course_heat(time_range="7d")

# 分析特定课程热度
analyze_course_heat(course_id=123, time_range="1d")
```

**参数说明:**
- `course_id`: 课程ID（可选，不指定则分析所有课程）
- `time_range`: 时间范围（1d/7d/30d/all）

**返回结果:**
```json
{
  "course_id": 1,
  "title": "Java编程基础",
  "heat_score": 85.5,
  "view_count": 150,
  "recent_views": 45,
  "trend": "上升",
  "recommendation_reason": "Java编程基础近期热度上升，值得推荐"
}
```

### 课程推荐生成
```python
# 生成推荐课程
generate_course_recommendations(
    user_id=123,
    category="技术",
    limit=10
)
```

**参数说明:**
- `user_id`: 用户ID（可选，用于个性化推荐）
- `category`: 课程分类（可选，用于过滤）
- `limit`: 推荐数量限制

**返回结果:**
```json
{
  "course_id": 1,
  "title": "Java编程基础",
  "category": "技术",
  "level": "BEGINNER",
  "price": 99.0,
  "heat_score": 85.5,
  "recommendation_type": "热门",
  "reason": "该课程在技术分类中热度较高"
}
```

### 更新推荐课程
```python
# 自动更新推荐课程
update_featured_courses(auto_update=True)

# 手动指定推荐课程
update_featured_courses(
    auto_update=False,
    manual_course_ids=[1, 2, 3, 4, 5]
)
```

## 🔧 安装和配置

### 1. 安装依赖
```bash
pip install mcp requests
```

### 2. 启动MCP服务器
```bash
python start_mcp_server.py
```

### 3. 测试功能
```bash
python test_mcp_recommendations.py
```

## 📊 热度算法说明

### 热度分数计算
```
热度分数 = 基础分数 × 时间权重 × 价格因子 × 等级因子
```

**各因子说明:**
- **基础分数**: 观看次数 × 10
- **时间权重**: 1天=3.0, 7天=2.0, 30天=1.5, 全部=1.0
- **价格因子**: 1.0 + (价格/1000)
- **等级因子**: 初级=1.0, 中级=1.2, 高级=1.5

### 趋势分析
- **上升**: 最近观看次数 > 总观看次数 × 70%
- **下降**: 最近观看次数 < 总观看次数 × 30%
- **稳定**: 其他情况

## 🎨 推荐策略

### 1. 热门推荐
- 基于热度分数排序
- 选择热度最高的课程
- 适合新用户和一般推荐

### 2. 个性化推荐
- 基于用户观看历史
- 分析用户偏好
- 推荐相似课程

### 3. 趋势推荐
- 识别热度上升的课程
- 推荐新兴热门内容
- 帮助用户发现新内容

## 🔄 集成到Spring Boot应用

### 1. 配置MCP服务器地址
在 `application.yml` 中配置：
```yaml
mcp:
  server:
    url: http://localhost:6277
```

### 2. 调用MCP服务
```java
@Service
public class CourseRecommendationService {
    
    @Autowired
    private RestTemplate restTemplate;
    
    public List<CourseRecommendation> getRecommendations(Long userId) {
        String url = "http://localhost:6277/tools/generate_course_recommendations";
        
        Map<String, Object> request = new HashMap<>();
        request.put("user_id", userId);
        request.put("limit", 10);
        
        ResponseEntity<List<CourseRecommendation>> response = 
            restTemplate.postForEntity(url, request, List.class);
        
        return response.getBody();
    }
}
```

## 📈 监控和优化

### 1. 性能监控
- 监控API响应时间
- 跟踪推荐准确率
- 分析用户反馈

### 2. 算法优化
- 定期调整热度权重
- 优化推荐算法
- 增加更多特征维度

### 3. 数据质量
- 清理异常数据
- 验证数据完整性
- 监控数据更新频率

## 🚨 注意事项

1. **数据同步**: 确保MCP服务器能访问到最新的课程数据
2. **性能考虑**: 大量课程时考虑分页和缓存
3. **隐私保护**: 个性化推荐时注意用户隐私
4. **错误处理**: 做好异常处理和降级策略

## 🔮 未来扩展

1. **机器学习**: 集成更复杂的推荐算法
2. **实时分析**: 支持实时热度计算
3. **多维度推荐**: 考虑更多用户行为特征
4. **A/B测试**: 支持推荐策略的A/B测试

## 📞 技术支持

如有问题，请查看：
- MCP服务器日志
- API响应状态码
- 数据库连接状态
- 网络连接情况 