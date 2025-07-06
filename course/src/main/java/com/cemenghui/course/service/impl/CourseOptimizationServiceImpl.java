package com.cemenghui.course.service.impl;

import com.cemenghui.course.service.CourseOptimizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;

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
            // 构建请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + difyOptimizationKey);
            
            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("inputs", Map.of(
                "original_title", originalTitle,
                "category", category != null ? category : "",
                "description", description != null ? description : "",
                "optimization_type", "title"
            ));
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            
            // 发送请求到Dify工作流
            ResponseEntity<Map> response = restTemplate.postForEntity(
                difyOptimizationUrl, 
                entity, 
                Map.class
            );
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> result = response.getBody();
                // 根据Dify返回格式解析优化后的标题
                if (result.containsKey("optimized_title")) {
                    return (String) result.get("optimized_title");
                } else if (result.containsKey("title")) {
                    return (String) result.get("title");
                } else if (result.containsKey("output")) {
                    return (String) result.get("output");
                } else {
                    return originalTitle; // 如果AI优化失败，返回原标题
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
            // 构建请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + difyOptimizationKey);
            
            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("inputs", Map.of(
                "original_description", originalDescription,
                "category", category != null ? category : "",
                "title", title != null ? title : "",
                "optimization_type", "description"
            ));
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            
            // 发送请求到Dify工作流
            ResponseEntity<Map> response = restTemplate.postForEntity(
                difyOptimizationUrl, 
                entity, 
                Map.class
            );
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> result = response.getBody();
                // 根据Dify返回格式解析优化后的简介
                if (result.containsKey("optimized_description")) {
                    return (String) result.get("optimized_description");
                } else if (result.containsKey("description")) {
                    return (String) result.get("description");
                } else if (result.containsKey("output")) {
                    return (String) result.get("output");
                } else {
                    return originalDescription; // 如果AI优化失败，返回原简介
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
            // 构建请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + difyOptimizationKey);
            
            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("inputs", Map.of(
                "original_title", title != null ? title : "",
                "original_description", description != null ? description : "",
                "category", category != null ? category : "",
                "optimization_type", "both"
            ));
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            
            // 发送请求到Dify工作流
            ResponseEntity<Map> response = restTemplate.postForEntity(
                difyOptimizationUrl, 
                entity, 
                Map.class
            );
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> aiResult = response.getBody();
                
                // 解析优化后的标题
                if (aiResult.containsKey("optimized_title")) {
                    result.put("optimized_title", (String) aiResult.get("optimized_title"));
                } else if (aiResult.containsKey("title")) {
                    result.put("optimized_title", (String) aiResult.get("title"));
                } else {
                    result.put("optimized_title", title);
                }
                
                // 解析优化后的简介
                if (aiResult.containsKey("optimized_description")) {
                    result.put("optimized_description", (String) aiResult.get("optimized_description"));
                } else if (aiResult.containsKey("description")) {
                    result.put("optimized_description", (String) aiResult.get("description"));
                } else {
                    result.put("optimized_description", description);
                }
                
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