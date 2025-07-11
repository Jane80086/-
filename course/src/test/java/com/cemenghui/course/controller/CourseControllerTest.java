package com.cemenghui.course.controller;

import com.cemenghui.course.common.Result;
import com.cemenghui.course.entity.Course;
import com.cemenghui.course.service.CourseService;
import com.cemenghui.course.service.CourseHistoryService;
import com.cemenghui.course.service.CourseOptimizationService;
import com.cemenghui.course.service.impl.CourseManagerServiceImpl;
import com.cemenghui.course.service.impl.MinioServiceImpl;
import com.cemenghui.course.service.ChapterService;
import com.cemenghui.common.JWTUtil;
import com.cemenghui.course.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CourseControllerTest {
    @InjectMocks
    private CourseController courseController;
    @Mock
    private CourseService courseService;
    @Mock
    private CourseManagerServiceImpl courseManagerService;
    @Mock
    private CourseHistoryService courseHistoryService;
    @Mock
    private CourseOptimizationService courseOptimizationService;
    @Mock
    private MinioServiceImpl minioServiceImpl;
    @Mock
    private ChapterService chapterService;
    @Mock
    private JWTUtil jwtUtil;
    @Mock
    private ReviewService reviewService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateCourseWithVideo_Success() throws Exception {
        Course course = new Course();
        MultipartFile videoFile = mock(MultipartFile.class);
        when(videoFile.isEmpty()).thenReturn(false);
        when(minioServiceImpl.uploadCourseVideo(videoFile)).thenReturn("videoUrl");
        when(courseManagerService.createCourse(any())).thenReturn(course);
        ResponseEntity<Result> response = courseController.createCourseWithVideo(course, videoFile);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("课程创建成功", response.getBody().getMessage());
    }

    @Test
    void testCreateCourseWithVideo_Exception() throws Exception {
        Course course = new Course();
        MultipartFile videoFile = mock(MultipartFile.class);
        when(videoFile.isEmpty()).thenReturn(false);
        when(minioServiceImpl.uploadCourseVideo(videoFile)).thenThrow(new RuntimeException("fail"));
        ResponseEntity<Result> response = courseController.createCourseWithVideo(course, videoFile);
        assertEquals(500, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMessage().contains("课程创建失败"));
    }

    @Test
    void testGetCourseDetail_NotFound() throws com.cemenghui.course.service.NotFoundException {
        when(courseService.getCourseDetail(1L)).thenReturn(null);
        ResponseEntity<Result> response = courseController.getCourseDetail(1L);
        assertEquals(404, response.getStatusCodeValue());
        assertEquals("课程不存在", response.getBody().getMessage());
    }

    @Test
    void testGetCourseDetail_Success() throws com.cemenghui.course.service.NotFoundException {
        Course course = new Course();
        when(courseService.getCourseDetail(1L)).thenReturn(course);
        ResponseEntity<Result> response = courseController.getCourseDetail(1L);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("操作成功", response.getBody().getMessage());
    }

    @Test
    void testUpdateCourse_Success() throws Exception {
        Course updated = new Course();
        when(courseManagerService.editCourse(eq(1L), any())).thenReturn(updated);
        ResponseEntity<Result> response = courseController.updateCourse(1L, updated);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("课程更新成功", response.getBody().getMessage());
    }

    @Test
    void testDeleteCourse_Success() {
        when(courseManagerService.deleteCourse(1L)).thenReturn(true);
        ResponseEntity<Result> response = courseController.deleteCourse(1L);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("课程删除成功", response.getBody().getMessage());
    }

    @Test
    void testDeleteCourse_NotFound() {
        when(courseManagerService.deleteCourse(1L)).thenReturn(false);
        ResponseEntity<Result> response = courseController.deleteCourse(1L);
        assertEquals(404, response.getStatusCodeValue());
        assertTrue(response.getBody().getMessage().contains("课程不存在"));
    }

    @Test
    void testGetCourseList_Success() {
        Course c = new Course();
        c.setStatus("PUBLISHED");
        when(courseService.listCourses()).thenReturn(List.of(c));
        ResponseEntity<Result> response = courseController.getCourseList();
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("获取成功", response.getBody().getMessage());
    }
} 