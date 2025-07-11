package com.cemenghui.course.service.impl;

import com.cemenghui.course.entity.Course;
import com.cemenghui.course.service.CourseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class CourseServiceImplTest {
    
    @Mock
    private CourseService courseService;
    
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Test
    public void testGetCourseDetail_Success() throws Exception {
        // Given
        Course course = new Course();
        course.setId(1L);
        course.setTitle("测试课程");
        course.setDescription("这是一个测试课程");
        
        when(courseService.getCourseDetail(1L)).thenReturn(course);
        
        // When
        Course result = courseService.getCourseDetail(1L);
        
        // Then
        assertNotNull(result);
        assertEquals("测试课程", result.getTitle());
        assertEquals("这是一个测试课程", result.getDescription());
        verify(courseService, times(1)).getCourseDetail(1L);
    }
    
    @Test
    public void testGetCourseDetail_NotFound() throws Exception {
        // Given
        when(courseService.getCourseDetail(999L)).thenReturn(null);
        
        // When
        Course result = courseService.getCourseDetail(999L);
        
        // Then
        assertNull(result);
        verify(courseService, times(1)).getCourseDetail(999L);
    }
    
    @Test
    public void testGetCourseDetail_ThrowsException() throws Exception {
        // Given
        when(courseService.getCourseDetail(1L)).thenThrow(new RuntimeException("数据库连接失败"));
        
        // When & Then
        assertThrows(RuntimeException.class, () -> {
            courseService.getCourseDetail(1L);
        });
        
        verify(courseService, times(1)).getCourseDetail(1L);
    }
} 