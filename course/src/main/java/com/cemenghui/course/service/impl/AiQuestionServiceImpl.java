package com.cemenghui.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.cemenghui.course.dao.QuestionDao;
import com.cemenghui.course.entity.Question;
import com.cemenghui.course.service.AiQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AiQuestionServiceImpl implements AiQuestionService {
    
    @Autowired
    private QuestionDao questionDao;
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Value("${dify.workflow.url:http://localhost:8088/workflow/U7GxMeHuVNJC1Kd8}")
    private String difyWorkflowUrl;
    
    @Value("${dify.api.key:app-Z4eFAVjcGNqXSDsJyP8ZA4bW}")
    private String difyApiKey;

    @Override
    public Question askQuestion(Long courseId, Long userId, String questionContent) {
        try {
            // 1. 创建问题记录
            Question question = new Question();
            question.setCourseId(courseId);
            question.setUserId(userId);
            question.setQuestion(questionContent);
            question.setCreatedAt(LocalDateTime.now());
            
            // 2. 调用Dify工作流获取AI回答
            String aiAnswer = callDifyWorkflow(questionContent, courseId);
            question.setAiAnswer(aiAnswer);
            
            // 3. 保存到数据库
            questionDao.insert(question);
            
            return question;
            
        } catch (Exception e) {
            System.err.println("AI问答失败: " + e.getMessage());
            throw new RuntimeException("AI问答服务暂时不可用，请稍后重试");
        }
    }

    @Override
    public List<Question> getQuestionsByCourseId(Long courseId) {
        QueryWrapper<Question> wrapper = new QueryWrapper<>();
        wrapper.eq("course_id", courseId)
               .orderByDesc("created_at");
        return questionDao.selectList(wrapper);
    }

    @Override
    public List<Question> getQuestionsByUserId(Long userId) {
        QueryWrapper<Question> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId)
               .orderByDesc("created_at");
        return questionDao.selectList(wrapper);
    }

    @Override
    public boolean likeQuestion(Long questionId) {
        UpdateWrapper<Question> wrapper = new UpdateWrapper<>();
        wrapper.eq("id", questionId)
               .setSql("like_count = like_count + 1");
        return questionDao.update(null, wrapper) > 0;
    }

    @Override
    public boolean reportQuestion(Long questionId) {
        UpdateWrapper<Question> wrapper = new UpdateWrapper<>();
        wrapper.eq("id", questionId)
               .setSql("report_count = report_count + 1");
        return questionDao.update(null, wrapper) > 0;
    }

    @Override
    public boolean acceptAiAnswer(Long questionId) {
        Question question = questionDao.selectById(questionId);
        if (question != null && question.getAiAnswer() != null) {
            UpdateWrapper<Question> wrapper = new UpdateWrapper<>();
            wrapper.eq("id", questionId)
                   .set("accept_answer_type", "AI")
                   .set("accept_answer_content", question.getAiAnswer());
            return questionDao.update(null, wrapper) > 0;
        }
        return false;
    }

    @Override
    public boolean acceptManualAnswer(Long questionId, String manualAnswer) {
        UpdateWrapper<Question> wrapper = new UpdateWrapper<>();
        wrapper.eq("id", questionId)
               .set("manual_answer", manualAnswer)
               .set("accept_answer_type", "MANUAL")
               .set("accept_answer_content", manualAnswer);
        return questionDao.update(null, wrapper) > 0;
    }

    /**
     * 调用Dify工作流获取AI回答
     */
    private String callDifyWorkflow(String question, Long courseId) {
        try {
            // 构建请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + difyApiKey);
            
            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("inputs", Map.of(
                "question", question,
                "course_id", courseId.toString()
            ));
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            
            // 发送请求到Dify工作流
            ResponseEntity<Map> response = restTemplate.postForEntity(
                difyWorkflowUrl, 
                entity, 
                Map.class
            );
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> result = response.getBody();
                // 根据Dify返回格式解析答案
                if (result.containsKey("answer")) {
                    return (String) result.get("answer");
                } else if (result.containsKey("output")) {
                    return (String) result.get("output");
                } else {
                    return "抱歉，AI暂时无法回答这个问题，请稍后重试。";
                }
            } else {
                return "AI服务暂时不可用，请稍后重试。";
            }
            
        } catch (Exception e) {
            System.err.println("调用Dify工作流失败: " + e.getMessage());
            return "AI服务暂时不可用，请稍后重试。";
        }
    }
} 