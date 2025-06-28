"""
MCP QA Server
为课程管理系统提供智能问答服务
"""

from typing import Optional, List, Dict, Any
from mcp.server.fastmcp import FastMCP
from pydantic import BaseModel, Field
import requests
import json
from datetime import datetime

# 创建MCP服务器
mcp = FastMCP("QA Server", dependencies=["requests"])

# API配置
API_BASE_URL = "http://localhost:8080/api"

class QuestionRequest(BaseModel):
    """问题请求"""
    question: str = Field(..., description="用户问题")
    user_id: Optional[int] = Field(None, description="用户ID")
    course_id: Optional[int] = Field(None, description="课程ID")
    context: Optional[str] = Field(None, description="上下文信息")

class AnswerResponse(BaseModel):
    """答案响应"""
    answer: str = Field(..., description="答案内容")
    confidence: float = Field(..., description="置信度")
    sources: List[str] = Field(..., description="信息来源")
    related_courses: List[int] = Field(..., description="相关课程ID")
    timestamp: str = Field(..., description="回答时间")

class FAQItem(BaseModel):
    """常见问题项"""
    id: int = Field(..., description="问题ID")
    question: str = Field(..., description="问题")
    answer: str = Field(..., description="答案")
    category: str = Field(..., description="分类")
    tags: List[str] = Field(..., description="标签")
    view_count: int = Field(..., description="查看次数")

@mcp.tool()
def ask_question(question: str, user_id: Optional[int] = None, course_id: Optional[int] = None) -> AnswerResponse:
    """
    回答用户问题
    
    Args:
        question: 用户问题
        user_id: 用户ID（可选）
        course_id: 课程ID（可选）
    
    Returns:
        答案响应
    """
    try:
        # 构建请求数据
        request_data = {
            "question": question
        }
        
        if user_id:
            request_data["user_id"] = str(user_id)
        if course_id:
            request_data["course_id"] = str(course_id)
        
        # 调用AI服务
        response = requests.post(
            f"{API_BASE_URL}/ai/qa",
            json=request_data
        )
        response.raise_for_status()
        
        data = response.json()
        
        return AnswerResponse(
            answer=data.get("answer", "抱歉，我无法回答这个问题。"),
            confidence=data.get("confidence", 0.0),
            sources=data.get("sources", []),
            related_courses=data.get("related_courses", []),
            timestamp=datetime.now().isoformat()
        )
        
    except Exception as e:
        print(f"回答问题时出错: {e}")
        return AnswerResponse(
            answer="抱歉，服务暂时不可用，请稍后再试。",
            confidence=0.0,
            sources=[],
            related_courses=[],
            timestamp=datetime.now().isoformat()
        )

@mcp.tool()
def get_faq_list(category: Optional[str] = None, limit: int = 10) -> List[FAQItem]:
    """
    获取常见问题列表
    
    Args:
        category: 问题分类（可选）
        limit: 返回数量限制
    
    Returns:
        常见问题列表
    """
    try:
        params = {"limit": str(limit)}
        if category:
            params["category"] = category
        
        response = requests.get(f"{API_BASE_URL}/faq", params=params)
        response.raise_for_status()
        
        faq_list = response.json()
        return [FAQItem(**item) for item in faq_list]
        
    except Exception as e:
        print(f"获取FAQ列表时出错: {e}")
        return []

@mcp.tool()
def search_faq(keyword: str, limit: int = 10) -> List[FAQItem]:
    """
    搜索常见问题
    
    Args:
        keyword: 搜索关键词
        limit: 返回数量限制
    
    Returns:
        匹配的FAQ列表
    """
    try:
        params = {
            "keyword": keyword,
            "limit": str(limit)
        }
        
        response = requests.get(f"{API_BASE_URL}/faq/search", params=params)
        response.raise_for_status()
        
        faq_list = response.json()
        return [FAQItem(**item) for item in faq_list]
        
    except Exception as e:
        print(f"搜索FAQ时出错: {e}")
        return []

@mcp.tool()
def get_course_qa(course_id: int, limit: int = 10) -> List[Dict[str, Any]]:
    """
    获取课程相关的问答
    
    Args:
        course_id: 课程ID
        limit: 返回数量限制
    
    Returns:
        课程问答列表
    """
    try:
        params = {"limit": str(limit)}
        
        response = requests.get(f"{API_BASE_URL}/courses/{course_id}/qa", params=params)
        response.raise_for_status()
        
        return response.json()
        
    except Exception as e:
        print(f"获取课程问答时出错: {e}")
        return []

@mcp.tool()
def submit_question(question: str, user_id: int, course_id: Optional[int] = None) -> bool:
    """
    提交新问题
    
    Args:
        question: 问题内容
        user_id: 用户ID
        course_id: 课程ID（可选）
    
    Returns:
        是否提交成功
    """
    try:
        request_data = {
            "question": question,
            "user_id": user_id
        }
        
        if course_id:
            request_data["course_id"] = str(course_id)
        
        response = requests.post(f"{API_BASE_URL}/qa/submit", json=request_data)
        response.raise_for_status()
        
        return True
        
    except Exception as e:
        print(f"提交问题时出错: {e}")
        return False

@mcp.tool()
def get_qa_categories() -> List[str]:
    """
    获取问答分类列表
    
    Returns:
        分类列表
    """
    try:
        response = requests.get(f"{API_BASE_URL}/qa/categories")
        response.raise_for_status()
        
        return response.json()
        
    except Exception as e:
        print(f"获取问答分类时出错: {e}")
        return []

@mcp.tool()
def get_popular_questions(limit: int = 10) -> List[Dict[str, Any]]:
    """
    获取热门问题
    
    Args:
        limit: 返回数量限制
    
    Returns:
        热门问题列表
    """
    try:
        params = {"limit": str(limit)}
        
        response = requests.get(f"{API_BASE_URL}/qa/popular", params=params)
        response.raise_for_status()
        
        return response.json()
        
    except Exception as e:
        print(f"获取热门问题时出错: {e}")
        return []

@mcp.tool()
def rate_answer(question_id: int, rating: int, user_id: int) -> bool:
    """
    评价答案质量
    
    Args:
        question_id: 问题ID
        rating: 评分（1-5）
        user_id: 用户ID
    
    Returns:
        是否评价成功
    """
    try:
        request_data = {
            "rating": rating,
            "user_id": user_id
        }
        
        response = requests.post(f"{API_BASE_URL}/qa/{question_id}/rate", json=request_data)
        response.raise_for_status()
        
        return True
        
    except Exception as e:
        print(f"评价答案时出错: {e}")
        return False

if __name__ == "__main__":
    mcp.run() 