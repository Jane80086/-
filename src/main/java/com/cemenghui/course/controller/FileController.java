package com.cemenghui.course.controller;

import com.cemenghui.course.common.Result;
import com.cemenghui.course.service.MinioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/file")
public class FileController {

    @Autowired
    private MinioService minioService;

    /**
     * 上传课程封面图片
     */
    @PostMapping("/upload/course-cover")
    public Result uploadCourseCover(@RequestParam("file") MultipartFile file) {
        try {
            String fileUrl = minioService.uploadCourseCover(file);
            Map<String, Object> result = new HashMap<>();
            result.put("fileUrl", fileUrl);
            result.put("fileName", file.getOriginalFilename());
            result.put("fileSize", file.getSize());
            return Result.success("课程封面上传成功", result);
        } catch (Exception e) {
            return Result.fail("课程封面上传失败: " + e.getMessage());
        }
    }

    /**
     * 上传课程视频
     */
    @PostMapping("/upload/course-video")
    public Result uploadCourseVideo(@RequestParam("file") MultipartFile file) {
        try {
            String fileUrl = minioService.uploadCourseVideo(file);
            Map<String, Object> result = new HashMap<>();
            result.put("fileUrl", fileUrl);
            result.put("fileName", file.getOriginalFilename());
            result.put("fileSize", file.getSize());
            return Result.success("课程视频上传成功", result);
        } catch (Exception e) {
            return Result.fail("课程视频上传失败: " + e.getMessage());
        }
    }

    /**
     * 上传用户头像
     */
    @PostMapping("/upload/user-avatar")
    public Result uploadUserAvatar(@RequestParam("file") MultipartFile file) {
        try {
            String fileUrl = minioService.uploadUserAvatar(file);
            Map<String, Object> result = new HashMap<>();
            result.put("fileUrl", fileUrl);
            result.put("fileName", file.getOriginalFilename());
            result.put("fileSize", file.getSize());
            return Result.success("用户头像上传成功", result);
        } catch (Exception e) {
            return Result.fail("用户头像上传失败: " + e.getMessage());
        }
    }

    /**
     * 上传文档文件
     */
    @PostMapping("/upload/document")
    public Result uploadDocument(@RequestParam("file") MultipartFile file) {
        try {
            String fileUrl = minioService.uploadDocument(file);
            Map<String, Object> result = new HashMap<>();
            result.put("fileUrl", fileUrl);
            result.put("fileName", file.getOriginalFilename());
            result.put("fileSize", file.getSize());
            return Result.success("文档上传成功", result);
        } catch (Exception e) {
            return Result.fail("文档上传失败: " + e.getMessage());
        }
    }

    /**
     * 通用文件上传
     */
    @PostMapping("/upload")
    public Result uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("folder") String folder) {
        try {
            String fileUrl = minioService.uploadFile(file, folder);
            Map<String, Object> result = new HashMap<>();
            result.put("fileUrl", fileUrl);
            result.put("fileName", file.getOriginalFilename());
            result.put("fileSize", file.getSize());
            result.put("folder", folder);
            return Result.success("文件上传成功", result);
        } catch (Exception e) {
            return Result.fail("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 下载文件
     */
    @GetMapping("/download")
    public ResponseEntity<InputStreamResource> downloadFile(@RequestParam("objectName") String objectName) {
        try {
            InputStream inputStream = minioService.downloadFile(objectName);
            InputStreamResource resource = new InputStreamResource(inputStream);
            
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + objectName);
            headers.add(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
            headers.add(HttpHeaders.PRAGMA, "no-cache");
            headers.add(HttpHeaders.EXPIRES, "0");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 删除文件
     */
    @DeleteMapping("/delete")
    public Result deleteFile(@RequestParam("objectName") String objectName) {
        try {
            minioService.deleteFile(objectName);
            return Result.success("文件删除成功", null);
        } catch (Exception e) {
            return Result.fail("文件删除失败: " + e.getMessage());
        }
    }

    /**
     * 获取文件列表
     */
    @GetMapping("/list")
    public Result listFiles(@RequestParam("folder") String folder) {
        try {
            List<String> files = minioService.listFiles(folder);
            return Result.success(files);
        } catch (Exception e) {
            return Result.fail("获取文件列表失败: " + e.getMessage());
        }
    }

    /**
     * 检查文件是否存在
     */
    @GetMapping("/exists")
    public Result fileExists(@RequestParam("objectName") String objectName) {
        try {
            boolean exists = minioService.fileExists(objectName);
            Map<String, Object> result = new HashMap<>();
            result.put("exists", exists);
            result.put("objectName", objectName);
            return Result.success(result);
        } catch (Exception e) {
            return Result.fail("检查文件失败: " + e.getMessage());
        }
    }

    /**
     * 获取预签名URL
     */
    @GetMapping("/presigned-url")
    public Result getPresignedUrl(
            @RequestParam("objectName") String objectName,
            @RequestParam(defaultValue = "3600") int expirySeconds) {
        try {
            String presignedUrl = minioService.getPresignedUrl(objectName, expirySeconds);
            Map<String, Object> result = new HashMap<>();
            result.put("presignedUrl", presignedUrl);
            result.put("expirySeconds", expirySeconds);
            return Result.success(result);
        } catch (Exception e) {
            return Result.fail("获取预签名URL失败: " + e.getMessage());
        }
    }

    /**
     * 获取MinIO状态
     */
    @GetMapping("/status")
    public Result getMinioStatus() {
        try {
            Map<String, Object> status = new HashMap<>();
            status.put("bucketName", "course-files");
            status.put("endpoint", "http://localhost:9000");
            status.put("status", "connected");
            return Result.success(status);
        } catch (Exception e) {
            return Result.fail("MinIO连接失败: " + e.getMessage());
        }
    }
} 