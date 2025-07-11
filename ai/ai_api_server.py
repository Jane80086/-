from fastapi import FastAPI, Request
from dify_client import ask_ai, optimize_title_desc

app = FastAPI()

@app.post("/ai/qa")
async def ai_qa(req: Request):
    data = await req.json()
    question = data.get("question")
    context = data.get("context")
    result = ask_ai(question, context)
    return result

@app.post("/ai/optimize")
async def ai_optimize(req: Request):
    data = await req.json()
    title = data.get("title")
    description = data.get("description")
    result = optimize_title_desc(title, description)
    return result 