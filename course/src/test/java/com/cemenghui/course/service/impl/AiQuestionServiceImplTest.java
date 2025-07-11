package com.cemenghui.course.service.impl;

import com.cemenghui.course.dao.QuestionDao;
import com.cemenghui.course.entity.Question;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AiQuestionServiceImplTest {
    @InjectMocks
    private AiQuestionServiceImpl service;
    @Mock
    private QuestionDao questionDao;
    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAskQuestion() {
        Long courseId = 1L;
        Long userId = 2L;
        String questionContent = "AI如何工作？";
        // mock Dify返回
        Map<String, Object> difyResp = Map.of(
            "choices", java.util.List.of(
                Map.of("message", Map.of("content", "AI答复"))
            )
        );
        when(restTemplate.postForEntity(anyString(), any(), eq(Map.class)))
                .thenReturn(new ResponseEntity<>(difyResp, HttpStatus.OK));
        // mock insert
        when(questionDao.insert(any())).thenReturn(1);
        // mock selectById
        when(questionDao.selectById(anyLong())).thenReturn(null);

        Question result = service.askQuestion(courseId, userId, questionContent);
        assertNotNull(result);
        assertEquals(courseId, result.getCourseId());
        assertEquals(userId, result.getUserId());
        assertEquals(questionContent, result.getQuestion());
        assertEquals("AI答复", result.getAiAnswer());
    }
} 