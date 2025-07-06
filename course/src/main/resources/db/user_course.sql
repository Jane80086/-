CREATE TABLE user_course (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    purchase_time TIMESTAMP NOT NULL
); 