package com.cemenghui.meeting.java.com.example.demo.service;

import com.cemenghui.meeting.bean.ApiResponse;
import com.cemenghui.meeting.service.MinioService;

import io.minio.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MinioService 单元测试")
public class MinioServiceTest {

    @Mock
    private MinioClient minioClient;

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private MinioService minioService;

    @BeforeEach
    void setUp() {
        // 注入mock的minioClient和配置
        ReflectionTestUtils.setField(minioService, "minioClient", minioClient);
        ReflectionTestUtils.setField(minioService, "bucket", "test-bucket");
        ReflectionTestUtils.setField(minioService, "presignedUrlExpiration", 3600);
    }

    @Test
    @DisplayName("测试文件上传成功")
    public void testUploadFile_Success() throws Exception {
        when(multipartFile.getOriginalFilename()).thenReturn("test.txt");
        when(multipartFile.getInputStream()).thenReturn(new ByteArrayInputStream("hello".getBytes()));
        when(multipartFile.getSize()).thenReturn(5L);
        when(multipartFile.getContentType()).thenReturn("text/plain");

        // putObject无异常
        doAnswer(invocation -> null).when(minioClient).putObject(any(PutObjectArgs.class));

        ApiResponse<String> resp = minioService.uploadFile(multipartFile);
        assertEquals(200, resp.getCode());
        assertNotNull(resp.getData());
        assertTrue(resp.getData().endsWith("test.txt"));
    }

    @Test
    @DisplayName("测试文件上传失败")
    public void testUploadFile_Fail() throws Exception {
        when(multipartFile.getOriginalFilename()).thenReturn("test.txt");
        when(multipartFile.getInputStream()).thenThrow(new RuntimeException("IO error"));

        ApiResponse<String> resp = minioService.uploadFile(multipartFile);
        assertNotEquals(200, resp.getCode());
        assertEquals(500, resp.getCode());
        assertTrue(resp.getMessage().contains("文件上传失败"));
    }

    @Test
    @DisplayName("测试获取预签名URL成功")
    public void testGetPresignedUrl_Success() throws Exception {
        when(minioClient.getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class))).thenReturn("http://minio/test-url");
        ApiResponse<String> resp = minioService.getPresignedUrl("file.txt");
        assertEquals(200, resp.getCode());
        assertEquals("http://minio/test-url", resp.getData());
    }

    @Test
    @DisplayName("测试获取预签名URL失败")
    public void testGetPresignedUrl_Fail() throws Exception {
        when(minioClient.getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class))).thenThrow(new RuntimeException("minio error"));
        ApiResponse<String> resp = minioService.getPresignedUrl("file.txt");
        assertNotEquals(200, resp.getCode());
        assertEquals(500, resp.getCode());
        assertTrue(resp.getMessage().contains("生成预签名URL失败"));
    }

    @Test
    @DisplayName("测试批量获取预签名URL成功")
    public void testGetBatchPresignedUrls_Success() throws Exception {
        when(minioClient.getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class))).thenReturn("http://minio/url");
        List<String> files = Arrays.asList("a.txt", "b.txt");
        ApiResponse<Map<String, String>> resp = minioService.getBatchPresignedUrls(files);
        assertEquals(200, resp.getCode());
        assertEquals(2, resp.getData().size());
        assertEquals("http://minio/url", resp.getData().get("a.txt"));
    }

    @Test
    @DisplayName("测试批量获取预签名URL失败")
    public void testGetBatchPresignedUrls_Fail() throws Exception {
        when(minioClient.getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class))).thenThrow(new RuntimeException("minio error"));
        List<String> files = Arrays.asList("a.txt", "b.txt");
        ApiResponse<Map<String, String>> resp = minioService.getBatchPresignedUrls(files);
        assertNotEquals(200, resp.getCode());
        assertEquals(500, resp.getCode());
        assertTrue(resp.getMessage().contains("批量生成预签名URL失败"));
    }

    @Test
    @DisplayName("测试删除文件成功")
    public void testDeleteFile_Success() throws Exception {
        doNothing().when(minioClient).removeObject(any(RemoveObjectArgs.class));
        ApiResponse<Boolean> resp = minioService.deleteFile("file.txt");
        assertEquals(200, resp.getCode());
        assertTrue(resp.getData());
    }

    @Test
    @DisplayName("测试删除文件失败")
    public void testDeleteFile_Fail() throws Exception {
        doThrow(new RuntimeException("delete error")).when(minioClient).removeObject(any(RemoveObjectArgs.class));
        ApiResponse<Boolean> resp = minioService.deleteFile("file.txt");
        assertNotEquals(200, resp.getCode());
        assertEquals(500, resp.getCode());
        assertTrue(resp.getMessage().contains("文件删除失败"));
    }

    @Test
    @DisplayName("测试文件存在")
    public void testFileExists_True() throws Exception {
        when(minioClient.statObject(any(StatObjectArgs.class))).thenReturn(mock(StatObjectResponse.class));
        assertTrue(minioService.fileExists("file.txt"));
    }

    @Test
    @DisplayName("测试文件不存在")
    public void testFileExists_False() throws Exception {
        doThrow(new RuntimeException("not found")).when(minioClient).statObject(any(StatObjectArgs.class));
        assertFalse(minioService.fileExists("file.txt"));
    }
} 