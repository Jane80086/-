-- 数据迁移脚本
-- 将各子系统的数据迁移到统一的 course_manager 数据库

-- ========================================
-- 1. 迁移课程数据 (从 course_management)
-- ========================================

-- 迁移课程表
INSERT INTO course_manager.courses (
    id, title, description, instructor_id, price, duration, 
    course_level, category, status, cover_image, video_url, 
    created_time, updated_time, like_count, favorite_count
)
SELECT 
    id, title, description, instructor_id, price, duration, 
    course_level, category, status, cover_image, video_url, 
    created_time, updated_time, like_count, favorite_count
FROM course_management.courses
ON CONFLICT (id) DO NOTHING;

-- 迁移课程审核历史
INSERT INTO course_manager.course_review_history (
    id, course_id, admin_id, action, reason, review_time
)
SELECT 
    id, course_id, admin_id, action, reason, review_time
FROM course_management.course_review_history
ON CONFLICT (id) DO NOTHING;

-- 迁移评论表
INSERT INTO course_manager.comments (
    id, course_id, user_id, content, created_at, like_count, status
)
SELECT 
    id, course_id, user_id, content, created_at, like_count, status
FROM course_management.comments
ON CONFLICT (id) DO NOTHING;

-- 迁移课程历史表
INSERT INTO course_manager.course_history (
    course_history_id, course_id, user_id, viewed_at, deleted
)
SELECT 
    course_history_id, course_id, user_id, viewed_at, deleted
FROM course_management.course_history
ON CONFLICT (course_history_id) DO NOTHING;

-- 迁移课程审核表
INSERT INTO course_manager.review (
    review_id, course_id, reviewer_id, status, comment, reviewed_at, deleted
)
SELECT 
    review_id, course_id, reviewer_id, status, comment, reviewed_at, deleted
FROM course_management.review
ON CONFLICT (review_id) DO NOTHING;

-- 迁移课程问答表
INSERT INTO course_manager.questions (
    id, course_id, user_id, question, ai_answer, manual_answer, 
    like_count, accept_answer_type, accept_answer_content, 
    report_count, created_at, deleted
)
SELECT 
    id, course_id, user_id, question, ai_answer, manual_answer, 
    like_count, accept_answer_type, accept_answer_content, 
    report_count, created_at, deleted
FROM course_management.questions
ON CONFLICT (id) DO NOTHING;

-- 迁移精品课程表
INSERT INTO course_manager.featured_course (
    featured_course_id, course_id, promoted_by, promoted_at, priority, deleted
)
SELECT 
    featured_course_id, course_id, promoted_by, promoted_at, priority, deleted
FROM course_management.featured_course
ON CONFLICT (featured_course_id) DO NOTHING;

-- ========================================
-- 2. 迁移会议数据 (从 meeting_management)
-- ========================================

-- 迁移会议信息表
INSERT INTO course_manager.meeting_info (
    id, meeting_name, start_time, end_time, creator, meeting_content,
    status, reviewer, review_time, review_comment, create_time, 
    update_time, image_url, deleted
)
SELECT 
    id, meeting_name, start_time, end_time, creator, meeting_content,
    status, reviewer, review_time, review_comment, create_time, 
    update_time, image_url, deleted
FROM meeting_management.meeting_info
ON CONFLICT (id) DO NOTHING;

-- 迁移会议审核记录表
INSERT INTO course_manager.meeting_review_record (
    id, meeting_id, meeting_name, creator, reviewer, status, 
    review_comment, review_time
)
SELECT 
    id, meeting_id, meeting_name, creator, reviewer, status, 
    review_comment, review_time
FROM meeting_management.meeting_review_record
ON CONFLICT (id) DO NOTHING;

-- ========================================
-- 3. 迁移新闻数据 (从 news_management)
-- ========================================

-- 迁移新闻表
INSERT INTO course_manager.news (
    id, title, image, content, summary, author, user_id, status,
    view_count, is_deleted, audit_comment, audit_user_id, audit_time,
    deleted_time, create_time, update_time
)
SELECT 
    id, title, image, content, summary, author, user_id, status,
    view_count, is_deleted, audit_comment, audit_user_id, audit_time,
    deleted_time, create_time, update_time
FROM news_management.news
ON CONFLICT (id) DO NOTHING;

-- ========================================
-- 4. 迁移系统用户数据 (从 system_management)
-- ========================================

-- 迁移系统用户表
INSERT INTO course_manager.system_user (
    user_id, account, password, role, is_remembered, nickname,
    phone, email, real_name, enterprise_id, department, dynamic_code
)
SELECT 
    user_id, account, password, role, is_remembered, nickname,
    phone, email, real_name, enterprise_id, department, dynamic_code
FROM system_management.system_user
ON CONFLICT (user_id) DO NOTHING;

-- ========================================
-- 5. 迁移用户数据 (从 cemenghui)
-- ========================================

-- 迁移用户信息表
INSERT INTO course_manager.user_info (
    id, username, password, real_name, email, phone, user_type,
    status, create_time, update_time, deleted
)
SELECT 
    id, username, password, real_name, email, phone, user_type,
    status, create_time, update_time, deleted
FROM cemenghui.user_info
ON CONFLICT (id) DO NOTHING;

-- ========================================
-- 6. 重置序列 (如果需要)
-- ========================================

-- 重置课程表序列
SELECT setval('courses_id_seq', (SELECT MAX(id) FROM courses));

-- 重置会议表序列
SELECT setval('meeting_info_id_seq', (SELECT MAX(id) FROM meeting_info));

-- 重置新闻表序列
SELECT setval('news_id_seq', (SELECT MAX(id) FROM news));

-- 重置用户表序列
SELECT setval('user_info_id_seq', (SELECT MAX(id) FROM user_info));

-- ========================================
-- 7. 验证迁移结果
-- ========================================

-- 检查各表数据数量
SELECT 'courses' as table_name, COUNT(*) as count FROM courses
UNION ALL
SELECT 'course_review_history', COUNT(*) FROM course_review_history
UNION ALL
SELECT 'comments', COUNT(*) FROM comments
UNION ALL
SELECT 'course_history', COUNT(*) FROM course_history
UNION ALL
SELECT 'review', COUNT(*) FROM review
UNION ALL
SELECT 'questions', COUNT(*) FROM questions
UNION ALL
SELECT 'featured_course', COUNT(*) FROM featured_course
UNION ALL
SELECT 'meeting_info', COUNT(*) FROM meeting_info
UNION ALL
SELECT 'meeting_review_record', COUNT(*) FROM meeting_review_record
UNION ALL
SELECT 'news', COUNT(*) FROM news
UNION ALL
SELECT 'system_user', COUNT(*) FROM system_user
UNION ALL
SELECT 'user_info', COUNT(*) FROM user_info;

-- 检查数据完整性
SELECT '数据迁移完成！' as status; 