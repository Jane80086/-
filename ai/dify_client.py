import requests

DIFY_API_KEY = "app-0aNJ1b5OVdl8fYZIW7f3K4WZ"
DIFY_BASE_URL = "http://localhost:8088/v1"


def ask_ai(question, context=None):
    import json
    headers = {"Authorization": f"Bearer {DIFY_API_KEY}"}
    data = {
        "inputs": {},
        "query": question,
        "response_mode": "blocking",  # 尝试用blocking模式提升速度
        "conversation_id": "",
        "user": "test_user",
        "files": []
    }
    resp = requests.post(f"{DIFY_BASE_URL}/chat-messages", json=data, headers=headers, timeout=10)
    try:
        return resp.json()
    except Exception:
        answers = []
        for line in resp.text.splitlines():
            line = line.strip()
            if line.startswith('data:'):
                try:
                    obj = json.loads(line[5:].strip())
                    ans = obj.get('answer', '')
                    # 过滤掉<think>、空字符串、None等
                    if ans and not ans.strip().startswith('<') and ans.strip() != '':
                        answers.append(ans.replace('\\n', '\n').replace('\\', ''))
                except Exception:
                    pass
        final_answer = ''.join(answers).replace('\n\n', '\n').strip()
        return {"answer": final_answer}


def optimize_title_desc(title, description):
    headers = {"Authorization": f"Bearer {DIFY_API_KEY}"}
    data = {"title": title, "description": description}
    resp = requests.post(f"{DIFY_BASE_URL}/course/optimize", json=data, headers=headers)
    resp.raise_for_status()
    return resp.json() 