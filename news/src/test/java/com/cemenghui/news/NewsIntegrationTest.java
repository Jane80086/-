package com.cemenghui.news;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class NewsIntegrationTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testNewsList() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/news/list", String.class);
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
    }
} 