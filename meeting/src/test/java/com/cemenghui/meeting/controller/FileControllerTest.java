package com.cemenghui.meeting.controller;

import com.cemenghui.meeting.bean.ApiResponse;
import com.cemenghui.meeting.service.MinioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FileController 单元测试")
public class FileControllerTest {

    @Mock
    private MinioService minioService;

    @InjectMocks
    private FileController fileController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(fileController).build();
    }

    @Test
    @DisplayName("获取单个文件预签名URL-成功")
    public void testGetPresignedUrl_Success() throws Exception {
        when(minioService.getPresignedUrl("file.txt")).thenReturn(ApiResponse.success("http://url"));
        mockMvc.perform(get("/api/file/presigned-url/file.txt"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value("http://url"));
    }

    @Test
    @DisplayName("批量获取文件预签名URL-成功")
    public void testGetBatchPresignedUrls_Success() throws Exception {
        Map<String, String> urlMap = new HashMap<>();
        urlMap.put("a.txt", "urlA");
        urlMap.put("b.txt", "urlB");
        when(minioService.getBatchPresignedUrls(any())).thenReturn(ApiResponse.success(urlMap));
        String json = "[\"a.txt\",\"b.txt\"]";
        mockMvc.perform(post("/api/file/presigned-urls")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data['a.txt']").value("urlA"));
    }

    @Test
    @DisplayName("删除文件-成功")
    public void testDeleteFile_Success() throws Exception {
        when(minioService.deleteFile("file.txt")).thenReturn(ApiResponse.success(true));
        mockMvc.perform(delete("/api/file/file.txt"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    @DisplayName("检查文件存在-存在")
    public void testCheckFileExists_True() throws Exception {
        when(minioService.fileExists("file.txt")).thenReturn(true);
        mockMvc.perform(get("/api/file/exists/file.txt"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    @DisplayName("检查文件存在-不存在")
    public void testCheckFileExists_False() throws Exception {
        when(minioService.fileExists("file.txt")).thenReturn(false);
        mockMvc.perform(get("/api/file/exists/file.txt"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(false));
    }
} 