package com.cemenghui.course.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CourseOptimizationServiceImplTest {
    @InjectMocks
    private CourseOptimizationServiceImpl optimizationService;
    @Mock
    private RestTemplate restTemplate;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    void optimizeTitle_shouldReturnOriginalTitleIfException() {
        String title = "Java入门";
        String result = optimizationService.optimizeTitle(title, null, null);
        assertEquals(title, result);
    }
    @Test
    void optimizeDescription_shouldReturnOriginalDescriptionIfException() {
        String desc = "本课程介绍Java基础。";
        String result = optimizationService.optimizeDescription(desc, null, null);
        assertEquals(desc, result);
    }
    @Test
    void optimizeCourseInfo_shouldReturnOriginalIfException() {
        String title = "Java入门";
        String desc = "本课程介绍Java基础。";
        Map<String, String> result = optimizationService.optimizeCourseInfo(title, desc, null);
        assertEquals(title, result.get("optimized_title"));
        assertEquals(desc, result.get("optimized_description"));
    }
} 