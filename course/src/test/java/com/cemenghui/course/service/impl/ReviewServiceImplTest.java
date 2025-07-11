package com.cemenghui.course.service.impl;

import com.cemenghui.course.dao.CourseDao;
import com.cemenghui.course.dao.ReviewDao;
import com.cemenghui.course.entity.Course;
import com.cemenghui.course.entity.Review;
import com.cemenghui.course.entity.ReviewStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReviewServiceImplTest {
    @InjectMocks
    private ReviewServiceImpl reviewServiceImpl;
    @Mock
    private ReviewDao reviewDao;
    @Mock
    private CourseDao courseDao;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testApproveCourse() {
        Course course = new Course();
        when(courseDao.selectById(1L)).thenReturn(course);
        Review review = reviewServiceImpl.approveCourse(1L, 2L);
        assertEquals(ReviewStatus.APPROVED.name(), review.getStatus());
        verify(reviewDao).insert(any());
        verify(courseDao).updateById(any());
    }

    @Test
    void testRejectCourse() {
        Course course = new Course();
        when(courseDao.selectById(1L)).thenReturn(course);
        Review review = reviewServiceImpl.rejectCourse(1L, 2L, "reason");
        assertEquals(ReviewStatus.REJECTED.name(), review.getStatus());
        assertEquals("reason", review.getComment());
        verify(reviewDao).insert(any());
        verify(courseDao).updateById(any());
    }

    @Test
    void testGetReviewLog() {
        List<Review> reviews = Collections.singletonList(new Review());
        when(reviewDao.selectList(any())).thenReturn(reviews);
        List<Review> result = reviewServiceImpl.getReviewLog(1L);
        assertEquals(1, result.size());
    }
} 