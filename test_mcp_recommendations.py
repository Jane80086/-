#!/usr/bin/env python3
"""
测试MCP服务器的课程推荐功能
"""

import requests
import json
from datetime import datetime

# MCP服务器配置
MCP_SERVER_URL = "http://localhost:6277"

def test_course_heat_analysis():
    """测试课程热度分析"""
    print("=== 测试课程热度分析 ===")
    
    # 分析所有课程热度
    response = requests.post(f"{MCP_SERVER_URL}/tools/analyze_course_heat", json={
        "time_range": "7d"
    })
    
    if response.status_code == 200:
        result = response.json()
        print("热度分析结果:")
        for course in result:
            print(f"- {course['title']}: 热度分数={course['heat_score']:.2f}, 趋势={course['trend']}")
    else:
        print(f"热度分析失败: {response.status_code}")

def test_course_recommendations():
    """测试课程推荐"""
    print("\n=== 测试课程推荐 ===")
    
    # 生成推荐
    response = requests.post(f"{MCP_SERVER_URL}/tools/generate_course_recommendations", json={
        "user_id": 1,
        "category": "技术",
        "limit": 5
    })
    
    if response.status_code == 200:
        result = response.json()
        print("推荐课程:")
        for course in result:
            print(f"- {course['title']} ({course['recommendation_type']}): {course['reason']}")
    else:
        print(f"课程推荐失败: {response.status_code}")

def test_update_featured_courses():
    """测试更新推荐课程"""
    print("\n=== 测试更新推荐课程 ===")
    
    # 自动更新推荐课程
    response = requests.post(f"{MCP_SERVER_URL}/tools/update_featured_courses", json={
        "auto_update": True
    })
    
    if response.status_code == 200:
        result = response.json()
        print(f"更新结果: {result['message']}")
        print(f"更新的课程ID: {result['course_ids']}")
    else:
        print(f"更新推荐课程失败: {response.status_code}")

def test_personalized_recommendations():
    """测试个性化推荐"""
    print("\n=== 测试个性化推荐 ===")
    
    # 为特定用户生成个性化推荐
    response = requests.post(f"{MCP_SERVER_URL}/tools/generate_course_recommendations", json={
        "user_id": 123,
        "limit": 8
    })
    
    if response.status_code == 200:
        result = response.json()
        print("个性化推荐:")
        for course in result:
            if course['recommendation_type'] == '个性化':
                print(f"- {course['title']}: {course['reason']}")
    else:
        print(f"个性化推荐失败: {response.status_code}")

def test_trending_courses():
    """测试趋势课程分析"""
    print("\n=== 测试趋势课程分析 ===")
    
    # 分析趋势课程
    response = requests.post(f"{MCP_SERVER_URL}/tools/analyze_course_heat", json={
        "time_range": "1d"
    })
    
    if response.status_code == 200:
        result = response.json()
        trending_courses = [c for c in result if c['trend'] == '上升']
        print("趋势上升的课程:")
        for course in trending_courses:
            print(f"- {course['title']}: 热度分数={course['heat_score']:.2f}")
    else:
        print(f"趋势分析失败: {response.status_code}")

def main():
    """主测试函数"""
    print("开始测试MCP课程推荐功能...")
    print(f"测试时间: {datetime.now()}")
    
    try:
        # 测试各项功能
        test_course_heat_analysis()
        test_course_recommendations()
        test_update_featured_courses()
        test_personalized_recommendations()
        test_trending_courses()
        
        print("\n=== 测试完成 ===")
        
    except Exception as e:
        print(f"测试过程中出现错误: {e}")

if __name__ == "__main__":
    main() 