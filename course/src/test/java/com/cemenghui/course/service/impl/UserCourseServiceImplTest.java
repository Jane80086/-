package com.cemenghui.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cemenghui.course.dao.UserCourseDao;
import com.cemenghui.course.entity.UserCourse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserCourseServiceImplTest {
    @InjectMocks
    private UserCourseServiceImpl userCourseServiceImpl;
    @Mock
    private UserCourseDao userCourseDao;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testPurchaseCourse() {
        when(userCourseDao.insert(any(UserCourse.class))).thenReturn(1);
        assertTrue(userCourseServiceImpl.purchaseCourse(1L, 2L));
    }

    @Test
    void testPurchaseCourseFail() {
        when(userCourseDao.insert(any(UserCourse.class))).thenReturn(0);
        assertFalse(userCourseServiceImpl.purchaseCourse(1L, 2L));
    }

    @Test
    void testDeletePurchasedCourse() {
        when(userCourseDao.delete(any(QueryWrapper.class))).thenReturn(1);
        assertTrue(userCourseServiceImpl.deletePurchasedCourse(1L, 2L));
    }

    @Test
    void testDeletePurchasedCourseFail() {
        when(userCourseDao.delete(any(QueryWrapper.class))).thenReturn(0);
        assertFalse(userCourseServiceImpl.deletePurchasedCourse(1L, 2L));
    }

    @Test
    void testGetPurchasedCourses() {
        when(userCourseDao.selectList(any(QueryWrapper.class))).thenReturn(Collections.singletonList(new UserCourse()));
        List<UserCourse> result = userCourseServiceImpl.getPurchasedCourses(1L);
        assertEquals(1, result.size());
    }
} 