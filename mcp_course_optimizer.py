"""
MCP Course Optimizer Server
为课程管理系统提供AI优化标题和描述的功能，以及课程热度分析和推荐
"""

from typing import Optional, List, Dict, Any
from mcp.server.fastmcp import FastMCP
from pydantic import BaseModel, Field
import requests
import json
from datetime import datetime, timedelta
import math

# 创建MCP服务器
mcp = FastMCP("Course Optimizer", dependencies=["openai", "requests"])

# API配置
API_BASE_URL = "http://localhost:8080/api"

class CourseHeatAnalysis(BaseModel):
    """课程热度分析结果"""
    course_id: int = Field(..., description="课程ID")
    title: str = Field(..., description="课程标题")
    heat_score: float = Field(..., description="热度分数")
    view_count: int = Field(..., description="观看次数")
    recent_views: int = Field(..., description="最近7天观看次数")
    trend: str = Field(..., description="趋势：上升/下降/稳定")
    recommendation_reason: str = Field(..., description="推荐理由")

class CourseRecommendation(BaseModel):
    """课程推荐结果"""
    course_id: int = Field(..., description="课程ID")
    title: str = Field(..., description="课程标题")
    category: str = Field(..., description="课程分类")
    level: str = Field(..., description="难度等级")
    price: float = Field(..., description="价格")
    heat_score: float = Field(..., description="热度分数")
    recommendation_type: str = Field(..., description="推荐类型：热门/趋势/个性化")
    reason: str = Field(..., description="推荐理由")

@mcp.tool()
def analyze_course_heat(
    course_id: Optional[int] = None,
    time_range: str = "7d"
) -> List[CourseHeatAnalysis]:
    """
    分析课程热度，基于观看历史、评分、评论等数据
    
    Args:
        course_id: 特定课程ID，如果为None则分析所有课程
        time_range: 时间范围（1d/7d/30d/all）
    
    Returns:
        课程热度分析结果列表
    """
    
    try:
        # 获取课程数据
        if course_id:
            courses = [get_course_by_id(course_id)]
        else:
            courses = get_all_courses()
        
        heat_analyses = []
        
        for course in courses:
            # 获取观看历史数据
            view_history = get_course_view_history(course['id'], time_range)
            
            # 计算热度分数
            heat_score = calculate_heat_score(course, view_history, time_range)
            
            # 判断趋势
            trend = analyze_trend(view_history, time_range)
            
            # 生成推荐理由
            reason = generate_recommendation_reason(course, heat_score, trend)
            
            heat_analysis = CourseHeatAnalysis(
                course_id=course['id'],
                title=course['title'],
                heat_score=heat_score,
                view_count=len(view_history),
                recent_views=len([v for v in view_history if is_recent(v['viewedAt'], time_range)]),
                trend=trend,
                recommendation_reason=reason
            )
            
            heat_analyses.append(heat_analysis)
        
        # 按热度分数排序
        heat_analyses.sort(key=lambda x: x.heat_score, reverse=True)
        
        return heat_analyses
        
    except Exception as e:
        print(f"分析课程热度时出错: {e}")
        return []

@mcp.tool()
def generate_course_recommendations(
    user_id: Optional[int] = None,
    category: Optional[str] = None,
    limit: int = 10
) -> List[CourseRecommendation]:
    """
    生成课程推荐，支持个性化推荐和热门推荐
    
    Args:
        user_id: 用户ID，用于个性化推荐
        category: 课程分类过滤
        limit: 推荐数量限制
    
    Returns:
        课程推荐列表
    """
    
    try:
        recommendations = []
        
        # 获取热门课程
        hot_courses = get_hot_courses(category, limit // 2)
        
        for course in hot_courses:
            recommendation = CourseRecommendation(
                course_id=course['id'],
                title=course['title'],
                category=course['category'],
                level=course['level'],
                price=float(course['price']),
                heat_score=course.get('heat_score', 0.0),
                recommendation_type="热门",
                reason=f"该课程在{course['category']}分类中热度较高"
            )
            recommendations.append(recommendation)
        
        # 如果指定了用户ID，添加个性化推荐
        if user_id:
            personalized_courses = get_personalized_recommendations(user_id, category, limit // 2)
            
            for course in personalized_courses:
                recommendation = CourseRecommendation(
                    course_id=course['id'],
                    title=course['title'],
                    category=course['category'],
                    level=course['level'],
                    price=float(course['price']),
                    heat_score=course.get('heat_score', 0.0),
                    recommendation_type="个性化",
                    reason=f"基于您的学习历史推荐"
                )
                recommendations.append(recommendation)
        
        # 添加趋势课程
        trend_courses = get_trending_courses(category, limit // 4)
        
        for course in trend_courses:
            recommendation = CourseRecommendation(
                course_id=course['id'],
                title=course['title'],
                category=course['category'],
                level=course['level'],
                price=float(course['price']),
                heat_score=course.get('heat_score', 0.0),
                recommendation_type="趋势",
                reason="该课程近期热度上升较快"
            )
            recommendations.append(recommendation)
        
        return recommendations[:limit]
        
    except Exception as e:
        print(f"生成课程推荐时出错: {e}")
        return []

@mcp.tool()
def update_featured_courses(
    auto_update: bool = True,
    manual_course_ids: Optional[List[int]] = None
) -> Dict[str, Any]:
    """
    更新首页推荐课程列表
    
    Args:
        auto_update: 是否自动更新（基于热度算法）
        manual_course_ids: 手动指定的课程ID列表
    
    Returns:
        更新结果
    """
    
    try:
        if auto_update:
            # 自动选择热门课程
            hot_courses = get_hot_courses(limit=10)
            course_ids = [course['id'] for course in hot_courses]
        else:
            course_ids = manual_course_ids or []
        
        # 更新推荐课程
        success_count = 0
        for course_id in course_ids:
            if add_to_featured_courses(course_id):
                success_count += 1
        
        return {
            "success": True,
            "updated_count": success_count,
            "course_ids": course_ids,
            "message": f"成功更新{success_count}个推荐课程"
        }
        
    except Exception as e:
        return {
            "success": False,
            "error": str(e),
            "message": "更新推荐课程失败"
        }

# API调用函数
def get_all_courses() -> List[Dict[str, Any]]:
    """获取所有课程"""
    try:
        response = requests.get(f"{API_BASE_URL}/courses")
        if response.status_code == 200:
            return response.json()
        return []
    except:
        # 返回模拟数据
        return [
            {"id": 1, "title": "Java编程基础", "category": "技术", "level": "BEGINNER", "price": 99.0},
            {"id": 2, "title": "Python数据分析", "category": "技术", "level": "INTERMEDIATE", "price": 129.0},
            {"id": 3, "title": "项目管理实战", "category": "管理", "level": "ADVANCED", "price": 199.0}
        ]

def get_course_by_id(course_id: int) -> Dict[str, Any]:
    """根据ID获取课程"""
    try:
        response = requests.get(f"{API_BASE_URL}/courses/{course_id}")
        if response.status_code == 200:
            return response.json()
        return {}
    except:
        return {"id": course_id, "title": f"课程{course_id}", "category": "技术", "level": "BEGINNER", "price": 99.0}

def get_course_view_history(course_id: int, time_range: str) -> List[Dict[str, Any]]:
    """获取课程观看历史"""
    try:
        response = requests.get(f"{API_BASE_URL}/course-history/{course_id}")
        if response.status_code == 200:
            return response.json()
        return []
    except:
        # 返回模拟数据
        return [
            {"courseId": course_id, "userId": 1, "viewedAt": datetime.now().isoformat()},
            {"courseId": course_id, "userId": 2, "viewedAt": (datetime.now() - timedelta(days=1)).isoformat()}
        ]

def calculate_heat_score(course: Dict[str, Any], view_history: List[Dict[str, Any]], time_range: str) -> float:
    """计算热度分数"""
    base_score = len(view_history) * 10
    
    # 根据时间范围调整权重
    if time_range == "1d":
        weight = 3.0
    elif time_range == "7d":
        weight = 2.0
    elif time_range == "30d":
        weight = 1.5
    else:
        weight = 1.0
    
    # 考虑课程价格和等级
    price_factor = 1.0 + (float(course.get('price', 0)) / 1000)
    level_factor = {"BEGINNER": 1.0, "INTERMEDIATE": 1.2, "ADVANCED": 1.5}.get(course.get('level', 'BEGINNER'), 1.0)
    
    return base_score * weight * price_factor * level_factor

def analyze_trend(view_history: List[Dict[str, Any]], time_range: str) -> str:
    """分析趋势"""
    if len(view_history) < 2:
        return "稳定"
    
    # 简单趋势分析
    recent_count = len([v for v in view_history if is_recent(v['viewedAt'], time_range)])
    total_count = len(view_history)
    
    if recent_count > total_count * 0.7:
        return "上升"
    elif recent_count < total_count * 0.3:
        return "下降"
    else:
        return "稳定"

def is_recent(viewed_at: str, time_range: str) -> bool:
    """判断是否为最近的时间"""
    try:
        view_time = datetime.fromisoformat(viewed_at.replace('Z', '+00:00'))
        now = datetime.now()
        
        if time_range == "1d":
            return (now - view_time).days <= 1
        elif time_range == "7d":
            return (now - view_time).days <= 7
        elif time_range == "30d":
            return (now - view_time).days <= 30
        else:
            return True
    except:
        return False

def generate_recommendation_reason(course: Dict[str, Any], heat_score: float, trend: str) -> str:
    """生成推荐理由"""
    if trend == "上升":
        return f"{course['title']}近期热度上升，值得推荐"
    elif heat_score > 100:
        return f"{course['title']}热度较高，用户关注度高"
    else:
        return f"{course['title']}内容优质，适合推荐"

def get_hot_courses(category: Optional[str] = None, limit: int = 10) -> List[Dict[str, Any]]:
    """获取热门课程"""
    courses = get_all_courses()
    
    if category:
        courses = [c for c in courses if c.get('category') == category]
    
    # 模拟热度分数
    for course in courses:
        course['heat_score'] = calculate_heat_score(course, [], "7d")
    
    # 按热度排序
    courses.sort(key=lambda x: x.get('heat_score', 0), reverse=True)
    
    return courses[:limit]

def get_personalized_recommendations(user_id: int, category: Optional[str] = None, limit: int = 5) -> List[Dict[str, Any]]:
    """获取个性化推荐"""
    # 这里应该基于用户历史行为进行推荐
    # 目前返回模拟数据
    courses = get_all_courses()
    
    if category:
        courses = [c for c in courses if c.get('category') == category]
    
    return courses[:limit]

def get_trending_courses(category: Optional[str] = None, limit: int = 5) -> List[Dict[str, Any]]:
    """获取趋势课程"""
    courses = get_all_courses()
    
    if category:
        courses = [c for c in courses if c.get('category') == category]
    
    # 模拟趋势分数
    for course in courses:
        course['heat_score'] = calculate_heat_score(course, [], "1d") * 1.5  # 趋势课程权重更高
    
    courses.sort(key=lambda x: x.get('heat_score', 0), reverse=True)
    
    return courses[:limit]

def add_to_featured_courses(course_id: int) -> bool:
    """添加到推荐课程"""
    try:
        response = requests.post(f"{API_BASE_URL}/featured-courses", json={"courseId": course_id})
        return response.status_code == 200
    except:
        return False

if __name__ == "__main__":
    mcp.run() 