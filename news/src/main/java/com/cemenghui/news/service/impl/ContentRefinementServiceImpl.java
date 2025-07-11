package com.cemenghui.news.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.cemenghui.news.service.ContentRefinementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.regex.Pattern;

@Service
@Slf4j
public class ContentRefinementServiceImpl implements ContentRefinementService {

    private static final String OLLAMA_API_URL = "http://localhost:11434/api/generate";
    private static final String MODEL_NAME = "deepseek-r1:1.5b";
    private static final Pattern THINK_TAG_PATTERN = Pattern.compile("(?s)<think>.*?</think>\\n?");

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    /**
     * 供 Spring 使用的主构造函数。
     * 它会使用默认的 HttpClient 和 ObjectMapper。
     */
    public ContentRefinementServiceImpl() {
        this(HttpClient.newHttpClient(), new ObjectMapper());
    }

    /**
     * 供测试使用的构造函数，允许注入模拟的依赖项。
     * @param httpClient   HTTP 客户端
     * @param objectMapper Jackson 的 ObjectMapper
     */
    public ContentRefinementServiceImpl(HttpClient httpClient, ObjectMapper objectMapper) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public String refineContent(String originalContent) {
        if (originalContent == null || originalContent.trim().isEmpty()) {
            log.warn("Attempted to refine empty or null content.");
            return null;
        }

        String prompt = "请帮我润色以下新闻稿内容，使其语言更流畅、专业，并修正潜在的语法错误和拼写错误。请直接返回润色后的完整内容，不需要解释或额外的对话。\n\n" + originalContent;

        String requestBodyJson;
        try {
            requestBodyJson = objectMapper.writeValueAsString(
                    new OllamaRequest(MODEL_NAME, prompt, false)
            );
        } catch (JsonProcessingException e) {
            log.error("Error creating JSON request body for Ollama: {}", e.getMessage());
            return null;
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(OLLAMA_API_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBodyJson))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonNode rootNode = objectMapper.readTree(response.body());
                String rawRefinedText = rootNode.path("response").asText();

                if (rawRefinedText != null && !rawRefinedText.isEmpty()) {
                    String cleanedRefinedText = THINK_TAG_PATTERN.matcher(rawRefinedText).replaceAll("").trim();

                    if (cleanedRefinedText.isEmpty()) {
                        log.warn("Ollama API 响应去除思考过程后内容为空。原始响应: {}", rawRefinedText);
                        return null;
                    }

                    log.info("Content refined successfully.");
                    return cleanedRefinedText;
                } else {
                    log.warn("Ollama API 响应中未找到润色内容或内容为空: {}", response.body());
                    return null;
                }
            } else {
                log.error("调用 Ollama API 失败，HTTP 状态码: {}，响应体: {}", response.statusCode(), response.body());
                return null;
            }
        } catch (IOException | InterruptedException e) {
            log.error("调用 Ollama API 时发生异常: {}", e.getMessage(), e);
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            return null;
        }
    }

    // 内部类，用于构建 Ollama API 的请求体
    // 注意：为了在测试中可以被 Jackson 序列化，需要提供 getter 方法
    private static class OllamaRequest {
        private final String model;
        private final String prompt;
        private final boolean stream;

        public OllamaRequest(String model, String prompt, boolean stream) {
            this.model = model;
            this.prompt = prompt;
            this.stream = stream;
        }

        public String getModel() {
            return model;
        }

        public String getPrompt() {
            return prompt;
        }

        public boolean isStream() {
            return stream;
        }
    }
}
