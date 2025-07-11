#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import requests
import json

# 配置
BASE_URL = "http://localhost:8081"
COURSE_ID = 1

def test_ai_qna():
    """测试AI问答功能"""
    
    print("=== 测试课程AI问答功能 ===")
    
    # 1. 测试提交问题
    print("\n1. 测试提交AI问题...")
    question_data = {
        "question": "这个课程适合初学者吗？",
        "userId": 1
    }
    
    try:
        response = requests.post(
            f"{BASE_URL}/api/course/{COURSE_ID}/ai-qna",
            json=question_data,
            headers={"Content-Type": "application/json"}
        )
        
        if response.status_code == 200:
            result = response.json()
            print(f"✅ 提交问题成功: {result}")
            
            # 提取AI回复
            if result.get("data") and result["data"].get("aiReply"):
                print(f"🤖 AI回复: {result['data']['aiReply']}")
        else:
            print(f"❌ 提交问题失败: {response.status_code} - {response.text}")
            
    except Exception as e:
        print(f"❌ 请求异常: {e}")
    
    # 2. 测试获取AI问答历史
    print("\n2. 测试获取AI问答历史...")
    
    try:
        response = requests.get(
            f"{BASE_URL}/api/course/{COURSE_ID}/ai-qna",
            params={"current": 1, "size": 10}
        )
        
        if response.status_code == 200:
            result = response.json()
            print(f"✅ 获取AI问答历史成功: {result}")
        else:
            print(f"❌ 获取AI问答历史失败: {response.status_code} - {response.text}")
            
    except Exception as e:
        print(f"❌ 请求异常: {e}")
    
    # 3. 测试QnA接口
    print("\n3. 测试QnA接口...")
    
    try:
        response = requests.get(f"{BASE_URL}/api/qna/course/{COURSE_ID}")
        
        if response.status_code == 200:
            result = response.json()
            print(f"✅ 获取QnA列表成功: {result}")
        else:
            print(f"❌ 获取QnA列表失败: {response.status_code} - {response.text}")
            
    except Exception as e:
        print(f"❌ 请求异常: {e}")

def test_course_list():
    """测试课程列表接口"""
    print("\n=== 测试课程列表接口 ===")
    
    try:
        response = requests.get(f"{BASE_URL}/api/course/list")
        
        if response.status_code == 200:
            result = response.json()
            print(f"✅ 获取课程列表成功: {result}")
        else:
            print(f"❌ 获取课程列表失败: {response.status_code} - {response.text}")
            
    except Exception as e:
        print(f"❌ 请求异常: {e}")

if __name__ == "__main__":
    # 测试课程列表
    test_course_list()
    
    # 测试AI问答
    test_ai_qna()
    
    print("\n=== 测试完成 ===") 