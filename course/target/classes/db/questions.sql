CREATE TABLE questions (
    id BIGSERIAL PRIMARY KEY,
    course_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    question TEXT NOT NULL,
    ai_answer TEXT,
    manual_answer TEXT,
    like_count INTEGER DEFAULT 0,
    accept_answer_type VARCHAR(10), -- AI/MANUAL
    accept_answer_content TEXT,
    report_count INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0
);

-- 创建索引
CREATE INDEX idx_questions_course_id ON questions(course_id);
CREATE INDEX idx_questions_user_id ON questions(user_id);
CREATE INDEX idx_questions_created_at ON questions(created_at); 