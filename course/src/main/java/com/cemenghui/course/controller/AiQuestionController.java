package com.cemenghui.course.controller;

import com.cemenghui.course.entity.Question;
import com.cemenghui.course.service.AiQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/ai-questions")
public class AiQuestionController {

    @Autowired
    private AiQuestionService aiQuestionService;

    /**
     * 提交问题并获取AI回答
     */
    @PostMapping("/ask")
    public ResponseEntity<Question> askQuestion(
            @RequestParam Long courseId,
            @RequestParam Long userId,
            @RequestParam String question) {
        Question result = aiQuestionService.askQuestion(courseId, userId, question);
        return ResponseEntity.ok(result);
    }

    /**
     * 获取课程的所有问题
     */
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<Question>> getQuestionsByCourseId(@PathVariable Long courseId) {
        List<Question> questions = aiQuestionService.getQuestionsByCourseId(courseId);
        return ResponseEntity.ok(questions);
    }

    /**
     * 获取用户的所有问题
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Question>> getQuestionsByUserId(@PathVariable Long userId) {
        List<Question> questions = aiQuestionService.getQuestionsByUserId(userId);
        return ResponseEntity.ok(questions);
    }

    /**
     * 点赞问题
     */
    @PostMapping("/{questionId}/like")
    public ResponseEntity<String> likeQuestion(@PathVariable Long questionId) {
        boolean success = aiQuestionService.likeQuestion(questionId);
        return ResponseEntity.ok(success ? "点赞成功" : "点赞失败");
    }

    /**
     * 举报问题
     */
    @PostMapping("/{questionId}/report")
    public ResponseEntity<String> reportQuestion(@PathVariable Long questionId) {
        boolean success = aiQuestionService.reportQuestion(questionId);
        return ResponseEntity.ok(success ? "举报成功" : "举报失败");
    }

    /**
     * 接受AI回答
     */
    @PostMapping("/{questionId}/accept-ai")
    public ResponseEntity<String> acceptAiAnswer(@PathVariable Long questionId) {
        boolean success = aiQuestionService.acceptAiAnswer(questionId);
        return ResponseEntity.ok(success ? "已接受AI回答" : "操作失败");
    }

    /**
     * 接受人工回答
     */
    @PostMapping("/{questionId}/accept-manual")
    public ResponseEntity<String> acceptManualAnswer(
            @PathVariable Long questionId,
            @RequestParam String manualAnswer) {
        boolean success = aiQuestionService.acceptManualAnswer(questionId, manualAnswer);
        return ResponseEntity.ok(success ? "已接受人工回答" : "操作失败");
    }
} 