package com.cemenghui.course.controller;

import com.cemenghui.course.common.Result;
import com.cemenghui.course.entity.Question;
import com.cemenghui.course.service.QnAService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
     * 获取单个问题详情
     */
    @GetMapping("/{id}")
    public Result getQuestionDetail(@PathVariable Long id) {
        try {
            Question question = qnaService.getQuestionById(id);
            if (question == null) {
                return Result.fail("问题不存在");
            }
            return Result.success(question);
        } catch (Exception e) {
            return Result.fail("获取问题详情失败: " + e.getMessage());
        }
    }

    /**
     * 手动回复问题（保存人工回复）
     */
    @PostMapping("/{id}/reply")
    public Result replyQuestion(
            @PathVariable Long id,
            @RequestParam String replyContent,
            @RequestParam(defaultValue = "1") Long replyUserId) {
        try {
            boolean success = qnaService.replyQuestion(id, replyContent, replyUserId);
            if (success) {
                return Result.success("回复提交成功", null);
            } else {
                return Result.fail("问题不存在或回复失败");
            }
        } catch (Exception e) {
            return Result.fail("回复提交失败: " + e.getMessage());
        }
    }

    /**
     * 删除问题（真正删除数据库记录）
     */
    @DeleteMapping("/{id}")
    public Result deleteQuestion(@PathVariable Long id) {
        try {
            boolean success = qnaService.deleteQuestion(id);
            if (success) {
                return Result.success("问题删除成功", null);
            } else {
                return Result.fail("问题不存在或删除失败");
            }
        } catch (Exception e) {
            return Result.fail("问题删除失败: " + e.getMessage());
        }
    }

    /**
     * 分页获取课程问答
     */
    @GetMapping("/course/{courseId}/page")
    public Result getQuestionsByCoursePaged(@PathVariable Long courseId,
                                            @RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<Question> questions = qnaService.getQuestionsByCoursePaged(courseId, PageRequest.of(page, size));
            return Result.success(questions);
        } catch (Exception e) {
            return Result.fail("分页获取问答失败: " + e.getMessage());
        }
    }

    /**
     * 点赞问题
     */
    @PostMapping("/{id}/like")
    public Result likeQuestion(@PathVariable Long id, @RequestParam Long userId) {
        try {
            boolean success = qnaService.likeQuestion(id, userId);
            if (success) {
                return Result.success("点赞成功", null);
            } else {
                return Result.fail("问题不存在或点赞失败");
            }
        } catch (Exception e) {
            return Result.fail("点赞失败: " + e.getMessage());
        }
    }

    /**
     * 举报问题
     */
    @PostMapping("/{id}/report")
    public Result reportQuestion(@PathVariable Long id, @RequestParam Long userId, @RequestParam String reason) {
        try {
            boolean success = qnaService.reportQuestion(id, userId, reason);
            if (success) {
                return Result.success("举报成功", null);
            } else {
                return Result.fail("问题不存在或举报失败");
            }
        } catch (Exception e) {
            return Result.fail("举报失败: " + e.getMessage());
        }
    }

    /**
     * 采纳答案
     */
    @PostMapping("/{id}/accept")
    public Result acceptAnswer(@PathVariable Long id, @RequestParam String answerType, @RequestParam String answerContent) {
        try {
            boolean success = qnaService.acceptAnswer(id, answerType, answerContent);
            if (success) {
                return Result.success("采纳成功", null);
            } else {
                return Result.fail("问题不存在或采纳失败");
            }
        } catch (Exception e) {
            return Result.fail("采纳失败: " + e.getMessage());
        }
    }

    /**
     * 人工回复（分离）
     */
    @PostMapping("/{id}/manual-reply")
    public Result manualReply(@PathVariable Long id, @RequestParam String replyContent, @RequestParam(defaultValue = "1") Long replyUserId) {
        try {
            boolean success = qnaService.manualReply(id, replyContent, replyUserId);
            if (success) {
                return Result.success("人工回复提交成功", null);
            } else {
                return Result.fail("问题不存在或回复失败");
            }
        } catch (Exception e) {
            return Result.fail("人工回复失败: " + e.getMessage());
        }
    }
} 