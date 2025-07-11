import requests

DIFY_API_KEY = "app-sDs04lJf0Zp0sxQEoemW05M1"
DIFY_BASE_URL = "http://localhost:8088/v1"


def ask_ai(question, context=None):
    headers = {"Authorization": f"Bearer {DIFY_API_KEY}"}
    data = {"question": question}
    if context:
        data["context"] = context
    resp = requests.post(f"{DIFY_BASE_URL}/qa", json=data, headers=headers)
    resp.raise_for_status()
    return resp.json()


def optimize_title_desc(title, description):
    headers = {"Authorization": f"Bearer {DIFY_API_KEY}"}
    data = {"title": title, "description": description}
    resp = requests.post(f"{DIFY_BASE_URL}/course/optimize", json=data, headers=headers)
    resp.raise_for_status()
    return resp.json() 