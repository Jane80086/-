package com.cemenghui.course.service;

import com.cemenghui.course.entity.Question;
import java.util.List;

public interface AiQuestionService {
    /**
     * 提交问题并获取AI回答
     */
    Question askQuestion(Long courseId, Long userId, String questionContent);
    
    /**
     * 获取课程的所有问题
     */
    List<Question> getQuestionsByCourseId(Long courseId);
    
    /**
     * 获取用户的所有问题
     */
    List<Question> getQuestionsByUserId(Long userId);
    
    /**
     * 点赞问题
     */
    boolean likeQuestion(Long questionId);
    
    /**
     * 举报问题
     */
    boolean reportQuestion(Long questionId);
    
    /**
     * 接受AI回答
     */
    boolean acceptAiAnswer(Long questionId);
    
    /**
     * 接受人工回答
     */
    boolean acceptManualAnswer(Long questionId, String manualAnswer);
} 