package com.cemenghui.course.controller;

import com.cemenghui.course.common.Result;
import com.cemenghui.course.entity.Course;
import com.cemenghui.course.service.impl.UserServiceImpl;
import com.cemenghui.course.service.CourseService;
import com.cemenghui.course.service.ReviewService;
import com.cemenghui.entity.User; // main-app引用
import com.cemenghui.course.entity.Review; // Added import for Review
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminControllerTest {
    @InjectMocks
    private AdminController controller;
    @Mock
    private UserServiceImpl userService;
    @Mock
    private CourseService courseService;
    @Mock
    private ReviewService reviewService;
    private AutoCloseable closeable;
    @BeforeEach
    void setUp() { closeable = MockitoAnnotations.openMocks(this); }
    @AfterEach
    void tearDown() throws Exception { closeable.close(); }

    @Test
    void getUsers_shouldReturnPaged() {
        when(userService.getUsersByPage(anyInt(), anyInt())).thenReturn(Collections.singletonList(new User()));
        when(userService.count()).thenReturn(1L);
        Result result = controller.getUsers(0, 10);
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
    }
    @Test
    void getAllUsers_shouldReturnList() {
        when(userService.getAllUsers()).thenReturn(Collections.singletonList(new User()));
        Result result = controller.getAllUsers();
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
    }
    @Test
    void getUsersByType_shouldReturnList() {
        when(userService.findByUserType(anyString())).thenReturn(Collections.singletonList(new User()));
        Result result = controller.getUsersByType("ADMIN");
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
    }
    @Test
    void getUserById_shouldReturnUser() {
        User user = new User();
        user.setId(1L);
        when(userService.findById(1L)).thenReturn(user);
        Result result = controller.getUserById(1L);
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
    }
    @Test
    void getUserById_shouldReturnFail() {
        when(userService.findById(2L)).thenReturn(null);
        Result result = controller.getUserById(2L);
        assertEquals(500, result.getCode());
    }
    @Test
    void banUser_shouldReturnSuccess() {
        Result result = controller.banUser(1L);
        assertEquals(200, result.getCode());
    }
    @Test
    void unbanUser_shouldReturnSuccess() {
        Result result = controller.unbanUser(1L);
        assertEquals(200, result.getCode());
    }
    @Test
    void getCourses_shouldReturnPaged() {
        when(courseService.listCourses()).thenReturn(Collections.singletonList(new Course()));
        Result result = controller.getCourses(1, 12);
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
    }
    @Test
    void getPendingCourses_shouldReturnList() {
        when(courseService.getPendingCourses()).thenReturn(Collections.singletonList(new Course()));
        Result result = controller.getPendingCourses();
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
    }
    @Test
    void reviewCourse_approve() {
        Review review = new Review();
        when(reviewService.approveCourse(anyLong(), any())).thenReturn(review);
        Result result = controller.reviewCourse(1L, "approved", null);
        assertEquals(200, result.getCode());
    }
    @Test
    void reviewCourse_reject() {
        Review review = new Review();
        when(reviewService.rejectCourse(anyLong(), anyString())).thenReturn(review);
        Result result = controller.reviewCourse(1L, "rejected", "不合格");
        assertEquals(200, result.getCode());
    }
    @Test
    void reviewCourse_unknownStatus() {
        Result result = controller.reviewCourse(1L, "other", null);
        assertEquals(500, result.getCode());
    }
} 