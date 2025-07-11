package com.cemenghui.course.service.impl;

import com.cemenghui.course.entity.Question;
import com.cemenghui.course.service.AIException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import static org.junit.jupiter.api.Assertions.*;

class AIServiceImplTest {
    @InjectMocks
    private AIServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAutoReply() throws AIException {
        AIServiceImpl service = new AIServiceImpl();
        Question question = new Question();
        question.setQuestion("AI如何工作？");
        String reply = service.autoReply(question);
        assertEquals("AI自动生成的回复", reply);
    }
} 