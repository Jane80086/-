package com.cemenghui.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
    private QuestionDao questionDao;
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
    public Question askQuestion(Question question) {
        if (question.getCreatedAt() == null) {
            question.setCreatedAt(java.time.LocalDateTime.now());
        }
        questionDao.insert(question);
        return question;
    }

    /**
     * 获取问答列表
     * @param courseId 课程ID
     * @return 问答列表
     */
    @Override
    public List<Question> getQuestions(Long courseId) {
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Question::getCourseId, courseId);
        return questionDao.selectList(wrapper);
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
                question = questionDao.selectById(question.getId());
                if (question == null) return "问题不存在";
            }
            String aiAnswer = aiService.autoReply(question);
            question.setAiAnswer(aiAnswer);
            questionDao.updateById(question);
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
        return questionDao.selectById(id);
    }

    @Override
    @Transactional
    public boolean replyQuestion(Long id, String replyContent, Long replyUserId) {
        Question question = questionDao.selectById(id);
        if (question == null) return false;
        // 这里简单保存人工回复到aiAnswer字段（如需区分可扩展字段）
        question.setAiAnswer(replyContent);
        questionDao.updateById(question);
        return true;
    }

    @Override
    @Transactional
    public boolean deleteQuestion(Long id) {
        if (questionDao.selectById(id) == null) return false;
        questionDao.deleteById(id);
        return true;
    }

    @Override
    public IPage<Question> getQuestionsByCoursePaged(Long courseId, Page<Question> page) {
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Question::getCourseId, courseId)
               .orderByDesc(Question::getCreatedAt);
        return questionDao.selectPage(page, wrapper);
    }

    @Override
    @Transactional
    public boolean likeQuestion(Long id, Long userId) {
        Question question = questionDao.selectById(id);
        if (question == null) return false;
        if (question.getLikeCount() == null) question.setLikeCount(0);
        question.setLikeCount(question.getLikeCount() + 1); // 简化：不做用户去重
        questionDao.updateById(question);
        return true;
    }

    @Override
    @Transactional
    public boolean reportQuestion(Long id, Long userId, String reason) {
        Question question = questionDao.selectById(id);
        if (question == null) return false;
        if (question.getReportCount() == null) question.setReportCount(0);
        question.setReportCount(question.getReportCount() + 1);
        questionDao.updateById(question);
        return true;
    }

    @Override
    @Transactional
    public boolean acceptAnswer(Long id, String answerType, String answerContent) {
        Question question = questionDao.selectById(id);
        if (question == null) return false;
        question.setAcceptAnswerType(answerType);
        question.setAcceptAnswerContent(answerContent);
        questionDao.updateById(question);
        return true;
    }

    @Override
    @Transactional
    public boolean manualReply(Long id, String replyContent, Long replyUserId) {
        Question question = questionDao.selectById(id);
        if (question == null) return false;
        question.setManualAnswer(replyContent);
        questionDao.updateById(question);
        return true;
    }
}
