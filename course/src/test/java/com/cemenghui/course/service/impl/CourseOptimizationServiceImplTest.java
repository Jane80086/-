package com.cemenghui.course.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import static org.junit.jupiter.api.Assertions.*;

class CourseOptimizationServiceImplTest {
    @InjectMocks
    private CourseOptimizationServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testOptimizeCourseInfo() {
        var result = service.optimizeCourseInfo("title", "desc", "cat");
        assertNotNull(result);
    }
} 