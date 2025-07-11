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
    
    @Value("${dify.base-url:http://localhost:8088/v1}")
    private String difyBaseUrl;
    @Value("${dify.api-key:app-0aNJ1b5OVdl8fYZIW7f3K4WZ}")
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
     * 调用Dify v1/chat/completions获取AI回答
     */
    private String callDifyWorkflow(String question, Long courseId) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + difyApiKey);
            Map<String, Object> body = new HashMap<>();
            body.put("model", "gpt-3.5-turbo");
            List<Map<String, String>> messages = new java.util.ArrayList<>();
            messages.add(Map.of("role", "user", "content", question));
            body.put("messages", messages);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(
                difyBaseUrl + "/chat/completions",
                entity,
                Map.class
            );
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> result = response.getBody();
                if (result.containsKey("choices")) {
                    Object choicesObj = result.get("choices");
                    if (choicesObj instanceof List && !((List<?>) choicesObj).isEmpty()) {
                        Object first = ((List<?>) choicesObj).get(0);
                        if (first instanceof Map) {
                            Map<?, ?> firstMap = (Map<?, ?>) first;
                            Object messageObj = firstMap.get("message");
                            if (messageObj instanceof Map) {
                                Object content = ((Map<?, ?>) messageObj).get("content");
                                if (content != null) return content.toString();
                            }
                        }
                    }
                }
            }
            return "AI服务暂时不可用，请稍后重试。";
        } catch (Exception e) {
            System.err.println("调用Dify失败: " + e.getMessage());
            return "AI服务暂时不可用，请稍后重试。";
        }
    }
} 