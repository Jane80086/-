package com.cemenghui.course.controller;

import com.cemenghui.course.common.Result;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AIController {

    @Value("${dify.api-key}")
    private String difyApiKey;

    @Value("${dify.base-url}")
    private String difyBaseUrl;

    @PostMapping("/optimize")
    public ResponseEntity<Result> optimize(@RequestBody Map<String, Object> request) {
        String title = (String) request.get("title");
        String description = (String) request.get("description");
        String prompt = "请优化以下课程标题和简介，使其更吸引人：\\n标题：" + title + "\\n简介：" + description;
        try {
            String optimized = callDify(prompt);
            Map<String, Object> result = new HashMap<>();
            result.put("optimizedText", optimized);
            return ResponseEntity.ok(Result.success("AI优化成功", result));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Result.fail("AI优化失败: " + e.getMessage()));
        }
    }

    private String callDify(String prompt) throws Exception {
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");
        String bodyJson = "{\n" +
                "  \"model\": \"gpt-3.5-turbo\",\n" +
                "  \"messages\": [{\"role\": \"user\", \"content\": \"" + prompt + "\"}]\n" +
                "}";
        okhttp3.RequestBody body = okhttp3.RequestBody.create(mediaType, bodyJson);
        Request request = new Request.Builder()
                .url(difyBaseUrl + "/chat/completions")
                .post(body)
                .addHeader("Authorization", "Bearer " + difyApiKey)
                .addHeader("Content-Type", "application/json")
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new Exception("Dify API error: " + response);
            String responseBody = response.body().string();
            return responseBody;
        }
    }
} 