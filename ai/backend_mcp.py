import requests
from config import MCP_API_URL

def ask_mcp(question):
    try:
        resp = requests.post(MCP_API_URL, json={"question": question}, timeout=120)
        resp.encoding = 'utf-8'
        if resp.status_code == 200:
            result = resp.json()
            answer = result.get("answer", "MCP无回复")
            return answer
        else:
            return f"MCP服务异常: HTTP {resp.status_code} - {resp.text}"
    except Exception as e:
        return f"MCP服务异常: {str(e)}" 