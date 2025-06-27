package com.cemenghui.course.service.impl;

import com.cemenghui.course.dao.QuestionDao;
import com.cemenghui.course.entity.Question;
import com.cemenghui.course.service.AIService;
import com.cemenghui.course.service.QnAService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
public class QnAServiceImpl implements QnAService {
    @Autowired
    private QuestionDao questionRepo;
    @Autowired
    private AIService aiService;

    /**
     * 提交提问
     * @param courseId 课程ID
     * @param userId 用户ID
     * @param content 问题内容
     * @return 新增问题
     */
    @Override
    @Transactional
    public Question askQuestion(Long courseId, Long userId, String content) {
        Question question = new Question();
        question.setCourseId(courseId);
        question.setUserId(userId);
        question.setQuestion(content);
        question.setCreatedAt(java.time.LocalDateTime.now());
        // 保存到数据库
        question = questionRepo.save(question);
        return question;
    }

    /**
     * 获取问答列表
     * @param courseId 课程ID
     * @return 问答列表
     */
    @Override
    public List<Question> getQuestions(Long courseId) {
        return questionRepo.findByCourseId(courseId);
    }

    /**
     * 使用AI自动生成回复
     * @param question 问题对象
     * @return AI生成的回复
     */
    @Override
    @Transactional
    public String autoReply(Question question) {
        try {
            // 若只传id，需查库
            if (question.getId() != null && (question.getQuestion() == null || question.getQuestion().isEmpty())) {
                question = questionRepo.findById(question.getId()).orElse(null);
                if (question == null) return "问题不存在";
            }
            String aiAnswer = aiService.autoReply(question);
            question.setAiAnswer(aiAnswer);
            questionRepo.save(question);
            return aiAnswer;
        } catch (Exception e) {
            return "AI答复失败: " + e.getMessage();
        }
    }

    /**
     * 新增问题事件
     * @param courseId 课程ID
     * @param userId 用户ID
     */
    protected void onQuestionAsked(Long courseId, Long userId) {
        // 触发AI回答
    }

    @Override
    public Question getQuestionById(Long id) {
        return questionRepo.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public boolean replyQuestion(Long id, String replyContent, Long replyUserId) {
        Question question = questionRepo.findById(id).orElse(null);
        if (question == null) return false;
        // 这里简单保存人工回复到aiAnswer字段（如需区分可扩展字段）
        question.setAiAnswer(replyContent);
        questionRepo.save(question);
        return true;
    }

    @Override
    @Transactional
    public boolean deleteQuestion(Long id) {
        if (!questionRepo.existsById(id)) return false;
        questionRepo.deleteById(id);
        return true;
    }
}
