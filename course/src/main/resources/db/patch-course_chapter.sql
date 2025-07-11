CREATE TABLE chapters (
    id BIGSERIAL PRIMARY KEY,
    course_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    duration INTEGER DEFAULT 0,
    description TEXT
);
CREATE INDEX idx_chapters_course_id ON chapters(course_id); 