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
            
            // 2. 调用Dify API获取AI回答
            String aiAnswer = callDifyWorkflow(questionContent, courseId);
            question.setAiAnswer(aiAnswer);
            
            // 3. 保存到数据库
            questionDao.insert(question);
            
            return question;
            
        } catch (Exception e) {
            System.err.println("AI问答失败: " + e.getMessage());
            e.printStackTrace();
            // 返回一个默认回答，避免前端超时
            Question question = new Question();
            question.setCourseId(courseId);
            question.setUserId(userId);
            question.setQuestion(questionContent);
            question.setAiAnswer("AI服务暂时不可用，请稍后再试。如果问题紧急，建议您仔细学习相关章节内容或联系课程管理员。");
            question.setCreatedAt(LocalDateTime.now());
            return question;
        }
    }
    
    /**
     * 快速生成AI回答
     */
    private String generateQuickAnswer(String question) {
        String lowerQuestion = question.toLowerCase();
        
        if (lowerQuestion.contains("什么是") || lowerQuestion.contains("如何") || lowerQuestion.contains("怎么")) {
            return "这是一个很好的问题！根据课程内容，" + question + " 的答案是：这是一个重要的概念，建议您仔细学习相关章节，并在实践中加深理解。";
        } else if (lowerQuestion.contains("为什么") || lowerQuestion.contains("原因")) {
            return "关于" + question + "，主要原因是：这涉及到课程的核心知识点，理解这个原理对于掌握整个课程内容非常重要。";
        } else if (lowerQuestion.contains("区别") || lowerQuestion.contains("不同")) {
            return "关于" + question + "的区别：它们各有特点，适用于不同的场景。建议您结合具体案例来理解它们的差异。";
        } else if (lowerQuestion.contains("优点") || lowerQuestion.contains("优势")) {
            return question + "的主要优点是：具有很好的性能和易用性，在实际项目中应用广泛。";
        } else if (lowerQuestion.contains("缺点") || lowerQuestion.contains("问题")) {
            return "关于" + question + "的缺点：确实存在一些局限性，但通过合理的设计和优化可以很好地解决这些问题。";
        } else {
            return "感谢您的提问！关于" + question + "，这是一个很有价值的问题。建议您：\n1. 仔细观看相关视频内容\n2. 阅读课程文档\n3. 动手实践练习\n4. 如有疑问可以继续提问";
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
            System.out.println("开始调用Dify API，问题: " + question);
            System.out.println("Dify配置 - URL: " + difyBaseUrl + ", API Key: " + difyApiKey.substring(0, 10) + "...");
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + difyApiKey);
            
            Map<String, Object> body = new HashMap<>();
            body.put("model", "gpt-3.5-turbo");
            body.put("temperature", 0.7);
            body.put("max_tokens", 1000);
            
            List<Map<String, String>> messages = new java.util.ArrayList<>();
            // 添加系统提示，让AI更好地理解这是课程问答场景
            messages.add(Map.of("role", "system", "content", 
                "你是一个专业的课程助教，请针对用户关于课程内容的问题提供准确、详细、易懂的回答。回答要简洁明了，重点突出，并鼓励学生深入学习。"));
            messages.add(Map.of("role", "user", "content", question));
            body.put("messages", messages);
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            
            System.out.println("发送请求到: " + difyBaseUrl + "/chat/completions");
            ResponseEntity<Map> response = restTemplate.postForEntity(
                difyBaseUrl + "/chat/completions",
                entity,
                Map.class
            );
            
            System.out.println("Dify API响应状态: " + response.getStatusCode());
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> result = response.getBody();
                System.out.println("Dify API响应内容: " + result);
                
                if (result.containsKey("choices")) {
                    Object choicesObj = result.get("choices");
                    if (choicesObj instanceof List && !((List<?>) choicesObj).isEmpty()) {
                        Object first = ((List<?>) choicesObj).get(0);
                        if (first instanceof Map) {
                            Map<?, ?> firstMap = (Map<?, ?>) first;
                            Object messageObj = firstMap.get("message");
                            if (messageObj instanceof Map) {
                                Object content = ((Map<?, ?>) messageObj).get("content");
                                if (content != null) {
                                    String answer = content.toString();
                                    System.out.println("AI回答成功: " + answer.substring(0, Math.min(100, answer.length())) + "...");
                                    return answer;
                                }
                            }
                        }
                    }
                }
                
                // 如果响应格式不符合预期，返回原始响应用于调试
                System.out.println("Dify API响应格式不符合预期，返回原始响应");
                return "AI服务响应格式异常，请稍后重试。";
            } else {
                System.out.println("Dify API请求失败，状态码: " + response.getStatusCode());
                return "AI服务请求失败，请稍后重试。";
            }
        } catch (Exception e) {
            System.err.println("调用Dify失败: " + e.getMessage());
            e.printStackTrace();
            return "AI服务暂时不可用，请稍后重试。";
        }
    }
} 