import sys
import io
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
from ai.backend_dify import ask_dify
from ai.backend_mcp import ask_mcp
from datetime import datetime
from typing import Optional

# 强制标准输出和标准错误为 utf-8
sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')
sys.stderr = io.TextIOWrapper(sys.stderr.buffer, encoding='utf-8')

app = FastAPI(title="AI QA Server", version="1.0.0")

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

class AIQuestionRequest(BaseModel):
    question: str
    ai_backend: str = "dify"
    courseId: Optional[int] = None
    userId: Optional[int] = None

# 内存存储 AI 问答历史
ai_questions_db = []
question_id_counter = 1

@app.post("/api/ai-questions/ask")
async def ask_ai_question(req: AIQuestionRequest):
    global question_id_counter
    
    if not req.question.strip():
        return {"code": 400, "success": False, "error": "问题不能为空"}
    
    if req.ai_backend == "dify":
        answer = ask_dify(req.question)
    elif req.ai_backend == "mcp":
        answer = ask_mcp(req.question)
    else:
        answer = f"未知AI后端: {req.ai_backend}"
    
    # 保存问答记录
    question_record = {
        "id": question_id_counter,
        "courseId": req.courseId or 1,
        "userId": req.userId or 1,
        "question": req.question,
        "aiAnswer": answer,
        "userName": "用户",
        "createTime": datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    }
    
    ai_questions_db.append(question_record)
    question_id_counter += 1
    
    return {
        "code": 200,
        "success": True,
        "data": {
            "id": question_record["id"],
            "aiAnswer": answer,
            "answer": answer
        }
    }

@app.get("/api/ai-questions/course/{course_id}")
async def get_ai_questions_by_course(course_id: int):
    try:
        course_questions = [q for q in ai_questions_db if q.get("courseId") == course_id]
        formatted = [{
            "id": q["id"],
            "question": q["question"],
            "answer": q["aiAnswer"],
            "userName": q["userName"],
            "createTime": q["createTime"]
        } for q in course_questions]
        
        return {
            "code": 200,
            "success": True,
            "data": formatted
        }
    except Exception as e:
        return {
            "code": 500,
            "success": False,
            "error": f"获取问答记录异常: {str(e)}"
        } 