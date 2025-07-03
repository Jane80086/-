package com.cemenghui.course.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class FileControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testUploadCourseCover() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "cover.jpg", MediaType.IMAGE_JPEG_VALUE, "test image".getBytes());
        mockMvc.perform(multipart("/api/file/upload/course-cover").file(file))
                .andExpect(status().isOk());
    }

    @Test
    public void testUploadCourseVideo() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "video.mp4", MediaType.APPLICATION_OCTET_STREAM_VALUE, "test video".getBytes());
        mockMvc.perform(multipart("/api/file/upload/course-video").file(file))
                .andExpect(status().isOk());
    }

    @Test
    public void testUploadUserAvatar() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "avatar.png", MediaType.IMAGE_PNG_VALUE, "test avatar".getBytes());
        mockMvc.perform(multipart("/api/file/upload/user-avatar").file(file))
                .andExpect(status().isOk());
    }

    @Test
    public void testUploadDocument() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "doc.pdf", MediaType.APPLICATION_PDF_VALUE, "test doc".getBytes());
        mockMvc.perform(multipart("/api/file/upload/document").file(file))
                .andExpect(status().isOk());
    }

    @Test
    public void testUploadFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", MediaType.TEXT_PLAIN_VALUE, "test file".getBytes());
        mockMvc.perform(multipart("/api/file/upload").file(file).param("folder", "test"))
                .andExpect(status().isOk());
    }

    @Test
    public void testListFiles() throws Exception {
        mockMvc.perform(get("/api/file/list").param("folder", "test"))
                .andExpect(status().isOk());
    }

    @Test
    public void testFileExists() throws Exception {
        mockMvc.perform(get("/api/file/exists").param("objectName", "test.txt"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetPresignedUrl() throws Exception {
        mockMvc.perform(get("/api/file/presigned-url").param("objectName", "test.txt").param("expirySeconds", "3600"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetMinioStatus() throws Exception {
        mockMvc.perform(get("/api/file/status"))
                .andExpect(status().isOk());
    }
} 