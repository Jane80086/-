import requests

url = "http://localhost:8090/ai/qa"  # 修正为实际服务端口8090

def ask_ai(question, context=None):
    data = {
        "question": question,
        "context": context or ""
    }
    resp = requests.post(url, json=data)
    print(f"Status: {resp.status_code}")
    print("Response:", resp.text)

if __name__ == "__main__":
    q = input("请输入你的问题：")
    ask_ai(q) 