package com.cemenghui.course.service.impl;

import com.cemenghui.course.dao.CourseDao;
import com.cemenghui.course.entity.Course;
import com.cemenghui.course.service.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CourseManagerServiceImplTest {
    @InjectMocks
    private CourseManagerServiceImpl courseManagerService;
    @Mock
    private CourseDao courseDao;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    void createCourse_shouldInsertAndReturnCourse() {
        Course course = new Course();
        course.setId(1L);
        when(courseDao.insert(course)).thenReturn(1);
        when(courseDao.selectById(1L)).thenReturn(course);
        Course result = courseManagerService.createCourse(course);
        assertEquals(1L, result.getId());
        verify(courseDao).insert(course);
    }
    @Test
    void editCourse_shouldReturnCourse() throws NotFoundException {
        Long id = 1L;
        Course course = new Course();
        course.setId(id);
        Course updatedCourse = new Course();
        updatedCourse.setTitle("Updated Title");
        when(courseDao.selectById(id)).thenReturn(course);
        when(courseDao.updateById(any(Course.class))).thenReturn(1);
        Course result = courseManagerService.editCourse(id, updatedCourse);
        assertEquals(id, result.getId());
        verify(courseDao).selectById(id);
    }
    @Test
    void deleteCourse_shouldDelete() {
        Long id = 1L;
        Course course = new Course();
        course.setId(id);
        when(courseDao.selectById(id)).thenReturn(course);
        when(courseDao.deleteById(id)).thenReturn(1);
        boolean result = courseManagerService.deleteCourse(id);
        assertTrue(result);
        verify(courseDao).deleteById(id);
    }
} 