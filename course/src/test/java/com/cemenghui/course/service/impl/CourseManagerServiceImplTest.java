package com.cemenghui.course.service.impl;

import com.cemenghui.course.dao.CourseDao;
import com.cemenghui.course.entity.Course;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CourseManagerServiceImplTest {
    @InjectMocks
    private CourseManagerServiceImpl courseManagerServiceImpl;
    @Mock
    private CourseDao courseDao;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateCourse() {
        Course course = new Course();
        course.setId(1L);
        course.setTitle("测试课程"); // 避免标题为空
        when(courseDao.insert(any())).thenAnswer(invocation -> {
            // 模拟插入后主键赋值
            Course arg = invocation.getArgument(0);
            arg.setId(1L);
            return 1;
        });
        when(courseDao.selectById(1L)).thenReturn(course);
        Course result = courseManagerServiceImpl.createCourse(course);
        assertNotNull(result);
        assertEquals("测试课程", result.getTitle());
    }

    @Test
    void testEditCourse() throws com.cemenghui.course.service.NotFoundException {
        Course oldCourse = new Course();
        oldCourse.setId(1L);
        oldCourse.setTitle("原课程");
        Course updated = new Course();
        updated.setTitle("新标题");
        updated.setDescription("新描述");
        updated.setCoverImage("img.png");
        when(courseDao.selectById(1L)).thenReturn(oldCourse);
        when(courseDao.updateById(any())).thenReturn(1);
        Course result = courseManagerServiceImpl.editCourse(1L, updated);
        assertNotNull(result);
        assertEquals("新标题", result.getTitle());
        assertEquals("新描述", result.getDescription());
        assertEquals("img.png", result.getCoverImage());
    }

    @Test
    void testDeleteCourse() {
        Course course = new Course();
        course.setId(1L);
        course.setTitle("测试课程");
        when(courseDao.selectById(1L)).thenReturn(course);
        when(courseDao.deleteById(1L)).thenReturn(1);
        boolean result = courseManagerServiceImpl.deleteCourse(1L);
        assertTrue(result);
    }
} 