package com.cemenghui.news.service.impl;

import org.apache.commons.io.FilenameUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileUploadServiceImplTest {

    private FileUploadServiceImpl fileUploadService;
    private final String testUploadPath = "target/test-uploads";
    private final String[] allowedTypes = {"jpg", "jpeg", "png", "gif", "webp"};

    @BeforeEach
    void setUp() throws Exception {
        fileUploadService = new FileUploadServiceImpl();

        // 使用反射设置私有字段 uploadPath
        setPrivateField(fileUploadService, "uploadPath", testUploadPath);

        // 确保测试目录存在且为空
        cleanTestDirectory();
        Files.createDirectories(Paths.get(testUploadPath));
    }

    @AfterEach
    void tearDown() throws IOException {
        // 清理测试目录
        cleanTestDirectory();
    }

    private void cleanTestDirectory() throws IOException {
        Path path = Paths.get(testUploadPath);
        if (Files.exists(path)) {
            FileSystemUtils.deleteRecursively(path);
        }
    }

    private void setPrivateField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @Test
    void uploadImage_shouldSuccess_whenValidImage() throws IOException {
        // 准备测试数据
        String originalFilename = "test.jpg";
        String content = "test content";
        MultipartFile file = new MockMultipartFile("file", originalFilename, "image/jpeg", content.getBytes());

        // 执行测试
        String result = fileUploadService.uploadImage(file);

        // 验证结果
        assertNotNull(result);
        assertTrue(result.startsWith("/uploads/images/"));
        assertTrue(result.endsWith(".jpg"));

        // 验证文件是否已创建
        String fileName = result.substring(result.lastIndexOf('/') + 1);
        File uploadedFile = new File(testUploadPath, fileName);
        assertTrue(uploadedFile.exists());
        assertEquals(content.length(), uploadedFile.length());
    }

    @Test
    void uploadImage_shouldThrowException_whenFileIsNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> fileUploadService.uploadImage(null));

        assertEquals("文件不能为空", exception.getMessage());
    }

    @Test
    void uploadImage_shouldThrowException_whenFileIsEmpty() {
        MultipartFile file = new MockMultipartFile("empty", new byte[0]);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> fileUploadService.uploadImage(file));

        assertEquals("文件不能为空", exception.getMessage());
    }

    @Test
    void uploadImage_shouldThrowException_whenFileTypeNotAllowed() {
        MultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "content".getBytes());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> fileUploadService.uploadImage(file));

        assertEquals("不支持的图片格式或文件过大", exception.getMessage());
    }

    @Test
    void uploadImage_shouldThrowException_whenFileTooLarge() {
        byte[] largeContent = new byte[5 * 1024 * 1024 + 1]; // 5MB + 1 byte
        MultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", largeContent);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> fileUploadService.uploadImage(file));

        assertEquals("不支持的图片格式或文件过大", exception.getMessage());
    }

    @Test
    void deleteImage_shouldReturnTrue_whenFileExists() throws IOException {
        String originalFilename = "test.jpg";
        MultipartFile file = new MockMultipartFile("file", originalFilename, "image/jpeg", "content".getBytes());
        String imagePath = fileUploadService.uploadImage(file);
        String fileName = imagePath.substring(imagePath.lastIndexOf('/') + 1);

        boolean result = fileUploadService.deleteImage(fileName);

        assertTrue(result);
        assertFalse(new File(testUploadPath, fileName).exists());
    }

    @Test
    void deleteImage_shouldReturnFalse_whenFileNotExists() {
        boolean result = fileUploadService.deleteImage("non-existent-file.jpg");

        assertFalse(result);
    }

    @Test
    void deleteImage_shouldReturnFalse_whenPathIsEmpty() {
        boolean result = fileUploadService.deleteImage("");

        assertFalse(result);
    }

    @Test
    void deleteImage_shouldReturnFalse_whenPathIsNull() {
        boolean result = fileUploadService.deleteImage(null);

        assertFalse(result);
    }

    @Test
    void validateImageFile_shouldReturnTrue_whenValidImage() {
        for (String ext : allowedTypes) {
            MultipartFile file = new MockMultipartFile("file", "test." + ext, "image/" + ext, "content".getBytes());

            boolean result = fileUploadService.validateImageFile(file);

            assertTrue(result, "Should allow " + ext + " files");
        }
    }

    @Test
    void validateImageFile_shouldReturnFalse_whenFileTooLarge() {
        byte[] largeContent = new byte[5 * 1024 * 1024 + 1];
        MultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", largeContent);

        boolean result = fileUploadService.validateImageFile(file);

        assertFalse(result);
    }

    @Test
    void validateImageFile_shouldReturnFalse_whenInvalidExtension() {
        MultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "content".getBytes());

        boolean result = fileUploadService.validateImageFile(file);

        assertFalse(result);
    }

    @Test
    void validateImageFile_shouldReturnFalse_whenNoExtension() {
        MultipartFile file = new MockMultipartFile("file", "noextension", "application/octet-stream", "content".getBytes());

        boolean result = fileUploadService.validateImageFile(file);

        assertFalse(result);
    }

    @Test
    void getImageUrl_shouldReturnCorrectUrl() {
        String fileName = "test.jpg";

        String result = fileUploadService.getImageUrl(fileName);

        assertEquals("/uploads/images/" + fileName, result);
    }

    @Test
    void getImageUrl_shouldHandleEmptyPath() {
        String result = fileUploadService.getImageUrl("");

        assertEquals("/uploads/images/", result);
    }

    @Test
    void getImageUrl_shouldHandleNullPath() {
        String result = fileUploadService.getImageUrl(null);

        assertEquals("/uploads/images/null", result);
    }
}