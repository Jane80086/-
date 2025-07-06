#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Course Module API 测试脚本 - 修复版
测试所有 course 模块的 API 接口
"""

import requests
import json
import time
from datetime import datetime

# 配置
BASE_URL = "http://localhost:8080"
HEADERS = {
    "Content-Type": "application/json",
    "Authorization": "Bearer test-token"
}

# 测试结果统计
test_results = {
    "total": 0,
    "passed": 0,
    "failed": 0,
    "errors": []
}

def log_test(test_name, success, error=None):
    """记录测试结果"""
    test_results["total"] += 1
    if success:
        test_results["passed"] += 1
        print(f"✅ {test_name}")
    else:
        test_results["failed"] += 1
        test_results["errors"].append(f"{test_name}: {error}")
        print(f"❌ {test_name}: {error}")

def test_health_check():
    """测试健康检查"""
    try:
        response = requests.get(f"{BASE_URL}/actuator/health", timeout=5)
        log_test("健康检查", response.status_code == 200, f"状态码: {response.status_code}")
    except Exception as e:
        log_test("健康检查", False, str(e))

def test_course_crud():
    """测试课程CRUD操作"""
    # 创建课程
    course_data = {
        "title": "测试课程",
        "description": "这是一个测试课程",
        "category": "编程开发",
        "price": 99.0,
        "duration": 120,
        "instructorId": 1
    }
    
    try:
        response = requests.post(f"{BASE_URL}/api/course/create", 
                               json=course_data, headers=HEADERS, timeout=10)
        log_test("创建课程", response.status_code in [200, 201], f"状态码: {response.status_code}")
        
        if response.status_code in [200, 201]:
            result = response.json()
            course_id = result.get("data", {}).get("id")
            if course_id:
                # 获取课程详情
                response = requests.get(f"{BASE_URL}/api/course/{course_id}", headers=HEADERS)
                log_test("获取课程详情", response.status_code == 200, f"状态码: {response.status_code}")
                
                # 更新课程
                update_data = {"title": "更新后的测试课程"}
                response = requests.put(f"{BASE_URL}/api/course/{course_id}", 
                                      json=update_data, headers=HEADERS)
                log_test("更新课程", response.status_code == 200, f"状态码: {response.status_code}")
                
                # 删除课程
                response = requests.delete(f"{BASE_URL}/api/course/{course_id}", headers=HEADERS)
                log_test("删除课程", response.status_code == 200, f"状态码: {response.status_code}")
            else:
                log_test("获取课程详情", False, "未获取到课程ID")
        else:
            log_test("获取课程详情", False, "创建课程失败")
            log_test("更新课程", False, "创建课程失败")
            log_test("删除课程", False, "创建课程失败")
            
    except Exception as e:
        log_test("创建课程", False, str(e))
        log_test("获取课程详情", False, str(e))
        log_test("更新课程", False, str(e))
        log_test("删除课程", False, str(e))

def test_course_list():
    """测试课程列表"""
    try:
        response = requests.get(f"{BASE_URL}/api/course/list", headers=HEADERS, timeout=5)
        log_test("获取课程列表", response.status_code == 200, f"状态码: {response.status_code}")
    except Exception as e:
        log_test("获取课程列表", False, str(e))

def test_search_apis():
    """测试搜索相关API"""
    # 基础搜索
    try:
        response = requests.get(f"{BASE_URL}/api/search/courses?keyword=Java", headers=HEADERS)
        log_test("课程搜索", response.status_code == 200, f"状态码: {response.status_code}")
    except Exception as e:
        log_test("课程搜索", False, str(e))
    
    # 高级搜索 - 使用POST请求
    try:
        search_data = {
            "keyword": "Java",
            "category": "编程开发",
            "level": "初级"
        }
        response = requests.post(f"{BASE_URL}/api/search/advanced", json=search_data, headers=HEADERS)
        log_test("高级搜索", response.status_code == 200, f"状态码: {response.status_code}")
    except Exception as e:
        log_test("高级搜索", False, str(e))
    
    # 搜索建议
    try:
        response = requests.get(f"{BASE_URL}/api/search/suggestions?q=Java", headers=HEADERS)
        log_test("搜索建议", response.status_code == 200, f"状态码: {response.status_code}")
    except Exception as e:
        log_test("搜索建议", False, str(e))
    
    # 热门关键词
    try:
        response = requests.get(f"{BASE_URL}/api/search/hot-keywords", headers=HEADERS)
        log_test("热门关键词", response.status_code == 200, f"状态码: {response.status_code}")
    except Exception as e:
        log_test("热门关键词", False, str(e))

def test_file_apis():
    """测试文件相关API"""
    # 文件列表
    try:
        response = requests.get(f"{BASE_URL}/api/file/list", headers=HEADERS)
        log_test("获取文件列表", response.status_code == 200, f"状态码: {response.status_code}")
    except Exception as e:
        log_test("获取文件列表", False, str(e))
    
    # 文件上传 - 创建测试文件
    try:
        files = {'file': ('test.txt', 'Hello World', 'text/plain')}
        response = requests.post(f"{BASE_URL}/api/file/upload", files=files, headers={})
        log_test("文件上传", response.status_code == 200, f"状态码: {response.status_code}")
    except Exception as e:
        log_test("文件上传", False, str(e))
    
    # 检查文件状态
    try:
        response = requests.get(f"{BASE_URL}/api/file/status", headers=HEADERS)
        log_test("文件服务状态", response.status_code == 200, f"状态码: {response.status_code}")
    except Exception as e:
        log_test("文件服务状态", False, str(e))

def test_admin_apis():
    """测试管理员API"""
    # 获取用户列表
    try:
        response = requests.get(f"{BASE_URL}/api/admin/users", headers=HEADERS)
        log_test("获取用户列表", response.status_code == 200, f"状态码: {response.status_code}")
    except Exception as e:
        log_test("获取用户列表", False, str(e))
    
    # 获取待审核课程
    try:
        response = requests.get(f"{BASE_URL}/api/admin/courses/pending", headers=HEADERS)
        log_test("获取待审核课程", response.status_code == 200, f"状态码: {response.status_code}")
    except Exception as e:
        log_test("获取待审核课程", False, str(e))
    
    # 获取管理员仪表板
    try:
        response = requests.get(f"{BASE_URL}/api/admin/dashboard", headers=HEADERS)
        log_test("管理员仪表板", response.status_code == 200, f"状态码: {response.status_code}")
    except Exception as e:
        log_test("管理员仪表板", False, str(e))

def test_stats_apis():
    """测试统计API"""
    # 统计数据概览
    try:
        response = requests.get(f"{BASE_URL}/api/stats/overview", headers=HEADERS)
        log_test("统计数据概览", response.status_code == 200, f"状态码: {response.status_code}")
    except Exception as e:
        log_test("统计数据概览", False, str(e))
    
    # 图表数据
    try:
        response = requests.get(f"{BASE_URL}/api/stats/chart", headers=HEADERS)
        log_test("图表数据", response.status_code == 200, f"状态码: {response.status_code}")
    except Exception as e:
        log_test("图表数据", False, str(e))
    
    # 仪表板数据
    try:
        response = requests.get(f"{BASE_URL}/api/stats/dashboard", headers=HEADERS)
        log_test("仪表板数据", response.status_code == 200, f"状态码: {response.status_code}")
    except Exception as e:
        log_test("仪表板数据", False, str(e))
    
    # 系统健康状态
    try:
        response = requests.get(f"{BASE_URL}/api/stats/health", headers=HEADERS)
        log_test("系统健康状态", response.status_code == 200, f"状态码: {response.status_code}")
    except Exception as e:
        log_test("系统健康状态", False, str(e))

def test_ai_apis():
    """测试AI相关API"""
    # AI聊天
    try:
        chat_data = {
            "message": "你好，请介绍一下Java编程",
            "sessionId": "test-session-123"
        }
        response = requests.post(f"{BASE_URL}/api/ai/chat", json=chat_data, headers=HEADERS)
        log_test("AI聊天", response.status_code == 200, f"状态码: {response.status_code}")
    except Exception as e:
        log_test("AI聊天", False, str(e))
    
    # AI优化
    try:
        optimize_data = {
            "title": "Java基础教程",
            "description": "Java编程入门课程"
        }
        response = requests.post(f"{BASE_URL}/api/ai/optimize", json=optimize_data, headers=HEADERS)
        log_test("AI优化", response.status_code == 200, f"状态码: {response.status_code}")
    except Exception as e:
        log_test("AI优化", False, str(e))
    
    # AI状态
    try:
        response = requests.get(f"{BASE_URL}/api/ai/status", headers=HEADERS)
        log_test("AI状态", response.status_code == 200, f"状态码: {response.status_code}")
    except Exception as e:
        log_test("AI状态", False, str(e))

def test_user_apis():
    """测试用户相关API"""
    # 用户注册
    try:
        user_data = {
            "username": f"testuser{int(time.time())}",
            "password": "123456",
            "email": f"test{int(time.time())}@example.com"
        }
        response = requests.post(f"{BASE_URL}/api/user/register", json=user_data, headers=HEADERS)
        log_test("用户注册", response.status_code in [200, 201], f"状态码: {response.status_code}")
    except Exception as e:
        log_test("用户注册", False, str(e))
    
    # 用户登录
    try:
        login_data = {
            "username": "testuser",
            "password": "123456"
        }
        response = requests.post(f"{BASE_URL}/api/user/login", json=login_data, headers=HEADERS)
        log_test("用户登录", response.status_code == 200, f"状态码: {response.status_code}")
    except Exception as e:
        log_test("用户登录", False, str(e))
    
    # 获取用户信息
    try:
        response = requests.get(f"{BASE_URL}/api/user/profile", headers=HEADERS)
        log_test("获取用户信息", response.status_code == 200, f"状态码: {response.status_code}")
    except Exception as e:
        log_test("获取用户信息", False, str(e))

def test_qna_apis():
    """测试问答相关API"""
    # 获取问答列表
    try:
        response = requests.get(f"{BASE_URL}/api/qna/list?courseId=1", headers=HEADERS)
        log_test("获取问答列表", response.status_code == 200, f"状态码: {response.status_code}")
    except Exception as e:
        log_test("获取问答列表", False, str(e))
    
    # 创建问答
    try:
        qna_data = {
            "courseId": 1,
            "question": "这是一个测试问题",
            "content": "这是问题的详细内容"
        }
        response = requests.post(f"{BASE_URL}/api/qna/create", json=qna_data, headers=HEADERS)
        log_test("创建问答", response.status_code == 200, f"状态码: {response.status_code}")
    except Exception as e:
        log_test("创建问答", False, str(e))

def test_recommendation_apis():
    """测试推荐相关API"""
    # 获取推荐课程
    try:
        response = requests.get(f"{BASE_URL}/api/recommendation/courses?userId=1", headers=HEADERS)
        log_test("获取推荐课程", response.status_code == 200, f"状态码: {response.status_code}")
    except Exception as e:
        log_test("获取推荐课程", False, str(e))
    
    # 获取热门课程
    try:
        response = requests.get(f"{BASE_URL}/api/recommendation/popular", headers=HEADERS)
        log_test("获取热门课程", response.status_code == 200, f"状态码: {response.status_code}")
    except Exception as e:
        log_test("获取热门课程", False, str(e))

def test_optimization_apis():
    """测试优化相关API"""
    # 课程优化预览
    try:
        response = requests.post(f"{BASE_URL}/api/course/optimize-preview", 
                               params={"title": "测试课程", "description": "测试描述"}, 
                               headers=HEADERS)
        log_test("课程优化预览", response.status_code == 200, f"状态码: {response.status_code}")
    except Exception as e:
        log_test("课程优化预览", False, str(e))

def main():
    """主测试函数"""
    print("🚀 开始测试 Course Module API...")
    print(f"📅 测试时间: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
    print(f"🌐 测试地址: {BASE_URL}")
    print("=" * 60)
    
    # 执行所有测试
    test_health_check()
    test_course_crud()
    test_course_list()
    test_search_apis()
    test_file_apis()
    test_admin_apis()
    test_stats_apis()
    test_ai_apis()
    test_user_apis()
    test_qna_apis()
    test_recommendation_apis()
    test_optimization_apis()
    
    # 输出测试结果
    print("=" * 60)
    print("📊 测试结果汇总:")
    print(f"总测试数: {test_results['total']}")
    print(f"通过: {test_results['passed']}")
    print(f"失败: {test_results['failed']}")
    
    if test_results['failed'] > 0:
        success_rate = (test_results['passed'] / test_results['total']) * 100
        print(f"成功率: {success_rate:.1f}%")
        print("\n❌ 失败的测试:")
        for error in test_results['errors']:
            print(f"  - {error}")
    else:
        print("🎉 所有测试通过!")
    
    print(f"\n⏰ 测试完成时间: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")

if __name__ == "__main__":
    main() 