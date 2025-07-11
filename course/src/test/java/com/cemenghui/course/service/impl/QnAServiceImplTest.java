package com.cemenghui.course.service.impl;

import com.cemenghui.course.dao.QuestionDao;
import com.cemenghui.course.entity.Question;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class QnAServiceImplTest {
    @InjectMocks
    private QnAServiceImpl qnaService;
    @Mock
    private QuestionDao questionDao;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    void getQuestions_shouldReturnList() {
        Long courseId = 1L;
        List<Question> questionList = Collections.singletonList(new Question());
        when(questionDao.selectList(any())).thenReturn(questionList);
        List<Question> result = qnaService.getQuestions(courseId);
        assertEquals(1, result.size());
        verify(questionDao).selectList(any());
    }
    @Test
    void askQuestion_shouldInsert() {
        Question question = new Question();
        question.setId(1L);
        when(questionDao.insert(question)).thenReturn(1);
        Question result = qnaService.askQuestion(question);
        assertEquals(1L, result.getId());
        verify(questionDao).insert(question);
    }
    @Test
    void deleteQuestion_shouldDelete() {
        Long id = 1L;
        Question question = new Question();
        question.setId(id);
        when(questionDao.selectById(id)).thenReturn(question);
        when(questionDao.deleteById(id)).thenReturn(1);
        boolean result = qnaService.deleteQuestion(id);
        assertTrue(result);
        verify(questionDao).deleteById(id);
    }
} 