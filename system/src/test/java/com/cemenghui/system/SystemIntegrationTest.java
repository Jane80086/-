package com.cemenghui.system;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SystemIntegrationTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testLogin() {
        String loginJson = "{\"account\":\"admin\",\"password\":\"admin123\"}";
        ResponseEntity<String> response = restTemplate.postForEntity("/api/login", loginJson, String.class);
        System.out.println("Response status: " + response.getStatusCodeValue());
        System.out.println("Response body: " + response.getBody());
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
    }
} 