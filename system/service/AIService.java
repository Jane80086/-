package com.system.service;

import java.util.Map;

public interface AIService {
    String askAI(String question);
    String generateWelcome(Map<String, String> userInfo);
    String generateReportAnalysis(Map<String, Object> reportData);
} 