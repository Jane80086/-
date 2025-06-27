package com.cemenghui.course.service;

import com.cemenghui.course.entity.Course;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class MCPService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${mcp.server.url:http://127.0.0.1:6277}")
    private String mcpServerUrl;

    @Value("${mcp.server.auth-token:}")
    private String authToken;

    /**
     * 优化课程内容
     */
    public CourseOptimizationResult optimizeCourseContent(Course course, String targetAudience, String courseType) {
        String url = mcpServerUrl + "/tools/optimize_course_content";
        
        Map<String, Object> request = new HashMap<>();
        request.put("original_title", course.getTitle());
        request.put("original_description", course.getDescription());
        request.put("target_audience", targetAudience != null ? targetAudience : "初学者");
        request.put("course_type", courseType != null ? courseType : "技术");
        request.put("optimization_focus", "both");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (authToken != null && !authToken.isEmpty()) {
            headers.set("Authorization", "Bearer " + authToken);
        }

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);
        
        return restTemplate.postForObject(url, entity, CourseOptimizationResult.class);
    }

    /**
     * 分析课程SEO效果
     */
    public SEOAnalysisResult analyzeCourseSEO(String title, String description) {
        String url = mcpServerUrl + "/tools/analyze_course_seo";
        
        Map<String, Object> request = new HashMap<>();
        request.put("title", title);
        request.put("description", description);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (authToken != null && !authToken.isEmpty()) {
            headers.set("Authorization", "Bearer " + authToken);
        }

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);
        
        return restTemplate.postForObject(url, entity, SEOAnalysisResult.class);
    }

    /**
     * 生成课程模板
     */
    public CourseTemplateResult generateCourseTemplates(String courseType, String targetAudience) {
        String url = mcpServerUrl + "/tools/generate_course_templates";
        
        Map<String, Object> request = new HashMap<>();
        request.put("course_type", courseType);
        request.put("target_audience", targetAudience != null ? targetAudience : "初学者");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (authToken != null && !authToken.isEmpty()) {
            headers.set("Authorization", "Bearer " + authToken);
        }

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);
        
        return restTemplate.postForObject(url, entity, CourseTemplateResult.class);
    }

    // 内部结果类
    public static class CourseOptimizationResult {
        private String optimized_title;
        private String optimized_description;
        private String[] improvement_suggestions;
        private String[] seo_keywords;

        // Getters and Setters
        public String getOptimizedTitle() { return optimized_title; }
        public void setOptimizedTitle(String optimized_title) { this.optimized_title = optimized_title; }
        
        public String getOptimizedDescription() { return optimized_description; }
        public void setOptimizedDescription(String optimized_description) { this.optimized_description = optimized_description; }
        
        public String[] getImprovementSuggestions() { return improvement_suggestions; }
        public void setImprovementSuggestions(String[] improvement_suggestions) { this.improvement_suggestions = improvement_suggestions; }
        
        public String[] getSeoKeywords() { return seo_keywords; }
        public void setSeoKeywords(String[] seo_keywords) { this.seo_keywords = seo_keywords; }
    }

    public static class SEOAnalysisResult {
        private int seo_score;
        private int title_length;
        private int description_length;
        private String[] suggestions;
        private String grade;

        // Getters and Setters
        public int getSeoScore() { return seo_score; }
        public void setSeoScore(int seo_score) { this.seo_score = seo_score; }
        
        public int getTitleLength() { return title_length; }
        public void setTitleLength(int title_length) { this.title_length = title_length; }
        
        public int getDescriptionLength() { return description_length; }
        public void setDescriptionLength(int description_length) { this.description_length = description_length; }
        
        public String[] getSuggestions() { return suggestions; }
        public void setSuggestions(String[] suggestions) { this.suggestions = suggestions; }
        
        public String getGrade() { return grade; }
        public void setGrade(String grade) { this.grade = grade; }
    }

    public static class CourseTemplateResult {
        private String[] titles;
        private String[] descriptions;

        // Getters and Setters
        public String[] getTitles() { return titles; }
        public void setTitles(String[] titles) { this.titles = titles; }
        
        public String[] getDescriptions() { return descriptions; }
        public void setDescriptions(String[] descriptions) { this.descriptions = descriptions; }
    }
} 