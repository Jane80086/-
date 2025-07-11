package com.cemenghui.course.service.impl;

import com.cemenghui.course.entity.Course;
import com.cemenghui.course.entity.Question;
import com.cemenghui.course.service.AIException;
import com.cemenghui.course.service.AIService;
import com.cemenghui.course.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.context.annotation.Lazy;

/**
 * AI服务实现
 */
@Service
public class AIServiceImpl implements AIService {
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    @Lazy
    private CourseService courseService;
    
    @Value("${dify.api.key}")
    private String difyApiKey;
    
    @Value("${dify.base-url}")
    private String difyBaseUrl;
    
    @Value("${dify.workflow.url}")
    private String difyWorkflowUrl;
    
    private List<String> trendKeyWords = new ArrayList<>();

    /**
     * 返回优化后的课程（标题、简介等）
     * @param course 课程对象
     * @return 优化后的课程
     * @throws AIException AI相关异常
     */
    @Override
    public Course optimizeCourseInfo(Course course) throws AIException {
        try {
            // 构建优化请求
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("inputs", Map.of(
                "title", course.getTitle(),
                "description", course.getDescription(),
                "category", course.getCategory()
            ));
            
            // 调用Dify API进行课程优化
            String optimizedContent = callDifyAPI(difyWorkflowUrl, requestBody);
            
            // 解析优化结果并更新课程信息
            // 这里简化处理，实际应该解析JSON响应
            course.setTitle(course.getTitle() + " (AI优化)");
            course.setDescription(course.getDescription() + "\n\nAI优化建议：" + optimizedContent);
            
            return course;
        } catch (Exception e) {
            throw new AIException("课程优化失败: " + e.getMessage());
        }
    }

    /**
     * 获取热门关键词列表
     * @return 热门关键词
     * @throws AIException AI相关异常
     */
    @Override
    public List<String> getHotSearchTrends() throws AIException {
        try {
            // 模拟热门关键词，实际可以从数据库或外部API获取
            trendKeyWords.clear();
            trendKeyWords.add("Java编程");
            trendKeyWords.add("Spring Boot");
            trendKeyWords.add("Vue.js");
            trendKeyWords.add("Python");
            trendKeyWords.add("机器学习");
            trendKeyWords.add("前端开发");
            trendKeyWords.add("后端开发");
            trendKeyWords.add("数据库");
            return trendKeyWords;
        } catch (Exception e) {
            throw new AIException("获取热门关键词失败: " + e.getMessage());
        }
    }

    /**
     * 返回AI生成的问答回复内容
     * @param question 问题对象
     * @return AI回复
     * @throws AIException AI相关异常
     */
    @Override
    public String autoReply(Question question) throws AIException {
        try {
            // 获取课程信息
            Course course = null;
            try {
                course = courseService.getCourseDetail(question.getCourseId());
            } catch (Exception e) {
                return "抱歉，无法找到相关课程信息。";
            }
            
            // 构建AI问答请求
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("inputs", Map.of(
                "course_title", course.getTitle(),
                "course_description", course.getDescription(),
                "course_category", course.getCategory(),
                "user_question", question.getQuestion()
            ));
            
            // 调用Dify API进行AI问答
            String aiReply = callDifyAPI(difyWorkflowUrl, requestBody);
            
            return aiReply != null && !aiReply.isEmpty() ? aiReply : "抱歉，AI暂时无法回答您的问题，请稍后再试。";
        } catch (Exception e) {
            throw new AIException("AI问答失败: " + e.getMessage());
        }
    }

    /**
     * 为课程打分用于推荐排序
     * @param courseId 课程ID
     * @return 课程分数
     * @throws AIException AI相关异常
     */
    @Override
    public Double scoreCourse(Long courseId) throws AIException {
        try {
            Course course = null;
            try {
                course = courseService.getCourseDetail(courseId);
            } catch (Exception e) {
                return 0.0;
            }
            
            // 简单的评分算法，实际可以使用更复杂的AI模型
            double score = 0.0;
            
            // 根据课程标题长度评分
            if (course.getTitle() != null) {
                score += Math.min(course.getTitle().length() * 0.1, 2.0);
            }
            
            // 根据课程描述长度评分
            if (course.getDescription() != null) {
                score += Math.min(course.getDescription().length() * 0.01, 3.0);
            }
            
            // 根据课程时长评分
            if (course.getDuration() != null) {
                score += Math.min(course.getDuration() * 0.01, 2.0);
            }
            
            // 根据价格评分（免费课程加分）
            if (course.getPrice() != null && course.getPrice().doubleValue() == 0) {
                score += 1.0;
            }
            
            return Math.min(score, 10.0); // 最高10分
        } catch (Exception e) {
            throw new AIException("课程评分失败: " + e.getMessage());
        }
    }

    /**
     * 调用Dify API
     * @param url API地址
     * @param requestBody 请求体
     * @return API响应
     */
    private String callDifyAPI(String url, Map<String, Object> requestBody) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + difyApiKey);
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                // 解析Dify API响应
                if (responseBody.containsKey("answer")) {
                    return (String) responseBody.get("answer");
                } else if (responseBody.containsKey("message")) {
                    return (String) responseBody.get("message");
                }
            }
            
            return "AI服务暂时不可用，请稍后再试。";
        } catch (Exception e) {
            // 如果API调用失败，返回默认回复
            return "基于您的问题，我建议您查看课程的相关章节。如果问题仍然存在，请联系课程讲师获取帮助。";
        }
    }
}
