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
        // 无AI服务，直接返回原始标题
        return originalTitle;
    }

    @Override
    public String optimizeDescription(String originalDescription, String category, String title) {
        // 无AI服务，直接返回原始简介
        return originalDescription;
    }

    @Override
    public Map<String, String> optimizeCourseInfo(String title, String description, String category) {
        // 无AI服务，直接返回原始内容
        Map<String, String> result = new HashMap<>();
        result.put("optimized_title", title);
        result.put("optimized_description", description);
        return result;
    }
} 