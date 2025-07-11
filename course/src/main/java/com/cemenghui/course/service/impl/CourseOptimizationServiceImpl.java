package com.cemenghui.course.service.impl;

import com.cemenghui.course.service.CourseOptimizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class CourseOptimizationServiceImpl implements CourseOptimizationService {
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Value("${dify.course.optimization.url:http://localhost:8088/workflow/6vZFPlP5pITAU8Qk}")
    private String difyOptimizationUrl;
    
    @Value("${dify.course.optimization.key:app-sDs04lJf0Zp0sxQEoemW05M1}")
    private String difyOptimizationKey;

    @Override
    public String optimizeTitle(String originalTitle, String category, String description) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("title", originalTitle);
            requestBody.put("description", description != null ? description : "");
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(
                "http://localhost:5001/ai/optimize",
                entity,
                Map.class
            );
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> result = response.getBody();
                if (result.containsKey("optimized_title")) {
                    return (String) result.get("optimized_title");
                } else if (result.containsKey("title")) {
                    return (String) result.get("title");
                } else if (result.containsKey("output")) {
                    return (String) result.get("output");
                } else {
                    return originalTitle;
                }
            } else {
                return originalTitle;
            }
        } catch (Exception e) {
            System.err.println("AI优化标题失败: " + e.getMessage());
            return originalTitle;
        }
    }

    @Override
    public String optimizeDescription(String originalDescription, String category, String title) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("title", title != null ? title : "");
            requestBody.put("description", originalDescription);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(
                "http://localhost:5001/ai/optimize",
                entity,
                Map.class
            );
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> result = response.getBody();
                if (result.containsKey("optimized_description")) {
                    return (String) result.get("optimized_description");
                } else if (result.containsKey("description")) {
                    return (String) result.get("description");
                } else if (result.containsKey("output")) {
                    return (String) result.get("output");
                } else {
                    return originalDescription;
                }
            } else {
                return originalDescription;
            }
        } catch (Exception e) {
            System.err.println("AI优化简介失败: " + e.getMessage());
            return originalDescription;
        }
    }

    @Override
    public Map<String, String> optimizeCourseInfo(String title, String description, String category) {
        Map<String, String> result = new HashMap<>();
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("title", title);
            requestBody.put("description", description);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(
                "http://localhost:5001/ai/optimize",
                entity,
                Map.class
            );
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> aiResult = response.getBody();
                result.put("optimized_title", (String) aiResult.getOrDefault("optimized_title", title));
                result.put("optimized_description", (String) aiResult.getOrDefault("optimized_description", description));
            } else {
                result.put("optimized_title", title);
                result.put("optimized_description", description);
            }
        } catch (Exception e) {
            System.err.println("AI批量优化课程信息失败: " + e.getMessage());
            result.put("optimized_title", title);
            result.put("optimized_description", description);
        }
        return result;
    }
} 