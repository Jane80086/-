package com.cemenghui.course.controller;

import com.cemenghui.course.service.QnAService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import com.cemenghui.course.entity.Question;
import com.cemenghui.course.common.Result;

class QnAControllerTest {
    @InjectMocks
    private QnAController qnAController;
    @Mock
    private QnAService qnAService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAutoReply_Success() {
        Long id = 1L;
        Question q = new Question();
        q.setId(id);
        when(qnAService.autoReply(any())).thenReturn("AI答复");
        Result response = qnAController.autoReply(id);
        assertEquals("AI回复生成成功", response.getMessage());
        assertEquals("AI答复", response.getData());
    }

    @Test
    void testAutoReply_Exception() {
        Long id = 1L;
        when(qnAService.autoReply(any())).thenThrow(new RuntimeException("fail"));
        Result response = qnAController.autoReply(id);
        assertTrue(response.getMessage().contains("AI回复生成失败"));
    }
} 