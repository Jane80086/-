import requests
import json
import random
import string
import time

def random_title():
    return '课程_' + ''.join(random.choices(string.ascii_letters + string.digits, k=6))

BASE_URL = "http://localhost:8080/api/course"

def wait_for_service():
    """等待服务启动"""
    print("等待服务启动...")
    max_retries = 30
    for i in range(max_retries):
        try:
            response = requests.get(f"{BASE_URL}/list", timeout=5)
            if response.status_code == 200:
                print("✅ 服务已启动")
                return True
        except:
            pass
        time.sleep(1)
    print("❌ 服务启动超时")
    return False

def test_basic_crud():
    """测试基本CRUD操作"""
    print("\n=== 基本CRUD操作测试 ===")
    
    # 1. 创建课程
    print("1. 测试创建课程...")
    course_data = {
        "title": "测试课程",
        "description": "这是一个测试课程",
        "instructorId": 1,
        "price": 99.99,
        "duration": 120,
        "category": "技术",
        "status": "DRAFT",
        "coverImage": "http://example.com/cover.jpg",
        "videoUrl": "http://example.com/video.mp4"
    }
    
    response = requests.post(f"{BASE_URL}/create", json=course_data)
    print(f"创建响应: {response.status_code}")
    
    if response.status_code == 200:
        result = response.json()
        course_id = result.get('data', {}).get('id')
        print(f"✅ 创建成功，ID: {course_id}")
        
        # 2. 获取课程详情
        print(f"2. 测试获取课程详情 (ID: {course_id})...")
        detail_response = requests.get(f"{BASE_URL}/{course_id}")
        print(f"详情响应: {detail_response.status_code}")
        
        if detail_response.status_code == 200:
            print("✅ 获取详情成功")
        else:
            print(f"❌ 获取详情失败: {detail_response.text}")
        
        # 3. 更新课程
        print(f"3. 测试更新课程 (ID: {course_id})...")
        update_data = {
            "title": "更新后的测试课程",
            "description": "这是更新后的测试课程",
            "price": 199.99
        }
        
        update_response = requests.put(f"{BASE_URL}/{course_id}", json=update_data)
        print(f"更新响应: {update_response.status_code}")
        
        if update_response.status_code == 200:
            print("✅ 更新成功")
        else:
            print(f"❌ 更新失败: {update_response.text}")
        
        # 4. 删除课程
        print(f"4. 测试删除课程 (ID: {course_id})...")
        delete_response = requests.delete(f"{BASE_URL}/{course_id}")
        print(f"删除响应: {delete_response.status_code}")
        
        if delete_response.status_code == 200:
            print("✅ 删除成功")
        else:
            print(f"❌ 删除失败: {delete_response.text}")
    else:
        print(f"❌ 创建失败: {response.text}")

def test_validation():
    """测试参数校验"""
    print("\n=== 参数校验测试 ===")
    
    # 1. 测试缺少title字段
    print("1. 测试缺少title字段...")
    invalid_data = {
        "description": "缺少标题的课程",
        "price": 99.99
    }
    
    response = requests.post(f"{BASE_URL}/create", json=invalid_data)
    print(f"响应状态码: {response.status_code}")
    
    if response.status_code == 400:
        print("✅ 正确返回400错误码")
    else:
        print(f"❌ 期望400，实际{response.status_code}")
    
    # 2. 测试空标题
    print("2. 测试空标题...")
    empty_title_data = {
        "title": "",
        "description": "空标题的课程",
        "price": 99.99
    }
    
    response = requests.post(f"{BASE_URL}/create", json=empty_title_data)
    print(f"响应状态码: {response.status_code}")
    
    if response.status_code == 400:
        print("✅ 正确返回400错误码")
    else:
        print(f"❌ 期望400，实际{response.status_code}")

def test_list_and_search():
    """测试列表和搜索功能"""
    print("\n=== 列表和搜索功能测试 ===")
    
    # 1. 获取课程列表
    print("1. 测试获取课程列表...")
    response = requests.get(f"{BASE_URL}/list")
    
    if response.status_code == 200:
        result = response.json()
        courses = result.get('data', [])
        print(f"✅ 获取成功，共{len(courses)}个课程")
    else:
        print(f"❌ 获取失败: {response.text}")
    
    # 2. 搜索课程
    print("2. 测试搜索课程...")
    response = requests.get(f"{BASE_URL}/search?keyword=测试")
    
    if response.status_code == 200:
        result = response.json()
        courses = result.get('data', [])
        print(f"✅ 搜索成功，找到{len(courses)}个结果")
    else:
        print(f"❌ 搜索失败: {response.text}")

def test_error_handling():
    """测试错误处理"""
    print("\n=== 错误处理测试 ===")
    
    # 1. 获取不存在的课程
    print("1. 测试获取不存在的课程...")
    response = requests.get(f"{BASE_URL}/99999")
    
    if response.status_code == 404:
        print("✅ 正确返回错误码")
    else:
        print(f"❌ 期望404，实际{response.status_code}")
    
    # 2. 更新不存在的课程
    print("2. 测试更新不存在的课程...")
    update_data = {"title": "不存在的课程"}
    response = requests.put(f"{BASE_URL}/99999", json=update_data)
    
    if response.status_code == 404:
        print("✅ 正确返回错误码")
    else:
        print(f"❌ 期望404，实际{response.status_code}")

def main():
    """主测试函数"""
    print("=== 课程API最终测试 ===")
    
    if not wait_for_service():
        return
    
    test_basic_crud()
    test_validation()
    test_list_and_search()
    test_error_handling()
    
    print("\n=== 所有测试完成 ===")

if __name__ == "__main__":
    main() 