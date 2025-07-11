package com.cemenghui.course.service.impl;

import com.cemenghui.course.dao.CourseDao;
import com.cemenghui.course.entity.Course;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CourseServiceImplTest {
    @InjectMocks
    private CourseServiceImpl courseServiceImpl;
    @Mock
    private CourseDao courseDao;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetCourseDetail_Found() {
        Course course = new Course();
        when(courseDao.selectById(1L)).thenReturn(course);
        try {
            Course result = courseServiceImpl.getCourseDetail(1L);
            assertNotNull(result);
        } catch (Exception e) {
            fail("Should not throw exception: " + e.getMessage());
        }
    }

    @Test
    void testGetCourseDetail_NotFound() {
        when(courseDao.selectById(2L)).thenReturn(null);
        assertThrows(com.cemenghui.course.service.NotFoundException.class, () -> {
            courseServiceImpl.getCourseDetail(2L);
        });
    }

    @Test
    void testListCourses() {
        when(courseDao.selectList(any())).thenReturn(java.util.Collections.emptyList());
        assertTrue(courseServiceImpl.listCourses().isEmpty());
    }
} 