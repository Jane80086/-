package com.cemenghui.course.service.impl;

import com.cemenghui.course.dao.QuestionDao;
import com.cemenghui.course.entity.Question;
import com.cemenghui.course.service.AIService;
import com.cemenghui.course.service.QnAService;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class QnAServiceImpl implements QnAService {
    private QuestionDao questionRepo;
    private AIService aiService;

    /**
     * 提交提问
     * @param courseId 课程ID
     * @param userId 用户ID
     * @param content 问题内容
     * @return 新增问题
     */
    @Override
    public Question askQuestion(Long courseId, Long userId, String content) {
        onQuestionAsked(courseId, userId);
        return null;
    }

    /**
     * 获取问答列表
     * @param courseId 课程ID
     * @return 问答列表
     */
    @Override
    public List<Question> getQuestions(Long courseId) {
        return null;
    }

    /**
     * 使用AI自动生成回复
     * @param question 问题对象
     * @return AI生成的回复
     */
    @Override
    public String autoReply(Question question) {
        return null;
    }

    /**
     * 新增问题事件
     * @param courseId 课程ID
     * @param userId 用户ID
     */
    protected void onQuestionAsked(Long courseId, Long userId) {
        // 触发AI回答
    }
}
