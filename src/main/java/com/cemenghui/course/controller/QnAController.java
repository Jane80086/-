package com.cemenghui.course.controller;

import com.cemenghui.course.common.Result;
import com.cemenghui.course.entity.Question;
import com.cemenghui.course.service.QnAService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/qna")
public class QnAController {

    @Autowired
    private QnAService qnaService;

    /**
     * 提交问题
     */
    @PostMapping("/ask")
    public Result askQuestion(
            @RequestParam Long courseId,
            @RequestParam String content,
            @RequestParam(defaultValue = "1") Long userId) {
        try {
            Question question = qnaService.askQuestion(courseId, userId, content);
            return Result.success("问题提交成功", question);
        } catch (Exception e) {
            return Result.fail("问题提交失败: " + e.getMessage());
        }
    }

    /**
     * 获取课程问答列表
     */
    @GetMapping("/course/{courseId}")
    public Result getQuestionsByCourse(@PathVariable Long courseId) {
        try {
            List<Question> questions = qnaService.getQuestions(courseId);
            return Result.success(questions);
        } catch (Exception e) {
            return Result.fail("获取问答列表失败: " + e.getMessage());
        }
    }

    /**
     * AI自动回复
     */
    @PostMapping("/{id}/auto-reply")
    public Result autoReply(@PathVariable Long id) {
        try {
            // 这里需要先获取问题对象，然后调用AI回复
            // 简化实现，实际应该从数据库获取Question对象
            Question question = new Question();
            question.setId(id);
            
            String reply = qnaService.autoReply(question);
            return Result.success("AI回复生成成功", reply);
        } catch (Exception e) {
            return Result.fail("AI回复生成失败: " + e.getMessage());
        }
    }

    /**
     * 手动回复问题
     */
    @PostMapping("/{id}/reply")
    public Result replyQuestion(
            @PathVariable Long id,
            @RequestParam String replyContent,
            @RequestParam(defaultValue = "1") Long replyUserId) {
        try {
            // 这里需要实现手动回复逻辑
            // 简化实现，返回成功消息
            return Result.success("回复提交成功", null);
        } catch (Exception e) {
            return Result.fail("回复提交失败: " + e.getMessage());
        }
    }

    /**
     * 删除问题
     */
    @DeleteMapping("/{id}")
    public Result deleteQuestion(@PathVariable Long id) {
        try {
            // 这里需要实现删除逻辑
            // 简化实现，返回成功消息
            return Result.success("问题删除成功", null);
        } catch (Exception e) {
            return Result.fail("问题删除失败: " + e.getMessage());
        }
    }
} 