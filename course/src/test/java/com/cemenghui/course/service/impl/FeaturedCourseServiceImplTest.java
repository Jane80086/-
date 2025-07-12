package com.cemenghui.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cemenghui.course.dao.FeaturedCourseDao;
import com.cemenghui.course.entity.FeaturedCourse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FeaturedCourseServiceImplTest {
    @InjectMocks
    private FeaturedCourseServiceImpl featuredCourseServiceImpl;
    @Mock
    private FeaturedCourseDao featuredCourseDao;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testPromoteToFeaturedSuccess() {
        FeaturedCourse fc = new FeaturedCourse();
        when(featuredCourseDao.selectById(1L)).thenReturn(fc);
        when(featuredCourseDao.insert(any(FeaturedCourse.class))).thenReturn(1);
        boolean result = featuredCourseServiceImpl.promoteToFeatured(1L, 10);
        assertTrue(result);
    }

    @Test
    void testPromoteToFeaturedNotFound() {
        when(featuredCourseDao.selectById(1L)).thenReturn(null);
        boolean result = featuredCourseServiceImpl.promoteToFeatured(1L, 10);
        assertFalse(result);
    }

    @Test
    void testRemoveFromFeaturedSuccess() {
        FeaturedCourse fc = new FeaturedCourse();
        when(featuredCourseDao.selectById(1L)).thenReturn(fc);
        when(featuredCourseDao.delete(any(LambdaQueryWrapper.class))).thenReturn(1);
        boolean result = featuredCourseServiceImpl.removeFromFeatured(1L);
        assertTrue(result);
    }

    @Test
    void testRemoveFromFeaturedNotFound() {
        when(featuredCourseDao.selectById(1L)).thenReturn(null);
        boolean result = featuredCourseServiceImpl.removeFromFeatured(1L);
        assertFalse(result);
    }

    @Test
    void testListFeaturedCourses() {
        when(featuredCourseDao.selectList(any(LambdaQueryWrapper.class))).thenReturn(Collections.singletonList(new FeaturedCourse()));
        List<FeaturedCourse> result = featuredCourseServiceImpl.listFeaturedCourses();
        assertEquals(1, result.size());
    }
} 