package com.cemenghui.course.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import static org.junit.jupiter.api.Assertions.*;
import com.cemenghui.course.common.Result;

class TestControllerTest {
    @InjectMocks
    private TestController testController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testHello() {
        Result result = testController.hello();
        assertEquals("测试成功", result.getMessage());
        assertNotNull(result.getData());
    }
} 