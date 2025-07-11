package com.cemenghui.course.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class AIClient {
    @Value("${dify.api-key}")
    private String difyApiKey;

    @Value("${dify.base-url}")
    private String difyBaseUrl;

    private final OkHttpClient client = new OkHttpClient();

    /**
     * 通用Dify Chat接口调用，返回AI回复内容
     */
    public String chat(String prompt) throws IOException {
        MediaType mediaType = MediaType.parse("application/json");
        String bodyJson = "{\n" +
                "  \"model\": \"gpt-3.5-turbo\",\n" +
                "  \"messages\": [{\"role\": \"user\", \"content\": \"" + prompt + "\"}]\n" +
                "}";
        RequestBody body = RequestBody.create(mediaType, bodyJson);
        Request request = new Request.Builder()
                .url(difyBaseUrl + "/chat/completions")
                .post(body)
                .addHeader("Authorization", "Bearer " + difyApiKey)
                .addHeader("Content-Type", "application/json")
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Dify API error: " + response);
            String responseBody = response.body().string();
            JSONObject json = JSON.parseObject(responseBody);
            if (json.containsKey("choices")) {
                JSONArray choices = json.getJSONArray("choices");
                if (choices.size() > 0) {
                    JSONObject message = choices.getJSONObject(0).getJSONObject("message");
                    return message.getString("content");
                }
            }
            return responseBody;
        }
    }

    /**
     * 课程优化接口，传递自定义prompt
     */
    public String optimize(String title, String description) throws IOException {
        String prompt = "请优化以下课程标题和简介，使其更吸引人：\n标题：" + title + "\n简介：" + description;
        return chat(prompt);
    }

    /**
     * 问答接口，直接传递问题
     */
    public String answerQuestion(String question) throws IOException {
        return chat(question);
    }
} 