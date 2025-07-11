package com.cemenghui.course.service.impl;

import com.cemenghui.course.dao.QuestionDao;
import com.cemenghui.course.entity.Question;
import com.cemenghui.course.service.AIService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class QnAServiceImplTest {
    @InjectMocks
    private QnAServiceImpl qnAServiceImpl;
    @Mock
    private QuestionDao questionDao;
    @Mock
    private AIService aiService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAutoReply_Success() throws com.cemenghui.course.service.AIException {
        Question q = new Question();
        q.setId(1L);
        q.setQuestion("test?");
        when(aiService.autoReply(q)).thenReturn("AI答复");
        when(questionDao.selectById(1L)).thenReturn(q);
        String result = qnAServiceImpl.autoReply(q);
        assertEquals("AI答复", result);
    }

    @Test
    void testAskQuestion() {
        Question q = new Question();
        when(questionDao.insert(any())).thenReturn(1);
        Question result = qnAServiceImpl.askQuestion(q);
        assertNotNull(result);
    }
} 