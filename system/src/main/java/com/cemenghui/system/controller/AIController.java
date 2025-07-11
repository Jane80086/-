package com.cemenghui.system.controller;

import com.cemenghui.system.service.AIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AIController {
    @Autowired
    private AIService aiService;

    @PostMapping("/ask")
    public Map<String, String> askAI(@RequestBody Map<String, String> body) {
        String question = body.get("question");
        String answer = aiService.askAI(question);
        return Map.of("answer", answer);
    }

    @PostMapping("/welcome")
    public Map<String, String> welcome(@RequestBody Map<String, String> userInfo) {
        String welcome = aiService.generateWelcome(userInfo);
        return Map.of("welcome", welcome);
    }

    @PostMapping("/report-analysis")
    public Map<String, String> reportAnalysis(@RequestBody Map<String, Object> reportData) {
        String analysis = aiService.generateReportAnalysis(reportData);
        return Map.of("analysis", analysis);
    }
} 
