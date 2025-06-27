#!/usr/bin/env python3
"""
测试完整的课程推荐系统
包括MCP服务器和Spring Boot应用
"""

import requests
import json
import time
from datetime import datetime

# 配置
SPRING_BOOT_URL = "http://localhost:8080/api"
MCP_SERVER_URL = "http://localhost:6277"

def test_spring_boot_recommendations():
    """测试Spring Boot推荐API"""
    print("=== 测试Spring Boot推荐API ===")
    
    # 测试MCP服务器状态
    try:
        response = requests.get(f"{SPRING_BOOT_URL}/recommendations/mcp-status")
        if response.status_code == 200:
            result = response.json()
            print(f"MCP服务器状态: {result['message']}")
        else:
            print("无法获取MCP服务器状态")
    except Exception as e:
        print(f"测试MCP状态失败: {e}")
    
    # 测试热度分析
    try:
        response = requests.get(f"{SPRING_BOOT_URL}/recommendations/heat-analysis?time_range=7d")
        if response.status_code == 200:
            result = response.json()
            print(f"热度分析结果: {len(result)}个课程")
            for course in result[:3]:  # 显示前3个
                print(f"  - {course.get('title', 'Unknown')}: 热度={course.get('heat_score', 0):.2f}")
        else:
            print("热度分析失败")
    except Exception as e:
        print(f"测试热度分析失败: {e}")
    
    # 测试热门课程
    try:
        response = requests.get(f"{SPRING_BOOT_URL}/recommendations/hot?limit=5")
        if response.status_code == 200:
            result = response.json()
            print(f"热门课程: {len(result)}个")
            for course in result:
                print(f"  - {course.get('title', 'Unknown')} ({course.get('recommendation_type', 'Unknown')})")
        else:
            print("获取热门课程失败")
    except Exception as e:
        print(f"测试热门课程失败: {e}")
    
    # 测试趋势课程
    try:
        response = requests.get(f"{SPRING_BOOT_URL}/recommendations/trending?limit=3")
        if response.status_code == 200:
            result = response.json()
            print(f"趋势课程: {len(result)}个")
            for course in result:
                print(f"  - {course.get('title', 'Unknown')}: 热度={course.get('heat_score', 0):.2f}")
        else:
            print("获取趋势课程失败")
    except Exception as e:
        print(f"测试趋势课程失败: {e}")
    
    # 测试首页推荐
    try:
        response = requests.get(f"{SPRING_BOOT_URL}/recommendations/homepage?limit=6")
        if response.status_code == 200:
            result = response.json()
            print(f"首页推荐:")
            print(f"  热门课程: {len(result.get('hot_courses', []))}个")
            print(f"  趋势课程: {len(result.get('trending_courses', []))}个")
            print(f"  个性化课程: {len(result.get('personalized_courses', []))}个")
        else:
            print("获取首页推荐失败")
    except Exception as e:
        print(f"测试首页推荐失败: {e}")
    
    # 测试推荐统计
    try:
        response = requests.get(f"{SPRING_BOOT_URL}/recommendations/stats")
        if response.status_code == 200:
            result = response.json()
            print(f"推荐统计:")
            print(f"  热门课程数: {result.get('hot_courses_count', 0)}")
            print(f"  趋势课程数: {result.get('trending_courses_count', 0)}")
            print(f"  分析课程数: {result.get('total_courses_analyzed', 0)}")
            print(f"  平均热度: {result.get('average_heat_score', 0):.2f}")
        else:
            print("获取推荐统计失败")
    except Exception as e:
        print(f"测试推荐统计失败: {e}")

def test_mcp_server_directly():
    """直接测试MCP服务器"""
    print("\n=== 直接测试MCP服务器 ===")
    
    # 测试热度分析
    try:
        response = requests.post(f"{MCP_SERVER_URL}/tools/analyze_course_heat", json={
            "time_range": "7d"
        })
        if response.status_code == 200:
            result = response.json()
            print(f"MCP热度分析: {len(result)}个课程")
        else:
            print("MCP热度分析失败")
    except Exception as e:
        print(f"MCP热度分析失败: {e}")
    
    # 测试课程推荐
    try:
        response = requests.post(f"{MCP_SERVER_URL}/tools/generate_course_recommendations", json={
            "user_id": 1,
            "limit": 5
        })
        if response.status_code == 200:
            result = response.json()
            print(f"MCP课程推荐: {len(result)}个")
        else:
            print("MCP课程推荐失败")
    except Exception as e:
        print(f"MCP课程推荐失败: {e}")
    
    # 测试更新推荐课程
    try:
        response = requests.post(f"{MCP_SERVER_URL}/tools/update_featured_courses", json={
            "auto_update": True
        })
        if response.status_code == 200:
            result = response.json()
            print(f"MCP更新推荐: {result.get('message', 'Unknown')}")
        else:
            print("MCP更新推荐失败")
    except Exception as e:
        print(f"MCP更新推荐失败: {e}")

def test_category_recommendations():
    """测试分类推荐"""
    print("\n=== 测试分类推荐 ===")
    
    categories = ["技术", "管理", "营销"]
    
    for category in categories:
        try:
            response = requests.get(f"{SPRING_BOOT_URL}/recommendations/by-category/{category}?limit=3")
            if response.status_code == 200:
                result = response.json()
                print(f"{category}分类推荐:")
                print(f"  热门: {len(result.get('hot_courses', []))}个")
                print(f"  趋势: {len(result.get('trending_courses', []))}个")
            else:
                print(f"{category}分类推荐失败")
        except Exception as e:
            print(f"{category}分类推荐失败: {e}")

def test_personalized_recommendations():
    """测试个性化推荐"""
    print("\n=== 测试个性化推荐 ===")
    
    user_ids = [1, 2, 3]
    
    for user_id in user_ids:
        try:
            response = requests.get(f"{SPRING_BOOT_URL}/recommendations/personalized?userId={user_id}&limit=3")
            if response.status_code == 200:
                result = response.json()
                print(f"用户{user_id}个性化推荐: {len(result)}个课程")
            else:
                print(f"用户{user_id}个性化推荐失败")
        except Exception as e:
            print(f"用户{user_id}个性化推荐失败: {e}")

def check_services_status():
    """检查服务状态"""
    print("=== 检查服务状态 ===")
    
    # 检查Spring Boot应用
    try:
        response = requests.get(f"{SPRING_BOOT_URL.replace('/api', '')}/actuator/health", timeout=5)
        if response.status_code == 200:
            print("✅ Spring Boot应用运行正常")
        else:
            print("❌ Spring Boot应用状态异常")
    except Exception as e:
        print(f"❌ Spring Boot应用连接失败: {e}")
    
    # 检查MCP服务器
    try:
        response = requests.get(f"{MCP_SERVER_URL}/health", timeout=5)
        if response.status_code == 200:
            print("✅ MCP服务器运行正常")
        else:
            print("❌ MCP服务器状态异常")
    except Exception as e:
        print(f"❌ MCP服务器连接失败: {e}")

def main():
    """主测试函数"""
    print("=" * 60)
    print("课程推荐系统完整测试")
    print(f"测试时间: {datetime.now()}")
    print("=" * 60)
    
    # 检查服务状态
    check_services_status()
    
    # 等待服务启动
    print("\n等待服务启动...")
    time.sleep(2)
    
    try:
        # 测试各项功能
        test_spring_boot_recommendations()
        test_mcp_server_directly()
        test_category_recommendations()
        test_personalized_recommendations()
        
        print("\n" + "=" * 60)
        print("✅ 测试完成")
        print("=" * 60)
        
    except Exception as e:
        print(f"\n❌ 测试过程中出现错误: {e}")

if __name__ == "__main__":
    main() 