"""
MCP Course Recommendation Server
为课程管理系统提供课程推荐服务
"""

from typing import Optional, List, Dict, Any
from mcp.server.fastmcp import FastMCP
from pydantic import BaseModel, Field
import requests
import json
from datetime import datetime
import random

# 创建MCP服务器
mcp = FastMCP("Course Recommendation Server", dependencies=["requests"])

# API配置
API_BASE_URL = "http://localhost:8080/api"

class CourseRecommendation(BaseModel):
    """课程推荐结果"""
    course_id: int = Field(..., description="课程ID")
    title: str = Field(..., description="课程标题")
    category: str = Field(..., description="课程分类")
    level: str = Field(..., description="难度等级")
    price: float = Field(..., description="价格")
    instructor: str = Field(..., description="讲师")
    recommendation_score: float = Field(..., description="推荐分数")
    recommendation_reason: str = Field(..., description="推荐理由")
    tags: List[str] = Field(..., description="标签")

class UserPreference(BaseModel):
    """用户偏好"""
    user_id: int = Field(..., description="用户ID")
    preferred_categories: List[str] = Field(..., description="偏好分类")
    preferred_levels: List[str] = Field(..., description="偏好难度")
    max_price: Optional[float] = Field(None, description="最大价格")
    preferred_instructors: List[str] = Field(..., description="偏好讲师")

@mcp.tool()
def get_personalized_recommendations(
    user_id: int,
    limit: int = 10,
    category: Optional[str] = None,
    level: Optional[str] = None
) -> List[CourseRecommendation]:
    """
    获取个性化课程推荐
    
    Args:
        user_id: 用户ID
        limit: 推荐数量限制
        category: 课程分类过滤
        level: 难度等级过滤
    
    Returns:
        个性化推荐课程列表
    """
    try:
        # 获取用户偏好
        user_prefs = get_user_preferences(user_id)
        
        # 获取所有课程
        courses = get_all_courses()
        
        # 根据用户偏好筛选和排序
        recommendations = []
        
        for course in courses:
            # 应用过滤条件
            if category and course.get('category') != category:
                continue
            if level and course.get('level') != level:
                continue
            
            # 计算推荐分数
            score = calculate_recommendation_score(course, user_prefs)
            
            if score > 0:
                recommendation = CourseRecommendation(
                    course_id=course.get('id', 0),
                    title=course.get('title', ''),
                    category=course.get('category', ''),
                    level=course.get('level', ''),
                    price=float(course.get('price', 0)),
                    instructor=course.get('instructor', ''),
                    recommendation_score=score,
                    recommendation_reason=generate_recommendation_reason(course, user_prefs, score),
                    tags=course.get('tags', [])
                )
                recommendations.append(recommendation)
        
        # 按推荐分数排序
        recommendations.sort(key=lambda x: x.recommendation_score, reverse=True)
        
        return recommendations[:limit]
        
    except Exception as e:
        print(f"获取个性化推荐时出错: {e}")
        return []

@mcp.tool()
def get_popular_recommendations(
    limit: int = 10,
    category: Optional[str] = None,
    time_range: str = "7d"
) -> List[CourseRecommendation]:
    """
    获取热门课程推荐
    
    Args:
        limit: 推荐数量限制
        category: 课程分类过滤
        time_range: 时间范围（1d/7d/30d/all）
    
    Returns:
        热门推荐课程列表
    """
    try:
        # 获取热门课程
        hot_courses = get_hot_courses(category, time_range)
        
        recommendations = []
        
        for course in hot_courses:
            recommendation = CourseRecommendation(
                course_id=course.get('id', 0),
                title=course.get('title', ''),
                category=course.get('category', ''),
                level=course.get('level', ''),
                price=float(course.get('price', 0)),
                instructor=course.get('instructor', ''),
                recommendation_score=course.get('popularity_score', 0.0),
                recommendation_reason=f"该课程在{time_range}内非常热门",
                tags=course.get('tags', [])
            )
            recommendations.append(recommendation)
        
        return recommendations[:limit]
        
    except Exception as e:
        print(f"获取热门推荐时出错: {e}")
        return []

@mcp.tool()
def get_similar_courses(
    course_id: int,
    limit: int = 5
) -> List[CourseRecommendation]:
    """
    获取相似课程推荐
    
    Args:
        course_id: 参考课程ID
        limit: 推荐数量限制
    
    Returns:
        相似课程列表
    """
    try:
        # 获取参考课程信息
        reference_course = get_course_by_id(course_id)
        if not reference_course:
            return []
        
        # 获取所有课程
        all_courses = get_all_courses()
        
        # 计算相似度并排序
        similar_courses = []
        
        for course in all_courses:
            if course.get('id') == course_id:
                continue
            
            similarity_score = calculate_similarity_score(reference_course, course)
            
            if similarity_score > 0.3:  # 相似度阈值
                recommendation = CourseRecommendation(
                    course_id=course.get('id', 0),
                    title=course.get('title', ''),
                    category=course.get('category', ''),
                    level=course.get('level', ''),
                    price=float(course.get('price', 0)),
                    instructor=course.get('instructor', ''),
                    recommendation_score=similarity_score,
                    recommendation_reason=f"与《{reference_course.get('title', '')}》相似的课程",
                    tags=course.get('tags', [])
                )
                similar_courses.append(recommendation)
        
        # 按相似度排序
        similar_courses.sort(key=lambda x: x.recommendation_score, reverse=True)
        
        return similar_courses[:limit]
        
    except Exception as e:
        print(f"获取相似课程时出错: {e}")
        return []

@mcp.tool()
def get_trending_recommendations(
    limit: int = 10,
    category: Optional[str] = None
) -> List[CourseRecommendation]:
    """
    获取趋势课程推荐
    
    Args:
        limit: 推荐数量限制
        category: 课程分类过滤
    
    Returns:
        趋势推荐课程列表
    """
    try:
        # 获取趋势课程
        trending_courses = get_trending_courses(category)
        
        recommendations = []
        
        for course in trending_courses:
            recommendation = CourseRecommendation(
                course_id=course.get('id', 0),
                title=course.get('title', ''),
                category=course.get('category', ''),
                level=course.get('level', ''),
                price=float(course.get('price', 0)),
                instructor=course.get('instructor', ''),
                recommendation_score=course.get('trend_score', 0.0),
                recommendation_reason="该课程近期热度上升较快",
                tags=course.get('tags', [])
            )
            recommendations.append(recommendation)
        
        return recommendations[:limit]
        
    except Exception as e:
        print(f"获取趋势推荐时出错: {e}")
        return []

@mcp.tool()
def update_user_preferences(
    user_id: int,
    preferred_categories: List[str],
    preferred_levels: List[str],
    max_price: Optional[float] = None,
    preferred_instructors: Optional[List[str]] = None
) -> bool:
    """
    更新用户偏好设置
    
    Args:
        user_id: 用户ID
        preferred_categories: 偏好分类
        preferred_levels: 偏好难度
        max_price: 最大价格
        preferred_instructors: 偏好讲师
    
    Returns:
        是否更新成功
    """
    try:
        preference_data = {
            "user_id": user_id,
            "preferred_categories": preferred_categories,
            "preferred_levels": preferred_levels
        }
        
        if max_price is not None:
            preference_data["max_price"] = max_price
        if preferred_instructors:
            preference_data["preferred_instructors"] = preferred_instructors
        
        response = requests.put(
            f"{API_BASE_URL}/users/{user_id}/preferences",
            json=preference_data
        )
        response.raise_for_status()
        
        return True
        
    except Exception as e:
        print(f"更新用户偏好时出错: {e}")
        return False

# 辅助函数
def get_user_preferences(user_id: int) -> UserPreference:
    """获取用户偏好"""
    try:
        response = requests.get(f"{API_BASE_URL}/users/{user_id}/preferences")
        response.raise_for_status()
        
        data = response.json()
        return UserPreference(**data)
        
    except Exception as e:
        print(f"获取用户偏好时出错: {e}")
        # 返回默认偏好
        return UserPreference(
            user_id=user_id,
            preferred_categories=["编程", "设计", "营销"],
            preferred_levels=["初级", "中级"],
            max_price=1000.0,
            preferred_instructors=[]
        )

def get_all_courses() -> List[Dict[str, Any]]:
    """获取所有课程"""
    try:
        response = requests.get(f"{API_BASE_URL}/courses")
        response.raise_for_status()
        
        data = response.json()
        return data.get('courses', [])
        
    except Exception as e:
        print(f"获取课程列表时出错: {e}")
        return []

def get_course_by_id(course_id: int) -> Optional[Dict[str, Any]]:
    """根据ID获取课程"""
    try:
        response = requests.get(f"{API_BASE_URL}/courses/{course_id}")
        response.raise_for_status()
        
        return response.json()
        
    except Exception as e:
        print(f"获取课程详情时出错: {e}")
        return None

def get_hot_courses(category: Optional[str] = None, time_range: str = "7d") -> List[Dict[str, Any]]:
    """获取热门课程"""
    try:
        params = {"time_range": time_range}
        if category:
            params["category"] = category
        
        response = requests.get(f"{API_BASE_URL}/courses/hot", params=params)
        response.raise_for_status()
        
        return response.json()
        
    except Exception as e:
        print(f"获取热门课程时出错: {e}")
        return []

def get_trending_courses(category: Optional[str] = None) -> List[Dict[str, Any]]:
    """获取趋势课程"""
    try:
        params = {}
        if category:
            params["category"] = category
        
        response = requests.get(f"{API_BASE_URL}/courses/trending", params=params)
        response.raise_for_status()
        
        return response.json()
        
    except Exception as e:
        print(f"获取趋势课程时出错: {e}")
        return []

def calculate_recommendation_score(course: Dict[str, Any], user_prefs: UserPreference) -> float:
    """计算推荐分数"""
    score = 0.0
    
    # 分类匹配
    if course.get('category') in user_prefs.preferred_categories:
        score += 0.3
    
    # 难度匹配
    if course.get('level') in user_prefs.preferred_levels:
        score += 0.2
    
    # 价格匹配
    course_price = float(course.get('price', 0))
    if user_prefs.max_price and course_price <= user_prefs.max_price:
        score += 0.2
    
    # 讲师匹配
    if course.get('instructor') in user_prefs.preferred_instructors:
        score += 0.3
    
    # 随机因素
    score += random.uniform(0, 0.1)
    
    return min(score, 1.0)

def calculate_similarity_score(course1: Dict[str, Any], course2: Dict[str, Any]) -> float:
    """计算课程相似度"""
    score = 0.0
    
    # 分类相似度
    if course1.get('category') == course2.get('category'):
        score += 0.4
    
    # 难度相似度
    if course1.get('level') == course2.get('level'):
        score += 0.3
    
    # 价格相似度
    price1 = float(course1.get('price', 0))
    price2 = float(course2.get('price', 0))
    if abs(price1 - price2) < 50:
        score += 0.2
    
    # 讲师相似度
    if course1.get('instructor') == course2.get('instructor'):
        score += 0.1
    
    return score

def generate_recommendation_reason(course: Dict[str, Any], user_prefs: UserPreference, score: float) -> str:
    """生成推荐理由"""
    reasons = []
    
    if course.get('category') in user_prefs.preferred_categories:
        reasons.append(f"符合您偏好的{course.get('category')}分类")
    
    if course.get('level') in user_prefs.preferred_levels:
        reasons.append(f"难度等级适合您的水平")
    
    if course.get('instructor') in user_prefs.preferred_instructors:
        reasons.append(f"由您喜欢的讲师{course.get('instructor')}授课")
    
    if reasons:
        return "，".join(reasons)
    else:
        return "基于您的学习历史推荐"

if __name__ == "__main__":
    mcp.run() 