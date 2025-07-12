package com.cemenghui.course.service.impl;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;

class AIClientTest {
    private AIClient aiClient;
    private MockWebServer server;

    @BeforeEach
    void setUp() throws Exception {
        aiClient = new AIClient();
        server = new MockWebServer();
        server.start();
        ReflectionTestUtils.setField(aiClient, "difyApiKey", "test-key");
        ReflectionTestUtils.setField(aiClient, "difyBaseUrl", server.url("/").toString().replaceAll("/$", ""));
    }

    @AfterEach
    void tearDown() throws Exception {
        server.shutdown();
    }

    @Test
    void testChat_Normal() throws Exception {
        String json = "{\"choices\":[{\"message\":{\"content\":\"AI回复\"}}]}";
        server.enqueue(new MockResponse().setBody(json).setResponseCode(200));
        String result = aiClient.chat("你好");
        assertEquals("AI回复", result);
    }

    @Test
    void testChat_Error() {
        server.enqueue(new MockResponse().setResponseCode(500));
        assertThrows(IOException.class, () -> aiClient.chat("error"));
    }

    @Test
    void testChat_EmptyChoices() throws Exception {
        String json = "{\"choices\":[]}";
        server.enqueue(new MockResponse().setBody(json).setResponseCode(200));
        String result = aiClient.chat("empty");
        assertEquals(json, result);
    }

    @Test
    void testOptimize() throws Exception {
        server.enqueue(new MockResponse().setBody("{\"choices\":[{\"message\":{\"content\":\"优化后内容\"}}]}\n").setResponseCode(200));
        String result = aiClient.optimize("标题", "简介");
        assertEquals("优化后内容", result);
    }

    @Test
    void testAnswerQuestion() throws Exception {
        server.enqueue(new MockResponse().setBody("{\"choices\":[{\"message\":{\"content\":\"答复\"}}]}\n").setResponseCode(200));
        String result = aiClient.answerQuestion("问题");
        assertEquals("答复", result);
    }
} 