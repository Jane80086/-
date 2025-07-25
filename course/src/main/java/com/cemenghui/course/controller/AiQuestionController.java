package com.cemenghui.course.controller;

import com.cemenghui.course.common.Result;
import com.cemenghui.course.entity.Question;
import com.cemenghui.course.service.AiQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/ai-questions")
public class AiQuestionController {

    @Autowired
    private AiQuestionService aiQuestionService;

    public static class QuestionRequest {
        public Long courseId;
        public Long userId;
        public String question;
    }

    /**
     * 提交问题并获取AI回答
     */
    @PostMapping("/ask")
    public ResponseEntity<Result> askQuestion(@RequestBody QuestionRequest req) {
        try {
            Question result = aiQuestionService.askQuestion(req.courseId, req.userId, req.question);
            return ResponseEntity.ok(Result.success("AI问答成功", result));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Result.fail("AI问答失败: " + e.getMessage()));
        }
    }

    /**
     * 获取课程的所有问题
     */
    @GetMapping("/course/{courseId}")
    public ResponseEntity<Result> getQuestionsByCourseId(@PathVariable Long courseId) {
        try {
            List<Question> questions = aiQuestionService.getQuestionsByCourseId(courseId);
            return ResponseEntity.ok(Result.success("获取成功", questions));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Result.fail("获取失败: " + e.getMessage()));
        }
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

    /**
     * 测试AI问答功能
     */
    @PostMapping("/test")
    public ResponseEntity<Result> testAiQuestion(@RequestBody QuestionRequest req) {
        try {
            System.out.println("测试AI问答功能 - 问题: " + req.question);
            
            // 直接调用AI服务测试
            String testAnswer = aiQuestionService.askQuestion(req.courseId, req.userId, req.question).getAiAnswer();
            
            Map<String, Object> result = new HashMap<>();
            result.put("question", req.question);
            result.put("answer", testAnswer);
            result.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(Result.success("AI问答测试成功", result));
        } catch (Exception e) {
            System.err.println("AI问答测试失败: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Result.fail("AI问答测试失败: " + e.getMessage()));
        }
    }

    /**
     * 检查AI服务状态
     */
    @GetMapping("/status")
    public ResponseEntity<Result> checkAiStatus() {
        try {
            Map<String, Object> status = new HashMap<>();
            status.put("service", "AI问答服务");
            status.put("status", "运行中");
            status.put("timestamp", System.currentTimeMillis());
            
            // 可以在这里添加更多状态检查逻辑
            // 比如检查Dify API连接等
            
            return ResponseEntity.ok(Result.success("AI服务状态检查", status));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Result.fail("AI服务状态检查失败: " + e.getMessage()));
        }
    }
} 