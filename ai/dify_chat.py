import requests
import sys
import json
import os

DIFY_API_URL = 'http://localhost:8088/v1/chat-messages'
DIFY_API_KEY = 'app-0aNJ1b5OVdl8fYZIW7f3K4WZ'  # 用户提供的 Dify API Key
CONTEXT_FILE = 'dify_context.json'

# 读取/保存会话ID，实现多轮对话
def load_context(user_id):
    if os.path.exists(CONTEXT_FILE):
        with open(CONTEXT_FILE, 'r', encoding='utf-8') as f:
            ctx = json.load(f)
        return ctx.get(user_id, {})
    return {}

def save_context(user_id, conversation_id):
    ctx = {}
    if os.path.exists(CONTEXT_FILE):
        with open(CONTEXT_FILE, 'r', encoding='utf-8') as f:
            ctx = json.load(f)
    ctx[user_id] = {'conversation_id': conversation_id}
    with open(CONTEXT_FILE, 'w', encoding='utf-8') as f:
        json.dump(ctx, f, ensure_ascii=False, indent=2)

def ask_dify(question, user_id='testuser'):
    if not user_id:
        user_id = 'testuser'
    context = load_context(user_id)
    conversation_id = context.get('conversation_id', '')
    headers = {
        'Authorization': f'Bearer {DIFY_API_KEY}',
        'Content-Type': 'application/json',
    }
    data = {
        'inputs': {},
        'query': question,
        'user': user_id
    }
    if conversation_id:
        data['conversation_id'] = conversation_id
    try:
        resp = requests.post(DIFY_API_URL, json=data, headers=headers, timeout=60)
        resp.raise_for_status()
        result = resp.json()
        answer = result.get('answer') or result.get('message') or str(result)
        conversation_id = result.get('conversation_id') or conversation_id
        save_context(user_id, conversation_id)
        print(f'AI回复: {answer}')
        with open('../dify_result.txt', 'w', encoding='utf-8') as f:
            f.write(str(answer))
        return answer
    except Exception as e:
        print(f'调用Dify失败: {e}')
        with open('../dify_result.txt', 'w', encoding='utf-8') as f:
            f.write(f'调用Dify失败: {e}')
        return None

if __name__ == '__main__':
    if len(sys.argv) < 2:
        print('用法: python dify_chat.py 你的问题 [user_id]')
    else:
        question = sys.argv[1]
        user_id = sys.argv[2] if len(sys.argv) > 2 else 'testuser'
        ask_dify(question, user_id) 