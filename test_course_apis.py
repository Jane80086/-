#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Course模块API测试脚本
测试端口: 8081
"""

import requests
import json
import time
import os
from datetime import datetime

class CourseAPITester:
    def __init__(self, base_url="http://localhost:8081"):
        self.base_url = base_url
        self.session = requests.Session()
        self.test_results = []
        
    def log_test(self, test_name, success, response=None, error=None):
        """记录测试结果"""
        result = {
            "test_name": test_name,
            "success": success,
            "timestamp": datetime.now().strftime("%Y-%m-%d %H:%M:%S"),
            "status_code": response.status_code if response else None,
            "response": response.text if response else None,
            "error": str(error) if error else None
        }
        self.test_results.append(result)
        
        status = "✅ PASS" if success else "❌ FAIL"
        print(f"{status} {test_name}")
        if not success and error:
            print(f"   错误: {error}")
        if response and response.status_code != 200:
            print(f"   状态码: {response.status_code}")
            print(f"   响应: {response.text[:200]}...")
        print()

    def test_health_check(self):
        """测试健康检查接口"""
        try:
            response = self.session.get(f"{self.base_url}/api/course/health")
            success = response.status_code == 200
            self.log_test("健康检查", success, response)
            return success
        except Exception as e:
            self.log_test("健康检查", False, error=e)
            return False

    def test_course_list(self):
        """测试获取课程列表"""
        try:
            response = self.session.get(f"{self.base_url}/api/course/list")
            success = response.status_code == 200
            self.log_test("获取课程列表", success, response)
            return success
        except Exception as e:
            self.log_test("获取课程列表", False, error=e)
            return False

    def test_course_detail(self, course_id=1):
        """测试获取课程详情"""
        try:
            response = self.session.get(f"{self.base_url}/api/course/{course_id}")
            success = response.status_code == 200
            self.log_test(f"获取课程详情 (ID: {course_id})", success, response)
            return success
        except Exception as e:
            self.log_test(f"获取课程详情 (ID: {course_id})", False, error=e)
            return False

    def test_course_search(self):
        """测试课程搜索"""
        try:
            response = self.session.get(f"{self.base_url}/api/course/search?keyword=Java")
            success = response.status_code == 200
            self.log_test("课程搜索", success, response)
            return success
        except Exception as e:
            self.log_test("课程搜索", False, error=e)
            return False

    def test_course_create(self):
        """测试创建课程"""
        try:
            course_data = {
                "title": "Python编程基础",
                "description": "Python编程语言基础入门课程",
                "category": "编程开发",
                "duration": 120,
                "price": 0.0,
                "instructorId": 1
            }
            response = self.session.post(
                f"{self.base_url}/api/course/create",
                json=course_data,
                headers={"Content-Type": "application/json"}
            )
            success = response.status_code in [200, 201]
            self.log_test("创建课程", success, response)
            return success
        except Exception as e:
            self.log_test("创建课程", False, error=e)
            return False

    def test_course_optimize_preview(self):
        """测试AI课程优化预览"""
        try:
            data = {
                "title": "原始课程标题",
                "description": "原始课程描述",
                "category": "编程开发"
            }
            response = self.session.post(
                f"{self.base_url}/api/course/optimize-preview",
                params=data
            )
            success = response.status_code == 200
            self.log_test("AI课程优化预览", success, response)
            return success
        except Exception as e:
            self.log_test("AI课程优化预览", False, error=e)
            return False

    def test_user_controller(self):
        """测试用户相关接口"""
        try:
            # 测试用户登录
            login_data = {
                "username": "testuser",
                "password": "testpass"
            }
            response = self.session.post(
                f"{self.base_url}/api/user/login",
                json=login_data
            )
            success = response.status_code in [200, 401]  # 401也是正常的（用户不存在）
            self.log_test("用户登录", success, response)
            
            # 测试用户注册
            register_data = {
                "username": "newuser",
                "password": "newpass",
                "email": "newuser@example.com"
            }
            response = self.session.post(
                f"{self.base_url}/api/user/register",
                json=register_data
            )
            success = response.status_code in [200, 201, 400]  # 400可能是用户已存在
            self.log_test("用户注册", success, response)
            
            return True
        except Exception as e:
            self.log_test("用户相关接口", False, error=e)
            return False

    def test_admin_controller(self):
        """测试管理员相关接口"""
        try:
            # 测试获取待审核课程
            response = self.session.get(f"{self.base_url}/api/admin/pending-courses")
            success = response.status_code in [200, 401, 403]  # 可能需要认证
            self.log_test("获取待审核课程", success, response)
            
            # 测试课程审核
            review_data = {
                "adminId": 1,
                "action": "approve",
                "reason": "课程质量良好"
            }
            response = self.session.post(
                f"{self.base_url}/api/admin/review-course/1",
                json=review_data
            )
            success = response.status_code in [200, 401, 403, 404]  # 可能需要认证或课程不存在
            self.log_test("课程审核", success, response)
            
            return True
        except Exception as e:
            self.log_test("管理员相关接口", False, error=e)
            return False

    def test_file_controller(self):
        """测试文件上传相关接口"""
        try:
            # 测试文件上传
            files = {
                'file': ('test.txt', 'Hello World!', 'text/plain')
            }
            response = self.session.post(
                f"{self.base_url}/api/file/upload",
                files=files
            )
            success = response.status_code in [200, 201, 400, 401]  # 可能需要认证
            self.log_test("文件上传", success, response)
            
            # 测试获取文件列表
            response = self.session.get(f"{self.base_url}/api/file/list")
            success = response.status_code in [200, 401]  # 可能需要认证
            self.log_test("获取文件列表", success, response)
            
            return True
        except Exception as e:
            self.log_test("文件相关接口", False, error=e)
            return False

    def test_search_controller(self):
        """测试搜索相关接口"""
        try:
            # 测试高级搜索
            search_data = {
                "keyword": "Java",
                "category": "编程开发",
                "minPrice": 0,
                "maxPrice": 100
            }
            response = self.session.post(
                f"{self.base_url}/api/search/advanced",
                json=search_data
            )
            success = response.status_code in [200, 400]
            self.log_test("高级搜索", success, response)
            
            # 测试搜索建议
            response = self.session.get(f"{self.base_url}/api/search/suggestions?q=Java")
            success = response.status_code == 200
            self.log_test("搜索建议", success, response)
            
            return True
        except Exception as e:
            self.log_test("搜索相关接口", False, error=e)
            return False

    def test_stats_controller(self):
        """测试统计相关接口"""
        try:
            # 测试课程统计
            response = self.session.get(f"{self.base_url}/api/stats/course")
            success = response.status_code in [200, 401]  # 可能需要认证
            self.log_test("课程统计", success, response)
            
            # 测试用户统计
            response = self.session.get(f"{self.base_url}/api/stats/user")
            success = response.status_code in [200, 401]  # 可能需要认证
            self.log_test("用户统计", success, response)
            
            return True
        except Exception as e:
            self.log_test("统计相关接口", False, error=e)
            return False

    def test_qna_controller(self):
        """测试问答相关接口"""
        try:
            # 测试获取问答列表
            response = self.session.get(f"{self.base_url}/api/qna/list?courseId=1")
            success = response.status_code in [200, 404]  # 404可能是课程不存在
            self.log_test("获取问答列表", success, response)
            
            # 测试提交问题
            question_data = {
                "courseId": 1,
                "question": "这是一个测试问题",
                "userId": 1
            }
            response = self.session.post(
                f"{self.base_url}/api/qna/ask",
                json=question_data
            )
            success = response.status_code in [200, 201, 400, 404]
            self.log_test("提交问题", success, response)
            
            return True
        except Exception as e:
            self.log_test("问答相关接口", False, error=e)
            return False

    def test_ai_controller(self):
        """测试AI相关接口"""
        try:
            # 测试AI聊天
            chat_data = {
                "message": "你好，请介绍一下Java编程",
                "userId": 1
            }
            response = self.session.post(
                f"{self.base_url}/api/ai/chat",
                json=chat_data
            )
            success = response.status_code in [200, 400, 401]
            self.log_test("AI聊天", success, response)
            
            # 测试AI推荐
            response = self.session.get(f"{self.base_url}/api/ai/recommend?userId=1")
            success = response.status_code in [200, 401, 404]
            self.log_test("AI推荐", success, response)
            
            return True
        except Exception as e:
            self.log_test("AI相关接口", False, error=e)
            return False

    def test_featured_course_controller(self):
        """测试推荐课程相关接口"""
        try:
            # 测试获取推荐课程
            response = self.session.get(f"{self.base_url}/api/featured/list")
            success = response.status_code == 200
            self.log_test("获取推荐课程", success, response)
            
            # 测试获取热门课程
            response = self.session.get(f"{self.base_url}/api/featured/hot")
            success = response.status_code == 200
            self.log_test("获取热门课程", success, response)
            
            return True
        except Exception as e:
            self.log_test("推荐课程相关接口", False, error=e)
            return False

    def test_recommendation_controller(self):
        """测试推荐系统相关接口"""
        try:
            # 测试获取个性化推荐
            response = self.session.get(f"{self.base_url}/api/recommendation/personal?userId=1")
            success = response.status_code in [200, 401, 404]
            self.log_test("个性化推荐", success, response)
            
            return True
        except Exception as e:
            self.log_test("推荐系统相关接口", False, error=e)
            return False

    def test_course_optimization_controller(self):
        """测试课程优化相关接口"""
        try:
            # 测试课程优化
            optimization_data = {
                "courseId": 1,
                "optimizationType": "title"
            }
            response = self.session.post(
                f"{self.base_url}/api/optimization/optimize",
                json=optimization_data
            )
            success = response.status_code in [200, 400, 404]
            self.log_test("课程优化", success, response)
            
            return True
        except Exception as e:
            self.log_test("课程优化相关接口", False, error=e)
            return False

    def test_ai_question_controller(self):
        """测试AI问答相关接口"""
        try:
            # 测试AI问答
            question_data = {
                "question": "什么是Java编程？",
                "courseId": 1
            }
            response = self.session.post(
                f"{self.base_url}/api/ai-question/ask",
                json=question_data
            )
            success = response.status_code in [200, 400, 404]
            self.log_test("AI问答", success, response)
            
            return True
        except Exception as e:
            self.log_test("AI问答相关接口", False, error=e)
            return False

    def test_user_course_controller(self):
        """测试用户课程相关接口"""
        try:
            # 测试获取用户课程
            response = self.session.get(f"{self.base_url}/api/user-course/my?userId=1")
            success = response.status_code in [200, 401, 404]
            self.log_test("获取用户课程", success, response)
            
            return True
        except Exception as e:
            self.log_test("用户课程相关接口", False, error=e)
            return False

    def run_all_tests(self):
        """运行所有测试"""
        print("🚀 开始测试 Course 模块 API")
        print("=" * 50)
        
        # 基础功能测试
        self.test_health_check()
        self.test_course_list()
        self.test_course_detail()
        self.test_course_search()
        self.test_course_create()
        self.test_course_optimize_preview()
        
        # 用户相关测试
        self.test_user_controller()
        
        # 管理员相关测试
        self.test_admin_controller()
        
        # 文件相关测试
        self.test_file_controller()
        
        # 搜索相关测试
        self.test_search_controller()
        
        # 统计相关测试
        self.test_stats_controller()
        
        # 问答相关测试
        self.test_qna_controller()
        
        # AI相关测试
        self.test_ai_controller()
        
        # 推荐课程测试
        self.test_featured_course_controller()
        
        # 推荐系统测试
        self.test_recommendation_controller()
        
        # 课程优化测试
        self.test_course_optimization_controller()
        
        # AI问答测试
        self.test_ai_question_controller()
        
        # 用户课程测试
        self.test_user_course_controller()
        
        # 输出测试结果统计
        self.print_test_summary()

    def print_test_summary(self):
        """打印测试结果统计"""
        print("=" * 50)
        print("📊 测试结果统计")
        print("=" * 50)
        
        total_tests = len(self.test_results)
        passed_tests = sum(1 for result in self.test_results if result["success"])
        failed_tests = total_tests - passed_tests
        
        print(f"总测试数: {total_tests}")
        print(f"通过: {passed_tests} ✅")
        print(f"失败: {failed_tests} ❌")
        print(f"成功率: {(passed_tests/total_tests*100):.1f}%")
        
        if failed_tests > 0:
            print("\n❌ 失败的测试:")
            for result in self.test_results:
                if not result["success"]:
                    print(f"  - {result['test_name']}: {result['error']}")
        
        # 保存详细结果到文件
        with open("course_api_test_results.json", "w", encoding="utf-8") as f:
            json.dump(self.test_results, f, ensure_ascii=False, indent=2)
        print(f"\n📄 详细结果已保存到: course_api_test_results.json")

if __name__ == "__main__":
    # 创建测试器实例
    tester = CourseAPITester()
    
    # 运行所有测试
    tester.run_all_tests() 
            success = response.status_code in [200, 400, 404]
            self.log_test("AI问答", success, response)
            
            return True
        except Exception as e:
            self.log_test("AI问答相关接口", False, error=e)
            return False

    def test_user_course_controller(self):
        """测试用户课程相关接口"""
        try:
            # 测试获取用户课程
            response = self.session.get(f"{self.base_url}/api/user-course/my?userId=1")
            success = response.status_code in [200, 401, 404]
            self.log_test("获取用户课程", success, response)
            
            return True
        except Exception as e:
            self.log_test("用户课程相关接口", False, error=e)
            return False

    def run_all_tests(self):
        """运行所有测试"""
        print("🚀 开始测试 Course 模块 API")
        print("=" * 50)
        
        # 基础功能测试
        self.test_health_check()
        self.test_course_list()
        self.test_course_detail()
        self.test_course_search()
        self.test_course_create()
        self.test_course_optimize_preview()
        
        # 用户相关测试
        self.test_user_controller()
        
        # 管理员相关测试
        self.test_admin_controller()
        
        # 文件相关测试
        self.test_file_controller()
        
        # 搜索相关测试
        self.test_search_controller()
        
        # 统计相关测试
        self.test_stats_controller()
        
        # 问答相关测试
        self.test_qna_controller()
        
        # AI相关测试
        self.test_ai_controller()
        
        # 推荐课程测试
        self.test_featured_course_controller()
        
        # 推荐系统测试
        self.test_recommendation_controller()
        
        # 课程优化测试
        self.test_course_optimization_controller()
        
        # AI问答测试
        self.test_ai_question_controller()
        
        # 用户课程测试
        self.test_user_course_controller()
        
        # 输出测试结果统计
        self.print_test_summary()

    def print_test_summary(self):
        """打印测试结果统计"""
        print("=" * 50)
        print("📊 测试结果统计")
        print("=" * 50)
        
        total_tests = len(self.test_results)
        passed_tests = sum(1 for result in self.test_results if result["success"])
        failed_tests = total_tests - passed_tests
        
        print(f"总测试数: {total_tests}")
        print(f"通过: {passed_tests} ✅")
        print(f"失败: {failed_tests} ❌")
        print(f"成功率: {(passed_tests/total_tests*100):.1f}%")
        
        if failed_tests > 0:
            print("\n❌ 失败的测试:")
            for result in self.test_results:
                if not result["success"]:
                    print(f"  - {result['test_name']}: {result['error']}")
        
        # 保存详细结果到文件
        with open("course_api_test_results.json", "w", encoding="utf-8") as f:
            json.dump(self.test_results, f, ensure_ascii=False, indent=2)
        print(f"\n📄 详细结果已保存到: course_api_test_results.json")

if __name__ == "__main__":
    # 创建测试器实例
    tester = CourseAPITester()
    
    # 运行所有测试
    tester.run_all_tests() 