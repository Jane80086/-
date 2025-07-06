#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Course模块API测试脚本 - 简化版
使用方法: python run_api_tests.py
"""

import requests
import json
import time

def test_api(name, method, url, **kwargs):
    """测试单个API接口"""
    try:
        if method.upper() == 'GET':
            response = requests.get(url, **kwargs)
        elif method.upper() == 'POST':
            response = requests.post(url, **kwargs)
        elif method.upper() == 'PUT':
            response = requests.put(url, **kwargs)
        elif method.upper() == 'DELETE':
            response = requests.delete(url, **kwargs)
        else:
            print(f"❌ {name}: 不支持的HTTP方法 {method}")
            return False
            
        if response.status_code == 200:
            print(f"✅ {name}")
            return True
        else:
            print(f"❌ {name}: HTTP {response.status_code}")
            print(f"   响应: {response.text[:100]}...")
            return False
    except Exception as e:
        print(f"❌ {name}: {str(e)}")
        return False

def main():
    base_url = "http://localhost:8081"
    results = []
    
    print("🚀 开始测试Course模块API接口")
    print(f"📡 目标服务器: {base_url}")
    print("=" * 50)
    
    # 1. 测试课程优化API
    print("\n=== 课程优化API ===")
    results.append(test_api("优化课程标题", "POST", f"{base_url}/api/course-optimization/optimize-title", 
                           params={"originalTitle": "Java编程教程", "category": "编程开发"}))
    
    results.append(test_api("优化课程简介", "POST", f"{base_url}/api/course-optimization/optimize-description", 
                           params={"originalDescription": "Java基础入门课程", "category": "编程开发"}))
    
    results.append(test_api("批量优化课程信息", "POST", f"{base_url}/api/course-optimization/optimize-course-info", 
                           params={"title": "Python数据分析", "description": "Python数据分析入门课程", "category": "数据分析"}))
    
    # 2. 测试课程管理API
    print("\n=== 课程管理API ===")
    results.append(test_api("获取课程列表", "GET", f"{base_url}/api/course/list"))
    
    results.append(test_api("搜索课程", "GET", f"{base_url}/api/course/search", 
                           params={"keyword": "Java"}))
    
    results.append(test_api("AI优化预览", "POST", f"{base_url}/api/course/optimize-preview", 
                           params={"title": "Vue.js前端开发", "description": "Vue.js前端框架开发教程", "category": "前端开发"}))
    
    # 3. 测试创建课程
    print("\n=== 创建课程API ===")
    course_data = {
        "title": "Spring Boot实战教程",
        "description": "Spring Boot框架开发实战课程，从入门到精通",
        "category": "框架开发",
        "instructorId": 1,
        "price": 99.0,
        "duration": 180,
        "level": "INTERMEDIATE",
        "status": 0
    }
    
    create_success = test_api("创建课程（AI优化）", "POST", f"{base_url}/api/course/create", 
                             json=course_data, headers={"Content-Type": "application/json"})
    results.append(create_success)
    
    # 4. 测试推荐API
    print("\n=== 推荐API ===")
    results.append(test_api("获取用户推荐课程", "GET", f"{base_url}/api/recommendations/user", 
                           params={"userId": 1, "limit": 5}))
    
    # 5. 测试AI问答API
    print("\n=== AI问答API ===")
    results.append(test_api("提交AI问题", "POST", f"{base_url}/api/ai-questions/ask", 
                           params={"courseId": 1, "userId": 1, "question": "这个课程的重点是什么？"}))
    
    results.append(test_api("获取课程问题列表", "GET", f"{base_url}/api/ai-questions/course/1"))
    
    results.append(test_api("获取用户问题列表", "GET", f"{base_url}/api/ai-questions/user/1"))
    
    # 6. 测试用户课程API
    print("\n=== 用户课程API ===")
    results.append(test_api("购买课程", "POST", f"{base_url}/api/user-course/purchase", 
                           params={"userId": 1, "courseId": 1}))
    
    results.append(test_api("获取已购课程列表", "GET", f"{base_url}/api/user-course/list", 
                           params={"userId": 1}))
    
    results.append(test_api("删除已购课程", "DELETE", f"{base_url}/api/user-course/delete", 
                           params={"userId": 1, "courseId": 1}))
    
    # 生成测试报告
    print("\n" + "=" * 50)
    print("📊 测试报告")
    print("=" * 50)
    
    total_tests = len(results)
    passed_tests = sum(results)
    failed_tests = total_tests - passed_tests
    
    print(f"总测试数: {total_tests}")
    print(f"通过: {passed_tests} ✅")
    print(f"失败: {failed_tests} ❌")
    print(f"成功率: {(passed_tests/total_tests*100):.1f}%")
    
    if failed_tests > 0:
        print(f"\n⚠️  有 {failed_tests} 个测试失败，请检查服务器状态和API实现")
    else:
        print("\n🎉 所有测试通过！")
    
    print(f"\n💡 提示:")
    print(f"1. 确保course模块服务已启动在 {base_url}")
    print(f"2. 确保数据库连接正常")
    print(f"3. 确保Dify工作流服务可用")

if __name__ == "__main__":
    main() 