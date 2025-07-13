import requests
from config import DIFY_API_KEY, DIFY_BASE_URL

def ask_dify(question):
    headers = {
        "Authorization": f"Bearer {DIFY_API_KEY}",
        "Content-Type": "application/json"
    }
    data = {
        "inputs": {},
        "query": question,
        "response_mode": "blocking",  # 使用 blocking 模式而不是 streaming
        "conversation_id": "",
        "user": "test_user",
        "files": []
    }
    
    try:
        print(f"[DEBUG] 发送请求到 Dify: {DIFY_BASE_URL}/chat-messages")
        print(f"[DEBUG] 请求数据: {data}")
        
        resp = requests.post(f"{DIFY_BASE_URL}/chat-messages", json=data, headers=headers, timeout=120)
        resp.encoding = 'utf-8'
        
        print(f"[DEBUG] Dify 响应状态码: {resp.status_code}")
        print(f"[DEBUG] Dify 响应头: {dict(resp.headers)}")
        
        if resp.status_code != 200:
            print(f"[DEBUG] Dify 错误响应: {resp.text}")
            return f"Dify 服务错误: HTTP {resp.status_code} - {resp.text}"
        
        try:
            result = resp.json()
            print(f"[DEBUG] Dify JSON 响应: {result}")
            
            # 检查不同的响应格式
            answer = None
            if "answer" in result:
                answer = result["answer"]
            elif "choices" in result and len(result["choices"]) > 0:
                choice = result["choices"][0]
                if "message" in choice:
                    answer = choice["message"].get("content", "")
                else:
                    answer = choice.get("content", "")
            elif "data" in result:
                answer = result["data"].get("answer", "")
            
            if not answer:
                print(f"[DEBUG] 未找到答案，完整响应: {result}")
                return "Dify 返回格式异常，未找到答案"
            
            # 过滤 Dify 内部编码异常
            if "latin-1' codec can't encode characters" in str(answer):
                return "AI服务内部编码异常，请联系管理员修复 Dify 服务端编码配置"
            
            return answer
            
        except Exception as json_error:
            print(f"[DEBUG] JSON 解析失败: {json_error}")
            print(f"[DEBUG] 原始响应内容: {resp.text}")
            return f"Dify 响应格式异常: {str(json_error)}"
            
    except Exception as e:
        print(f"[DEBUG] Dify 请求异常: {e}")
        return f"Dify 服务异常: {str(e)}" 