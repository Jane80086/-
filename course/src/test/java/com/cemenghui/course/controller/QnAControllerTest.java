package com.cemenghui.course.controller;

import com.cemenghui.course.common.Result;
import com.cemenghui.course.entity.Question;
import com.cemenghui.course.service.QnAService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class QnAControllerTest {
    @InjectMocks
    private QnAController controller;
    @Mock
    private QnAService qnaService;
    @BeforeEach
    void setUp() { MockitoAnnotations.openMocks(this); }

    @Test
    void getQuestionsByCourse_shouldReturnList() {
        List<Question> questions = Collections.singletonList(new Question());
        when(qnaService.getQuestions(anyLong())).thenReturn(questions);
        Result result = controller.getQuestionsByCourse(1L);
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
    }
    @Test
    void askQuestion_shouldReturnSuccess() {
        Question question = new Question();
        question.setId(1L);
        when(qnaService.askQuestion(any(Question.class))).thenReturn(question);
        Result result = controller.askQuestion(question);
        assertEquals(200, result.getCode());
    }
    @Test
    void deleteQuestion_shouldReturnSuccess() {
        when(qnaService.deleteQuestion(anyLong())).thenReturn(true);
        Result result = controller.deleteQuestion(1L);
        assertEquals(200, result.getCode());
    }
} 