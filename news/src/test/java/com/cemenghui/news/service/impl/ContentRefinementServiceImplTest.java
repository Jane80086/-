package com.cemenghui.news.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * ContentRefinementServiceImpl 的单元测试类。
 * 使用 JUnit 5 和 Mockito。
 */
@ExtendWith(MockitoExtension.class) // 启用 Mockito 扩展
class ContentRefinementServiceImplTest {

    @Mock // 创建一个 HttpClient 的模拟对象
    private HttpClient mockHttpClient;

    @Mock // 创建一个 HttpResponse 的模拟对象
    private HttpResponse<String> mockHttpResponse;

    // 使用真实的 ObjectMapper，因为它不涉及外部依赖
    private final ObjectMapper objectMapper = new ObjectMapper();

    private ContentRefinementServiceImpl contentRefinementService;

    @BeforeEach
    void setUp() {
        // 在每个测试方法运行前，初始化被测试的服务，并注入模拟的 HttpClient
        contentRefinementService = new ContentRefinementServiceImpl(mockHttpClient, objectMapper);
    }

    // --- 场景 1: 正常情况测试 ---

    @Test
    @DisplayName("正常情况 - 成功润色内容并移除 think 标签")
    void refineContent_shouldReturnRefinedContent_whenApiCallIsSuccessful() throws IOException, InterruptedException {
        // Arrange (准备)
        String originalContent = "这是一个需要润色的新闻稿。";
        String expectedContent = "这是一篇经过专业润色的新闻稿，内容详实，观点明确。";
        // 模拟 Ollama API 返回的原始 JSON 响应，其中包含 <think> 标签
        String ollamaResponseJson = "{\"response\":\"<think>正在分析文本...</think>\\n这是一篇经过专业润色的新闻稿，内容详实，观点明确。\"}";

        // 当模拟的 HttpResponse 被调用时，设置其返回状态码为 200 (成功)
        when(mockHttpResponse.statusCode()).thenReturn(200);
        // 设置其返回体为我们准备的 JSON 字符串
        when(mockHttpResponse.body()).thenReturn(ollamaResponseJson);

        // 当模拟的 HttpClient 的 send 方法被以任何参数调用时，都返回我们准备好的 mockHttpResponse
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockHttpResponse);

        // Act (执行)
        String actualContent = contentRefinementService.refineContent(originalContent);

        // Assert (断言)
        assertThat(actualContent).isNotNull();
        assertThat(actualContent).isEqualTo(expectedContent);

        // Verify (验证) - 确认 mockHttpClient 的 send 方法确实被调用了 1 次
        verify(mockHttpClient, times(1)).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
    }

    // --- 场景 2: 异常情况测试 ---

    @Test
    @DisplayName("异常情况 - 输入内容为 null")
    void refineContent_shouldReturnNull_whenContentIsNull() throws IOException, InterruptedException {
        // Act
        String result = contentRefinementService.refineContent(null);

        // Assert
        assertThat(result).isNull();
        // 验证 HttpClient 的 send 方法从未被调用
        verify(mockHttpClient, never()).send(any(), any());
    }

    @Test
    @DisplayName("异常情况 - 输入内容为空字符串")
    void refineContent_shouldReturnNull_whenContentIsEmpty() throws IOException, InterruptedException {
        // Act
        String result = contentRefinementService.refineContent("");

        // Assert
        assertThat(result).isNull();
        verify(mockHttpClient, never()).send(any(), any());
    }


    @Test
    @DisplayName("异常情况 - 输入内容仅包含空格")
    void refineContent_shouldReturnNull_whenContentIsBlank() throws IOException, InterruptedException {
        // Act
        String result = contentRefinementService.refineContent("   ");

        // Assert
        assertThat(result).isNull();
        verify(mockHttpClient, never()).send(any(), any());
    }

    @Test
    @DisplayName("异常情况 - Ollama API 返回非 200 状态码")
    void refineContent_shouldReturnNull_whenApiReturnsError() throws IOException, InterruptedException {
        // Arrange
        String originalContent = "一些内容";
        when(mockHttpResponse.statusCode()).thenReturn(500); // 模拟服务器内部错误
        when(mockHttpResponse.body()).thenReturn("Internal Server Error");
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockHttpResponse);

        // Act
        String result = contentRefinementService.refineContent(originalContent);

        // Assert
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("异常情况 - 调用 API 时发生 IOException")
    void refineContent_shouldReturnNull_whenHttpClientThrowsIOException() throws IOException, InterruptedException {
        // Arrange
        String originalContent = "一些内容";
        // 模拟 HttpClient 在发送请求时抛出 IOException
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenThrow(new IOException("网络连接失败"));

        // Act
        String result = contentRefinementService.refineContent(originalContent);

        // Assert
        assertThat(result).isNull();
    }

    // --- 场景 3: 边界条件测试 ---

    @Test
    @DisplayName("边界条件 - API 响应中 response 字段为空")
    void refineContent_shouldReturnNull_whenApiResponseIsEmpty() throws IOException, InterruptedException {
        // Arrange
        String originalContent = "一些内容";
        String ollamaResponseJson = "{\"response\":\"\"}";

        when(mockHttpResponse.statusCode()).thenReturn(200);
        when(mockHttpResponse.body()).thenReturn(ollamaResponseJson);
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockHttpResponse);

        // Act
        String result = contentRefinementService.refineContent(originalContent);

        // Assert
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("边界条件 - API 响应移除 think 标签后为空")
    void refineContent_shouldReturnNull_whenResponseIsOnlyThinkTag() throws IOException, InterruptedException {
        // Arrange
        String originalContent = "一些内容";
        String ollamaResponseJson = "{\"response\":\"<think>只有思考，没有内容。</think>\"}";

        when(mockHttpResponse.statusCode()).thenReturn(200);
        when(mockHttpResponse.body()).thenReturn(ollamaResponseJson);
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockHttpResponse);

        // Act
        String result = contentRefinementService.refineContent(originalContent);

        // Assert
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("边界条件 - API 响应不包含 response 字段")
    void refineContent_shouldReturnNull_whenResponseFieldIsMissing() throws IOException, InterruptedException {
        // Arrange
        String originalContent = "一些内容";
        String malformedJson = "{\"model\":\"deepseek-r1:1.5b\"}"; // 缺少 "response" 字段

        when(mockHttpResponse.statusCode()).thenReturn(200);
        when(mockHttpResponse.body()).thenReturn(malformedJson);
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockHttpResponse);

        // Act
        String result = contentRefinementService.refineContent(originalContent);

        // Assert
        assertThat(result).isNull();
    }
}
