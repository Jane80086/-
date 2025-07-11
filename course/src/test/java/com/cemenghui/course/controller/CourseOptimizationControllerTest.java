package com.cemenghui.course.controller;

import com.cemenghui.course.service.CourseOptimizationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CourseOptimizationControllerTest {
    @InjectMocks
    private CourseOptimizationController controller;
    @Mock
    private CourseOptimizationService optimizationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testOptimize_Success() {
        String title = "t";
        String desc = "d";
        String category = "c";
        when(optimizationService.optimizeCourseInfo(title, desc, category)).thenReturn(java.util.Map.of("title", "t2"));
        ResponseEntity<?> response = controller.optimizeCourseInfo(title, desc, category);
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void testOptimize_Exception() {
        String title = "t";
        String desc = "d";
        String category = "c";
        when(optimizationService.optimizeCourseInfo(title, desc, category)).thenThrow(new RuntimeException("fail"));
        ResponseEntity<?> response = controller.optimizeCourseInfo(title, desc, category);
        assertEquals(200, response.getStatusCodeValue()); // 控制器异常分支返回200但带fail信息
    }
} 