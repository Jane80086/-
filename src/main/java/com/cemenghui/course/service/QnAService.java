package com.cemenghui.course.service;

import com.cemenghui.course.entity.Question;
import java.util.List;

/**
 * 问答服务接口
 */
public interface QnAService {
    /**
     * 提交提问
     * @param courseId 课程ID
     * @param userId 用户ID
     * @param content 问题内容
     * @return 新增问题
     */
    Question askQuestion(Long courseId, Long userId, String content);

    /**
     * 获取问答列表
     * @param courseId 课程ID
     * @return 问答列表
     */
    List<Question> getQuestions(Long courseId);

    /**
     * 使用AI自动生成回复
     * @param question 问题对象
     * @return AI生成的回复
     */
    String autoReply(Question question);
} 