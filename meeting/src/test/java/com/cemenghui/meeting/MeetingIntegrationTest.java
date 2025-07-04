package com.cemenghui.meeting;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import java.util.HashMap;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MeetingIntegrationTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testMeetingList() {
        // 构造请求体（MeetingQuery），可根据实际参数调整
        Map<String, Object> query = new HashMap<>();
        // query.put("xxx", value); // 如有必要可补充参数

        // 构造请求头，模拟登录后的 token（如有需要请替换为真实 token）
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "test-token-or-empty");

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(query, headers);
        ResponseEntity<String> response = restTemplate.postForEntity("/api/meeting/list", request, String.class);
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
    }
} 