package com.cemenghui.meeting.controller;

import com.cemenghui.meeting.bean.ApiResponse;
import com.cemenghui.meeting.service.MinioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/file")
@Tag(name = "文件管理", description = "文件上传、下载和访问相关接口")
@Slf4j
public class FileController {

    @Autowired
    private MinioService minioService;

    @GetMapping("/presigned-url/{objectName}")
    @Operation(summary = "获取文件预签名URL", description = "获取私有文件的预签名URL用于访问")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse<String> getPresignedUrl(
            @Parameter(description = "文件对象名称") @PathVariable String objectName) {
        log.info("获取文件预签名URL: {}", objectName);
        return minioService.getPresignedUrl(objectName);
    }

    @PostMapping("/presigned-urls")
    @Operation(summary = "批量获取文件预签名URL", description = "批量获取多个文件的预签名URL")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse<Map<String, String>> getBatchPresignedUrls(
            @Parameter(description = "文件对象名称列表") @RequestBody List<String> objectNames) {
        log.info("批量获取文件预签名URL，文件数量: {}", objectNames.size());
        return minioService.getBatchPresignedUrls(objectNames);
    }

    @DeleteMapping("/{objectName}")
    @Operation(summary = "删除文件", description = "删除指定的文件")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Boolean> deleteFile(
            @Parameter(description = "文件对象名称") @PathVariable String objectName) {
        log.info("删除文件: {}", objectName);
        return minioService.deleteFile(objectName);
    }

    @GetMapping("/exists/{objectName}")
    @Operation(summary = "检查文件是否存在", description = "检查指定文件是否存在于存储中")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse<Boolean> checkFileExists(
            @Parameter(description = "文件对象名称") @PathVariable String objectName) {
        log.info("检查文件是否存在: {}", objectName);
        boolean exists = minioService.fileExists(objectName);
        return ApiResponse.success(exists);
    }
} 