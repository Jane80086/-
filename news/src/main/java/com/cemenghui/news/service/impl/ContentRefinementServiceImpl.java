package com.cemenghui.news.service.impl;

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

@Service // 标记为 Spring Bean
@Slf4j // 使用 Lombok 的 @Slf4j 简化日志
public class ContentRefinementServiceImpl implements ContentRefinementService { // 实现接口

    private static final String OLLAMA_API_URL = "http://localhost:11434/api/generate";
    private static final String MODEL_NAME = "deepseek-r1:1.5b";

    // 编译正则表达式，用于匹配并移除 <think>...</think> 标签及其内容
    // (?s) 是 Pattern.DOTALL 的内联标志，让 '.' 匹配包括换行符在内的所有字符
    // \\n? 匹配可选的换行符，以确保移除标签后的多余换行也被清除
    private static final Pattern THINK_TAG_PATTERN = Pattern.compile("(?s)<think>.*?</think>\\n?");

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    // 构造器注入 HttpClient 和 ObjectMapper (Spring Boot 推荐方式)
    public ContentRefinementServiceImpl() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * 调用 AI 模型对文本内容进行润色。
     * @param originalContent 待润色的原始文本
     * @return 润色后的文本，如果失败则返回 null
     */
    @Override // 标记实现接口方法
    public String refineContent(String originalContent) {
        if (originalContent == null || originalContent.trim().isEmpty()) {
            log.warn("Attempted to refine empty or null content.");
            return null; // 或者抛出 IllegalArgumentException
        }

        // 构建 Prompt，明确指示模型进行润色
        // 确保 prompt 字符串中没有未转义的特殊字符，因为它是作为 JSON 字段的值发送的
        String prompt = "请帮我润色以下新闻稿内容，使其语言更流畅、专业，并修正潜在的语法错误和拼写错误。请直接返回润色后的完整内容，不需要解释或额外的对话。\n\n" + originalContent;

        String requestBodyJson;
        try {
            // 使用 Jackson ObjectMapper 将 Java 对象转换为 JSON 字符串，并处理转义
            requestBodyJson = objectMapper.writeValueAsString(
                    new OllamaRequest(MODEL_NAME, prompt, false)
            );
        } catch (IOException e) {
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
                // 解析 Ollama 返回的 JSON 响应
                JsonNode rootNode = objectMapper.readTree(response.body());
                // Ollama 的 /api/generate 接口返回的文本通常在 "response" 字段
                String rawRefinedText = rootNode.path("response").asText();

                if (rawRefinedText != null && !rawRefinedText.isEmpty()) {
                    // 使用正则表达式移除 <think>...</think> 标签及其内容
                    String cleanedRefinedText = THINK_TAG_PATTERN.matcher(rawRefinedText).replaceAll("").trim();

                    if (cleanedRefinedText.isEmpty()) {
                        log.warn("Ollama API 响应去除思考过程后内容为空。原始响应: {}", rawRefinedText);
                        // 如果清理后内容为空，可以选择返回原始内容或null，这里返回null
                        return null;
                    }

                    log.info("Content refined successfully.");
                    return cleanedRefinedText; // 返回处理后的文本
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
            Thread.currentThread().interrupt(); // 重新中断线程，表示当前线程被中断
            return null;
        }
    }

    // 内部类，用于构建 Ollama API 的请求体
    private static class OllamaRequest {
        public String model;
        public String prompt;
        public boolean stream;

        public OllamaRequest(String model, String prompt, boolean stream) {
            this.model = model;
            this.prompt = prompt;
            this.stream = stream;
        }
    }
}
