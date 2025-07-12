package com.cemenghui.course.service.impl;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.GetObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.http.Method;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.lang.reflect.Field;
import io.minio.GetObjectResponse;
import io.minio.ObjectWriteResponse;

class MinioServiceImplTest {
    @InjectMocks
    private MinioServiceImpl minioServiceImpl;
    @Mock
    private MinioClient minioClient;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        Field bucketField = MinioServiceImpl.class.getDeclaredField("bucketName");
        bucketField.setAccessible(true);
        bucketField.set(minioServiceImpl, "test-bucket");
        Field endpointField = MinioServiceImpl.class.getDeclaredField("endpoint");
        endpointField.setAccessible(true);
        endpointField.set(minioServiceImpl, "http://localhost:9000");
    }

    @Test
    void testUploadFile_Success() throws Exception {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("test.txt");
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream("abc".getBytes()));
        when(file.getSize()).thenReturn(3L);
        when(file.getContentType()).thenReturn("text/plain");
        when(minioClient.putObject(any(PutObjectArgs.class))).thenReturn(mock(ObjectWriteResponse.class));
        MinioServiceImpl spy = spy(minioServiceImpl);
        doReturn("url").when(spy).getFileUrl(anyString());
        String url = spy.uploadFile(file, "folder");
        assertEquals("url", url);
    }

    @Test
    void testUploadFile_Exception() throws Exception {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("test.txt");
        when(file.getInputStream()).thenThrow(new RuntimeException("fail"));
        assertThrows(RuntimeException.class, () -> minioServiceImpl.uploadFile(file, "folder"));
    }

    @Test
    void testDownloadFile_Success() throws Exception {
        GetObjectResponse response = mock(GetObjectResponse.class);
        when(minioClient.getObject(any(GetObjectArgs.class))).thenReturn(response);
        InputStream result = minioServiceImpl.downloadFile("obj");
        assertNotNull(result);
    }

    @Test
    void testDownloadFile_Exception() throws Exception {
        when(minioClient.getObject(any(GetObjectArgs.class))).thenThrow(new RuntimeException("fail"));
        assertThrows(RuntimeException.class, () -> minioServiceImpl.downloadFile("obj"));
    }

    @Test
    void testGetPresignedUrl_Success() throws Exception {
        when(minioClient.getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class))).thenReturn("url");
        String url = minioServiceImpl.getPresignedUrl("obj", 60);
        assertEquals("url", url);
    }

    @Test
    void testGetPresignedUrl_Exception() throws Exception {
        when(minioClient.getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class))).thenThrow(new RuntimeException("fail"));
        assertThrows(RuntimeException.class, () -> minioServiceImpl.getPresignedUrl("obj", 60));
    }
} 