#!/usr/bin/env python3
"""
简化的 MCP Course Optimizer Server 启动脚本
"""

import asyncio
import uvicorn
import anyio
from mcp.server.fastmcp import FastMCP
from mcp.server.stdio import stdio_server

# 重新创建MCP服务器实例
mcp = FastMCP("Course Optimizer")

# 重新定义工具函数
@mcp.tool()
def optimize_course_content(
    original_title: str,
    original_description: str,
    target_audience: str = "初学者",
    course_type: str = "技术"
) -> dict:
    """使用AI优化课程标题和描述"""
    
    # 模拟优化逻辑
    optimized_title = f"【{target_audience}必学】{original_title.replace('课程', '实战教程')}"
    optimized_description = f"专为{target_audience}设计的{course_type}课程。{original_description}通过系统化学习和实战练习，让你快速掌握核心技能。"
    
    return {
        "optimized_title": optimized_title,
        "optimized_description": optimized_description,
        "improvement_suggestions": [
            "使用更具吸引力的动词开头",
            "添加具体的学习成果",
            "包含目标受众关键词"
        ],
        "seo_keywords": [course_type, "课程", "学习", "培训"]
    }


@mcp.tool()
def analyze_course_seo(title: str, description: str) -> dict:
    """分析课程SEO效果"""
    title_length = len(title)
    description_length = len(description)
    
    seo_score = 0
    if 30 <= title_length <= 60:
        seo_score += 40
    if 150 <= description_length <= 300:
        seo_score += 40
    if "课程" in title or "学习" in title:
        seo_score += 20
    
    return {
        "seo_score": seo_score,
        "title_length": title_length,
        "description_length": description_length,
        "grade": "优秀" if seo_score >= 80 else "良好" if seo_score >= 60 else "需要改进"
    }


if __name__ == "__main__":
    print("🚀 启动 MCP Course Optimizer Server")
    print("📍 使用 stdio 传输模式")
    mcp.run() 