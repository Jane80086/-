package com.cemenghui.course.controller;

import com.cemenghui.course.common.Result;
import com.cemenghui.course.service.impl.MinioServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
    private MinioServiceImpl minioServiceImpl;

    /**
     * 上传课程封面图片
     */
    @PostMapping("/upload/course-cover")
    public Result uploadCourseCover(@RequestParam("file") MultipartFile file) {
        try {
            String fileUrl = minioServiceImpl.uploadCourseCover(file);
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
            String fileUrl = minioServiceImpl.uploadCourseVideo(file);
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
            String fileUrl = minioServiceImpl.uploadUserAvatar(file);
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
            String fileUrl = minioServiceImpl.uploadDocument(file);
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
            String fileUrl = minioServiceImpl.uploadFile(file, folder);
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
            InputStream inputStream = minioServiceImpl.downloadFile(objectName);
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
            minioServiceImpl.deleteFile(objectName);
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
            List<String> files = minioServiceImpl.listFiles(folder);
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
            boolean exists = minioServiceImpl.fileExists(objectName);
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
            String presignedUrl = minioServiceImpl.getPresignedUrl(objectName, expirySeconds);
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

    /**
     * 视频流播放接口，支持<video>标签直接播放，支持断点续传
     */
    @GetMapping("/stream")
    public ResponseEntity<InputStreamResource> streamVideo(@RequestParam("objectName") String objectName, @RequestHeader(value = "Range", required = false) String rangeHeader) {
        try {
            // 获取视频文件总长度
            long fileSize = minioServiceImpl.getFileSize(objectName);
            InputStream inputStream;
            long start = 0, end = fileSize - 1;
            boolean isPartial = false;

            if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
                // 解析Range头
                String[] ranges = rangeHeader.replace("bytes=", "").split("-");
                try {
                    start = Long.parseLong(ranges[0]);
                    if (ranges.length > 1 && !ranges[1].isEmpty()) {
                        end = Long.parseLong(ranges[1]);
                    }
                } catch (NumberFormatException ignored) {}
                if (end >= fileSize) end = fileSize - 1;
                if (start > end) start = 0;
                isPartial = true;
            }

            inputStream = minioServiceImpl.downloadFile(objectName, start, end);
            InputStreamResource resource = new InputStreamResource(inputStream);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_TYPE, "video/mp4");
            headers.add(HttpHeaders.ACCEPT_RANGES, "bytes");
            if (isPartial) {
                headers.add(HttpHeaders.CONTENT_RANGE, String.format("bytes %d-%d/%d", start, end, fileSize));
                headers.setContentLength(end - start + 1);
                return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                        .headers(headers)
                        .body(resource);
            } else {
                headers.setContentLength(fileSize);
                return ResponseEntity.ok()
                        .headers(headers)
                        .body(resource);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
} 