package com.cemenghui.course.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
    Question askQuestion(Question question);

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

    /**
     * 根据ID获取单个问题详情
     */
    Question getQuestionById(Long id);

    /**
     * 手动回复问题（保存人工回复）
     */
    boolean replyQuestion(Long id, String replyContent, Long replyUserId);

    /**
     * 删除问题（真正删除数据库记录）
     */
    boolean deleteQuestion(Long id);

    /**
     * 分页获取课程问答
     */
    IPage<Question> getQuestionsByCoursePaged(Long courseId, Page<Question> page);

    /**
     * 点赞/取消点赞
     */
    boolean likeQuestion(Long id, Long userId);

    /**
     * 举报问题
     */
    boolean reportQuestion(Long id, Long userId, String reason);

    /**
     * 采纳答案
     */
    boolean acceptAnswer(Long id, String answerType, String answerContent);

    /**
     * 人工回复
     */
    boolean manualReply(Long id, String replyContent, Long replyUserId);
} 