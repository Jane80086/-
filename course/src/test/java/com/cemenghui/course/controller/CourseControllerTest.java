package com.cemenghui.course.controller;

import com.cemenghui.course.common.Result;
import com.cemenghui.course.entity.Course;
import com.cemenghui.course.service.CourseService;
import com.cemenghui.course.service.NotFoundException;
import com.cemenghui.course.service.impl.CourseManagerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CourseControllerTest {
    @InjectMocks
    private CourseController controller;
    @Mock
    private CourseService courseService;
    @Mock
    private CourseManagerServiceImpl courseManagerService;
    @BeforeEach
    void setUp() { MockitoAnnotations.openMocks(this); }

    @Test
    void getCourseDetail_shouldReturnCourse() throws NotFoundException {
        Course course = new Course();
        course.setId(1L);
        when(courseService.getCourseDetail(1L)).thenReturn(course);
        ResponseEntity<Result> result = controller.getCourseDetail(1L);
        assertEquals(200, result.getBody().getCode());
        assertNotNull(result.getBody().getData());
    }
    @Test
    void getCourseDetail_shouldReturnFail() throws NotFoundException {
        when(courseService.getCourseDetail(2L)).thenThrow(new NotFoundException("Course not found"));
        ResponseEntity<Result> result = controller.getCourseDetail(2L);
        assertEquals(500, result.getStatusCodeValue());
    }
    @Test
    void getCourseList_shouldReturnList() {
        when(courseService.listCourses()).thenReturn(Collections.singletonList(new Course()));
        ResponseEntity<Result> result = controller.getCourseList();
        assertEquals(200, result.getBody().getCode());
        assertNotNull(result.getBody().getData());
    }
    @Test
    void searchCourses_shouldReturnList() {
        when(courseService.searchCourses(anyString())).thenReturn(Collections.singletonList(new Course()));
        ResponseEntity<Result> result = controller.searchCourses("Java");
        assertEquals(200, result.getBody().getCode());
        assertNotNull(result.getBody().getData());
    }
    @Test
    void createCourse_shouldReturnSuccess() {
        Course course = new Course();
        Course createdCourse = new Course();
        createdCourse.setId(1L);
        when(courseManagerService.createCourse(any(Course.class))).thenReturn(createdCourse);
        ResponseEntity<Result> result = controller.createCourse(course, true);
        assertEquals(200, result.getBody().getCode());
    }
    @Test
    void updateCourse_shouldReturnSuccess() throws NotFoundException {
        Course course = new Course();
        Course updatedCourse = new Course();
        updatedCourse.setId(1L);
        when(courseManagerService.editCourse(anyLong(), any(Course.class))).thenReturn(updatedCourse);
        ResponseEntity<Result> result = controller.updateCourse(1L, course);
        assertEquals(200, result.getBody().getCode());
    }
    @Test
    void updateCourse_shouldReturnFail() throws NotFoundException {
        Course course = new Course();
        when(courseManagerService.editCourse(anyLong(), any(Course.class))).thenThrow(new NotFoundException("Course not found"));
        ResponseEntity<Result> result = controller.updateCourse(2L, course);
        assertEquals(404, result.getStatusCodeValue());
    }
    @Test
    void deleteCourse_shouldReturnSuccess() {
        when(courseManagerService.deleteCourse(anyLong())).thenReturn(true);
        ResponseEntity<Result> result = controller.deleteCourse(1L);
        assertEquals(200, result.getBody().getCode());
    }
    @Test
    void deleteCourse_shouldReturnFail() {
        when(courseManagerService.deleteCourse(anyLong())).thenReturn(false);
        ResponseEntity<Result> result = controller.deleteCourse(2L);
        assertEquals(404, result.getStatusCodeValue());
    }
} 