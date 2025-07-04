package com.cemenghui.course.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 课程问答实体
 */
@TableName("questions")
@Data
public class Question implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("course_id")
    private Long courseId;

    @TableField("user_id")
    private Long userId;

    @TableField("question")
    private String question;

    @TableField("ai_answer")
    private String aiAnswer;

    @TableField("manual_answer")
    private String manualAnswer;

    @TableField("like_count")
    private Integer likeCount = 0;

    @TableField("accept_answer_type")
    private String acceptAnswerType; // AI/MANUAL

    @TableField("accept_answer_content")
    private String acceptAnswerContent;

    @TableField("report_count")
    private Integer reportCount = 0;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableLogic
    @TableField("deleted")
    private Integer deleted = 0;

    /**
     * 提交一个问题
     * @param questionContent 问题内容
     * @throws Exception 可能抛出的异常
     */
    public void ask(String questionContent) throws Exception {
        this.question = questionContent;
        this.createdAt = LocalDateTime.now();
        onQuestionAsked(this.courseId, this);
    }

    /**
     * 调用AI自动生成回复
     * @return AI生成的回复
     * @throws Exception 可能抛出的异常
     */
    public String autoReply() throws Exception {
        // 这里应调用AI服务生成回复
        this.aiAnswer = "AI自动生成的回复";
        onAutoReplied(this.id);
        return this.aiAnswer;
    }

    /**
     * 问题提交事件
     * @param courseId 课程ID
     * @param question 问题对象
     */
    protected void onQuestionAsked(Long courseId, Question question) {
        // 记录问题，用于统计
    }

    /**
     * AI自动回复事件
     * @param questionId 问题ID
     */
    protected void onAutoReplied(Long questionId) {
        // 由AI生成答案并返回用户
    }
} 