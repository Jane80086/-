from flask import Flask, request, jsonify
import requests

app = Flask(__name__)

# Dify API配置
DIFY_API_URL = "http://localhost:8088/v1/chat/completions"
DIFY_API_KEY = "app-0aNJ1b5OVdl8fYZIW7f3K4WZ"

def call_dify(prompt):
    headers = {
        "Authorization": f"Bearer {DIFY_API_KEY}",
        "Content-Type": "application/json"
    }
    data = {
        "inputs": {"question": prompt},
        "response_mode": "blocking"
    }
    resp = requests.post(DIFY_API_URL, json=data, headers=headers, timeout=30)
    resp.raise_for_status()
    result = resp.json()
    # 兼容Dify返回格式
    if "answer" in result:
        return result["answer"]
    elif "output" in result:
        return result["output"]
    elif "choices" in result and result["choices"]:
        return result["choices"][0].get("message", {}).get("content", "")
    else:
        return str(result)

@app.route('/ai/optimize', methods=['POST'])
def optimize():
    data = request.get_json()
    title = data.get('title', '')
    description = data.get('description', '')
    prompt = f"请帮我优化课程标题和简介。\n原始标题：{title}\n原始简介：{description}\n请分别输出优化后的标题和简介。"
    answer = call_dify(prompt)
    # 简单分割，实际可用正则或更复杂的解析
    lines = answer.split('\n')
    optimized_title = title
    optimized_description = description
    for line in lines:
        if '标题' in line:
            optimized_title = line.split('：', 1)[-1].strip()
        if '简介' in line:
            optimized_description = line.split('：', 1)[-1].strip()
    return jsonify({
        "optimized_title": optimized_title,
        "optimized_description": optimized_description
    })

@app.route('/ai/qna', methods=['POST'])
def qna():
    data = request.get_json()
    question = data.get('question', '')
    prompt = f"请作为课程AI助手，回答以下问题：{question}"
    answer = call_dify(prompt)
    return jsonify({
        "answer": answer
    })

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5001) 