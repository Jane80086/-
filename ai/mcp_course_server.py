"""
MCP Course Server
为课程管理系统提供课程相关的MCP服务
"""

from typing import Optional, List, Dict, Any
from mcp.server.fastmcp import FastMCP
from pydantic import BaseModel, Field
import requests
import json
from datetime import datetime

# 创建MCP服务器
mcp = FastMCP("Course Server", dependencies=["requests"])

# API配置
API_BASE_URL = "http://localhost:8080/api"

class CourseInfo(BaseModel):
    """课程信息"""
    id: int = Field(..., description="课程ID")
    title: str = Field(..., description="课程标题")
    description: str = Field(..., description="课程描述")
    category: str = Field(..., description="课程分类")
    level: str = Field(..., description="难度等级")
    price: float = Field(..., description="价格")
    instructor: str = Field(..., description="讲师")
    duration: int = Field(..., description="课程时长（分钟）")
    cover_image: str = Field(..., description="封面图片URL")
    video_url: str = Field(..., description="视频URL")
    created_at: str = Field(..., description="创建时间")
    updated_at: str = Field(..., description="更新时间")

class CourseCreateRequest(BaseModel):
    """创建课程请求"""
    title: str = Field(..., description="课程标题")
    description: str = Field(..., description="课程描述")
    category: str = Field(..., description="课程分类")
    level: str = Field(..., description="难度等级")
    price: float = Field(..., description="价格")
    instructor: str = Field(..., description="讲师")
    duration: int = Field(..., description="课程时长（分钟）")
    cover_image: str = Field(..., description="封面图片URL")
    video_url: str = Field(..., description="视频URL")

class CourseUpdateRequest(BaseModel):
    """更新课程请求"""
    title: Optional[str] = Field(None, description="课程标题")
    description: Optional[str] = Field(None, description="课程描述")
    category: Optional[str] = Field(None, description="课程分类")
    level: Optional[str] = Field(None, description="难度等级")
    price: Optional[float] = Field(None, description="价格")
    instructor: Optional[str] = Field(None, description="讲师")
    duration: Optional[int] = Field(None, description="课程时长（分钟）")
    cover_image: Optional[str] = Field(None, description="封面图片URL")
    video_url: Optional[str] = Field(None, description="视频URL")

@mcp.tool()
def get_all_courses(
    page: int = 1,
    size: int = 10,
    category: Optional[str] = None,
    level: Optional[str] = None,
    keyword: Optional[str] = None
) -> Dict[str, Any]:
    """
    获取所有课程列表，支持分页和筛选
    
    Args:
        page: 页码
        size: 每页大小
        category: 课程分类筛选
        level: 难度等级筛选
        keyword: 关键词搜索
    
    Returns:
        课程列表和分页信息
    """
    try:
        params = {
            "page": str(page),
            "size": str(size)
        }
        
        if category:
            params["category"] = category
        if level:
            params["level"] = level
        if keyword:
            params["keyword"] = keyword
        
        response = requests.get(f"{API_BASE_URL}/courses", params=params)
        response.raise_for_status()
        
        return response.json()
        
    except Exception as e:
        print(f"获取课程列表时出错: {e}")
        return {"courses": [], "total": 0, "page": page, "size": size}

@mcp.tool()
def get_course_by_id(course_id: int) -> Optional[CourseInfo]:
    """
    根据ID获取课程详情
    
    Args:
        course_id: 课程ID
    
    Returns:
        课程详情信息
    """
    try:
        response = requests.get(f"{API_BASE_URL}/courses/{course_id}")
        response.raise_for_status()
        
        course_data = response.json()
        # 确保所有必需字段都存在
        required_fields = ["id", "title", "description", "category", "level", "price", 
                          "instructor", "duration", "cover_image", "video_url", 
                          "created_at", "updated_at"]
        for field in required_fields:
            if field not in course_data:
                course_data[field] = "" if field in ["title", "description", "category", 
                                                    "level", "instructor", "cover_image", 
                                                    "video_url", "created_at", "updated_at"] else 0
        
        return CourseInfo(**course_data)
        
    except Exception as e:
        print(f"获取课程详情时出错: {e}")
        return None

@mcp.tool()
def create_course(course_data: CourseCreateRequest) -> Optional[CourseInfo]:
    """
    创建新课程
    
    Args:
        course_data: 课程创建数据
    
    Returns:
        创建的课程信息
    """
    try:
        response = requests.post(
            f"{API_BASE_URL}/courses",
            json=course_data.dict()
        )
        response.raise_for_status()
        
        response_data = response.json()
        # 确保所有必需字段都存在
        required_fields = ["id", "title", "description", "category", "level", "price", 
                          "instructor", "duration", "cover_image", "video_url", 
                          "created_at", "updated_at"]
        for field in required_fields:
            if field not in response_data:
                response_data[field] = "" if field in ["title", "description", "category", 
                                                    "level", "instructor", "cover_image", 
                                                    "video_url", "created_at", "updated_at"] else 0
        
        return CourseInfo(**response_data)
        
    except Exception as e:
        print(f"创建课程时出错: {e}")
        return None

@mcp.tool()
def update_course(course_id: int, course_data: CourseUpdateRequest) -> Optional[CourseInfo]:
    """
    更新课程信息
    
    Args:
        course_id: 课程ID
        course_data: 课程更新数据
    
    Returns:
        更新后的课程信息
    """
    try:
        # 过滤掉None值
        update_data = {k: v for k, v in course_data.dict().items() if v is not None}
        
        response = requests.put(
            f"{API_BASE_URL}/courses/{course_id}",
            json=update_data
        )
        response.raise_for_status()
        
        response_data = response.json()
        # 确保所有必需字段都存在
        required_fields = ["id", "title", "description", "category", "level", "price", 
                          "instructor", "duration", "cover_image", "video_url", 
                          "created_at", "updated_at"]
        for field in required_fields:
            if field not in response_data:
                response_data[field] = "" if field in ["title", "description", "category", 
                                                    "level", "instructor", "cover_image", 
                                                    "video_url", "created_at", "updated_at"] else 0
        
        return CourseInfo(**response_data)
        
    except Exception as e:
        print(f"更新课程时出错: {e}")
        return None

@mcp.tool()
def delete_course(course_id: int) -> bool:
    """
    删除课程
    
    Args:
        course_id: 课程ID
    
    Returns:
        是否删除成功
    """
    try:
        response = requests.delete(f"{API_BASE_URL}/courses/{course_id}")
        response.raise_for_status()
        
        return True
        
    except Exception as e:
        print(f"删除课程时出错: {e}")
        return False

@mcp.tool()
def search_courses(
    keyword: str,
    page: int = 1,
    size: int = 10
) -> Dict[str, Any]:
    """
    搜索课程
    
    Args:
        keyword: 搜索关键词
        page: 页码
        size: 每页大小
    
    Returns:
        搜索结果
    """
    try:
        params = {
            "keyword": keyword,
            "page": str(page),
            "size": str(size)
        }
        
        response = requests.get(f"{API_BASE_URL}/courses/search", params=params)
        response.raise_for_status()
        
        return response.json()
        
    except Exception as e:
        print(f"搜索课程时出错: {e}")
        return {"courses": [], "total": 0, "page": page, "size": size}

@mcp.tool()
def get_course_categories() -> List[str]:
    """
    获取所有课程分类
    
    Returns:
        课程分类列表
    """
    try:
        response = requests.get(f"{API_BASE_URL}/courses/categories")
        response.raise_for_status()
        
        return response.json()
        
    except Exception as e:
        print(f"获取课程分类时出错: {e}")
        return []

@mcp.tool()
def get_courses_by_category(category: str, page: int = 1, size: int = 10) -> Dict[str, Any]:
    """
    根据分类获取课程
    
    Args:
        category: 课程分类
        page: 页码
        size: 每页大小
    
    Returns:
        课程列表
    """
    try:
        params = {
            "page": str(page),
            "size": str(size)
        }
        
        response = requests.get(f"{API_BASE_URL}/courses/category/{category}", params=params)
        response.raise_for_status()
        
        return response.json()
        
    except Exception as e:
        print(f"获取分类课程时出错: {e}")
        return {"courses": [], "total": 0, "page": page, "size": size}

if __name__ == "__main__":
    mcp.run() 