package com.cemenghui.course.controller;

import com.cemenghui.course.common.Result;
import com.cemenghui.course.service.CourseOptimizationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import java.util.Map;
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
    void optimizeTitle_success() {
        when(optimizationService.optimizeTitle(anyString(), any(), any())).thenReturn("优化后标题");
        ResponseEntity<Result> resp = controller.optimizeTitle("原始标题", "分类", "描述");
        assertEquals(200, resp.getStatusCodeValue());
        assertEquals(200, resp.getBody().getCode());
        assertTrue(((Map<?,?>)resp.getBody().getData()).get("optimized_title").toString().contains("优化"));
    }
    @Test
    void optimizeTitle_fail() {
        when(optimizationService.optimizeTitle(anyString(), any(), any())).thenThrow(new RuntimeException("AI异常"));
        ResponseEntity<Result> resp = controller.optimizeTitle("原始标题", null, null);
        assertEquals(200, resp.getStatusCodeValue());
        assertEquals(500, resp.getBody().getCode());
        assertTrue(resp.getBody().getMessage().contains("失败"));
    }
    @Test
    void optimizeDescription_success() {
        when(optimizationService.optimizeDescription(anyString(), any(), any())).thenReturn("优化后简介");
        ResponseEntity<Result> resp = controller.optimizeDescription("原始简介", "分类", "标题");
        assertEquals(200, resp.getStatusCodeValue());
        assertEquals(200, resp.getBody().getCode());
        assertTrue(((Map<?,?>)resp.getBody().getData()).get("optimized_description").toString().contains("优化"));
    }
    @Test
    void optimizeDescription_fail() {
        when(optimizationService.optimizeDescription(anyString(), any(), any())).thenThrow(new RuntimeException("AI异常"));
        ResponseEntity<Result> resp = controller.optimizeDescription("原始简介", null, null);
        assertEquals(200, resp.getStatusCodeValue());
        assertEquals(500, resp.getBody().getCode());
        assertTrue(resp.getBody().getMessage().contains("失败"));
    }
    @Test
    void optimizeCourseInfo_success() {
        when(optimizationService.optimizeCourseInfo(anyString(), anyString(), any())).thenReturn(Map.of("optimized_title", "T", "optimized_description", "D"));
        ResponseEntity<Result> resp = controller.optimizeCourseInfo("T", "D", "C");
        assertEquals(200, resp.getStatusCodeValue());
        assertEquals(200, resp.getBody().getCode());
        assertEquals("T", ((Map<?,?>)resp.getBody().getData()).get("optimized_title"));
    }
    @Test
    void optimizeCourseInfo_fail() {
        when(optimizationService.optimizeCourseInfo(anyString(), anyString(), any())).thenThrow(new RuntimeException("AI异常"));
        ResponseEntity<Result> resp = controller.optimizeCourseInfo("T", "D", null);
        assertEquals(200, resp.getStatusCodeValue());
        assertEquals(500, resp.getBody().getCode());
        assertTrue(resp.getBody().getMessage().contains("失败"));
    }
} 