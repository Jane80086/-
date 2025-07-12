from fastapi import FastAPI, Request, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
from typing import Optional
import uvicorn
from datetime import datetime
from dify_client import ask_ai, optimize_title_desc

app = FastAPI(title="Course AI API Server", version="1.0.0")

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

class QuestionRequest(BaseModel):
    courseId: Optional[int] = None
    userId: Optional[int] = None
    question: str

ai_questions_db = []
question_id_counter = 1

@app.get("/")
async def root():
    return {"message": "Course AI API Server is running", "version": "1.0.0"}

@app.get("/health")
async def health():
    return {"status": "healthy", "service": "course-ai-api"}

@app.get("/api/ai-questions/course/{course_id}")
async def get_ai_questions_by_course(course_id: int):
    course_questions = [q for q in ai_questions_db if q.get("courseId") == course_id]
    formatted = [{
        "id": q["id"],
        "question": q["question"],
        "answer": q["aiAnswer"],
        "userName": q["userName"],
        "createTime": q["createTime"]
    } for q in course_questions]
    return {"code": 200, "success": True, "data": formatted}

@app.post("/api/ai-questions/ask")
async def ask_ai_question(request: QuestionRequest):
    global question_id_counter
    if not request.question.strip():
        raise HTTPException(status_code=400, detail="问题不能为空")
    
    # 添加异常处理，避免Dify超时导致500错误
    try:
        ai_response = ask_ai(request.question)
        ai_answer = ai_response.get("answer", "AI暂时无法回答，请稍后再试。") if isinstance(ai_response, dict) else str(ai_response)
    except Exception as e:
        print(f"AI服务异常: {e}")
        ai_answer = "AI服务暂时不可用，请稍后再试。"
    
    question_record = {
        "id": question_id_counter,
        "courseId": request.courseId or 1,
        "userId": request.userId or 1,
        "question": request.question,
        "aiAnswer": ai_answer,
        "userName": "用户",
        "createTime": datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    }
    ai_questions_db.append(question_record)
    question_id_counter += 1
    return {"code": 200, "success": True, "data": {"id": question_record["id"], "aiAnswer": ai_answer, "answer": ai_answer}}

if __name__ == "__main__":
    print("启动Course AI API Server...")
    print("服务地址: http://localhost:8090")
    print("API文档: http://localhost:8090/docs")
    uvicorn.run("ai_api_server:app", host="0.0.0.0", port=8090, reload=True)