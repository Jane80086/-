package com.cemenghui.course.service.impl;

import com.cemenghui.course.dao.CourseDao;
import com.cemenghui.course.dao.ReviewDao;
import com.cemenghui.course.entity.Course;
import com.cemenghui.course.entity.Review;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReviewServiceImplTest {
    @InjectMocks
    private ReviewServiceImpl reviewService;
    @Mock
    private ReviewDao reviewDao;
    @Mock
    private CourseDao courseDao;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    void approveCourse_shouldUpdateStatusAndInsertReview() {
        Long courseId = 1L;
        Long reviewerId = 2L;
        Course course = new Course();
        course.setId(courseId);
        when(courseDao.selectById(courseId)).thenReturn(course);
        Review review = reviewService.approveCourse(courseId, reviewerId);
        assertEquals(courseId, review.getCourseId());
        assertEquals("APPROVED", review.getStatus());
        verify(reviewDao).insert(any(Review.class));
        verify(courseDao).updateById(any(Course.class));
    }
    @Test
    void rejectCourse_shouldUpdateStatusAndInsertReview() {
        Long courseId = 1L;
        String reason = "不合格";
        Course course = new Course();
        course.setId(courseId);
        when(courseDao.selectById(courseId)).thenReturn(course);
        Review review = reviewService.rejectCourse(courseId, reason);
        assertEquals(courseId, review.getCourseId());
        assertEquals("REJECTED", review.getStatus());
        assertEquals(reason, review.getComment());
        verify(reviewDao).insert(any(Review.class));
        verify(courseDao).updateById(any(Course.class));
    }
    @Test
    void getReviewLog_shouldReturnList() {
        Long courseId = 1L;
        List<Review> reviews = Collections.singletonList(new Review());
        when(reviewDao.selectList(any())).thenReturn(reviews);
        List<Review> result = reviewService.getReviewLog(courseId);
        assertEquals(1, result.size());
        verify(reviewDao).selectList(any());
    }
} 