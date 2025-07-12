import requests

modules = {
    "course": "http://localhost:8081"
}

endpoints = {
    "course": [
        ("GET", "/api/course/list", None),
        ("POST", "/api/course/create", {
            "title": "测试课程",
            "description": "描述",
            "instructorId": 1,
            "price": 0.0,
            "duration": 120,
            "level": "BEGINNER",
            "category": "编程开发",
            "status": "已发布",
            "coverImage": "",
            "videoUrl": ""
        }),
        ("GET", "/api/course/1", None),
        ("PUT", "/api/course/1", {
            "title": "新课程名",
            "description": "新描述",
            "instructorId": 1,
            "price": 0.0,
            "duration": 120,
            "level": "BEGINNER",
            "category": "编程开发",
            "status": "已发布",
            "coverImage": "",
            "videoUrl": ""
        }),
        ("DELETE", "/api/course/1", None),
        ("GET", "/api/course/search", {"keyword": "test"}),
        ("POST", "/api/course/1/submit-review", {"adminId": 1}),
        ("POST", "/api/course/1/approve", {"adminId": 1}),
        ("POST", "/api/course/1/reject", {"adminId": 1}),
        ("GET", "/api/course/trends", None),
        ("GET", "/api/course/1/progress", None),
        ("POST", "/api/course/1/progress", {"progress": 50.0}),
        ("POST", "/api/course/1/record", {"currentTime": 10.0, "action": "play", "courseId": 1, "timestamp": 1234567890}),
        ("GET", "/api/course/1/notes", None),
        ("POST", "/api/course/1/notes", {"content": "新笔记", "timestamp": 120.0}),
        ("PUT", "/api/course/1/notes/1", {"content": "新笔记", "timestamp": 130.0}),
        ("DELETE", "/api/course/1/notes/1", None),
        ("GET", "/api/course/1/related", None),
        ("GET", "/api/course/my", None),
        ("PUT", "/api/course/1/unpublish", {"adminId": 1}),
        ("GET", "/api/course/1/review-history", None),
    ]
}

for module, base_url in modules.items():
    print(f"\n=== 检测模块: {module} ===")
    for method, path, data in endpoints.get(module, []):
        url = base_url + path
        try:
            if method == "GET":
                resp = requests.get(url, params=data if data else None, timeout=5)
            elif method == "POST":
                resp = requests.post(url, json=data, timeout=5)
            elif method == "PUT":
                resp = requests.put(url, json=data, timeout=5)
            elif method == "DELETE":
                resp = requests.delete(url, params=data if data else None, timeout=5)
            else:
                print(f"不支持的方法: {method}")
                continue
            print(f"{method} {url}")
            print(f"  状态码: {resp.status_code}")
            print(f"  响应: {resp.text[:300]}")
        except Exception as e:
            print(f"{method} {url} -> 请求失败: {e}")