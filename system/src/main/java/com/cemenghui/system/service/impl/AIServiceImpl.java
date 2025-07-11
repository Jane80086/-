package com.cemenghui.system.service.impl;

import com.cemenghui.system.service.AIService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.util.Map;

@Service
public class AIServiceImpl implements AIService {
    @Value("${openai.api.key}")
    private String openaiApiKey;

    @Value("${openai.api.url:https://api.openai.com}")
    private String openaiApiUrl;

    @Override
    public String askAI(String question) {
        String url = openaiApiUrl + "/v1/chat/completions";
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openaiApiKey);
        ObjectMapper mapper = new ObjectMapper();
        try {
            ObjectNode requestBody = mapper.createObjectNode();
            requestBody.put("model", "gpt-3.5-turbo");
            ArrayNode messages = mapper.createArrayNode();
            ObjectNode userMsg = mapper.createObjectNode();
            userMsg.put("role", "user");
            userMsg.put("content", question);
            messages.add(userMsg);
            requestBody.set("messages", messages);
            HttpEntity<String> entity = new HttpEntity<>(mapper.writeValueAsString(requestBody), headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            ObjectNode json = (ObjectNode) mapper.readTree(response.getBody());
            String answer = json.withArray("choices")
                    .get(0)
                    .path("message")
                    .path("content")
                    .asText();
            return answer;
        } catch (Exception e) {
            return "AI服务暂时不可用，请稍后再试。";
        }
    }

    @Override
    public String generateWelcome(Map<String, String> userInfo) {
        String url = openaiApiUrl + "/v1/chat/completions";
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openaiApiKey);
        ObjectMapper mapper = new ObjectMapper();
        try {
            String nickname = userInfo.getOrDefault("nickname", "用户");
            String enterprise = userInfo.getOrDefault("enterprise", "本系统");
            String prompt = String.format(
                    "请用一句温暖、积极的话欢迎名为%s的%s，他/她来自%s。内容不超过30字。",
                    nickname, enterprise
            );
            ObjectNode requestBody = mapper.createObjectNode();
            requestBody.put("model", "gpt-3.5-turbo");
            ArrayNode messages = mapper.createArrayNode();
            ObjectNode userMsg = mapper.createObjectNode();
            userMsg.put("role", "user");
            userMsg.put("content", prompt);
            messages.add(userMsg);
            requestBody.set("messages", messages);
            HttpEntity<String> entity = new HttpEntity<>(mapper.writeValueAsString(requestBody), headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            ObjectNode json = (ObjectNode) mapper.readTree(response.getBody());
            String answer = json.withArray("choices")
                    .get(0)
                    .path("message")
                    .path("content")
                    .asText();
            return answer;
        } catch (Exception e) {
            e.printStackTrace();
            return "欢迎来到系统，祝你工作愉快！";
        }
    }

    @Override
    public String generateReportAnalysis(Map<String, Object> reportData) {
        String url = openaiApiUrl + "/v1/chat/completions";
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openaiApiKey);
        ObjectMapper mapper = new ObjectMapper();
        try {
            // 1. 将报表数据转为简明文本
            StringBuilder sb = new StringBuilder();
            reportData.forEach((k, v) -> sb.append(k).append(": ").append(v).append("; "));
            String dataText = sb.toString();
            String prompt = String.format(
                    "请用简洁自然语言解读下述数据，生成1-2句话的分析摘要：%s",
                    dataText
            );
            ObjectNode requestBody = mapper.createObjectNode();
            requestBody.put("model", "gpt-3.5-turbo");
            ArrayNode messages = mapper.createArrayNode();
            ObjectNode userMsg = mapper.createObjectNode();
            userMsg.put("role", "user");
            userMsg.put("content", prompt);
            messages.add(userMsg);
            requestBody.set("messages", messages);
            HttpEntity<String> entity = new HttpEntity<>(mapper.writeValueAsString(requestBody), headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            ObjectNode json = (ObjectNode) mapper.readTree(response.getBody());
            String answer = json.withArray("choices")
                    .get(0)
                    .path("message")
                    .path("content")
                    .asText();
            return answer;
        } catch (Exception e) {
            e.printStackTrace();
            return "AI分析服务暂时不可用，请稍后再试。";
        }
    }
} 