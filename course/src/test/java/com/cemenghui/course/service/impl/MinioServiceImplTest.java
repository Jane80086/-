package com.cemenghui.course.service.impl;

import io.minio.MinioClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MinioServiceImplTest {
    @InjectMocks
    private MinioServiceImpl minioService;
    @Mock
    private MinioClient minioClient;
    @Mock
    private MultipartFile multipartFile;
    
    @BeforeEach
    void setUp() { 
        MockitoAnnotations.openMocks(this);
        // 注入测试用的bucketName和endpoint
        ReflectionTestUtils.setField(minioService, "bucketName", "test-bucket");
        ReflectionTestUtils.setField(minioService, "endpoint", "http://localhost:9000");
    }

    @Test
    void uploadCourseCover_shouldReturnUrl() throws IOException {
        when(multipartFile.getOriginalFilename()).thenReturn("cover.jpg");
        when(multipartFile.getContentType()).thenReturn("image/jpeg");
        when(multipartFile.getSize()).thenReturn(1024L);
        when(multipartFile.getInputStream()).thenReturn(new ByteArrayInputStream("test data".getBytes()));
        String url = minioService.uploadCourseCover(multipartFile);
        assertNotNull(url);
        assertTrue(url.contains("course-covers"));
    }
    @Test
    void uploadCourseVideo_shouldReturnUrl() throws IOException {
        when(multipartFile.getOriginalFilename()).thenReturn("video.mp4");
        when(multipartFile.getContentType()).thenReturn("video/mp4");
        when(multipartFile.getSize()).thenReturn(1024L);
        when(multipartFile.getInputStream()).thenReturn(new ByteArrayInputStream("test data".getBytes()));
        String url = minioService.uploadCourseVideo(multipartFile);
        assertNotNull(url);
        assertTrue(url.contains("course-videos"));
    }
    @Test
    void uploadUserAvatar_shouldReturnUrl() throws IOException {
        when(multipartFile.getOriginalFilename()).thenReturn("avatar.png");
        when(multipartFile.getContentType()).thenReturn("image/png");
        when(multipartFile.getSize()).thenReturn(1024L);
        when(multipartFile.getInputStream()).thenReturn(new ByteArrayInputStream("test data".getBytes()));
        String url = minioService.uploadUserAvatar(multipartFile);
        assertNotNull(url);
        assertTrue(url.contains("user-avatars"));
    }
    @Test
    void uploadDocument_shouldReturnUrl() throws IOException {
        when(multipartFile.getOriginalFilename()).thenReturn("doc.pdf");
        when(multipartFile.getContentType()).thenReturn("application/pdf");
        when(multipartFile.getSize()).thenReturn(1024L);
        when(multipartFile.getInputStream()).thenReturn(new ByteArrayInputStream("test data".getBytes()));
        String url = minioService.uploadDocument(multipartFile);
        assertNotNull(url);
        assertTrue(url.contains("documents"));
    }
} 