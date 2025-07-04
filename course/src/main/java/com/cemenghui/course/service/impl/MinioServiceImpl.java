package com.cemenghui.course.service.impl;

import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class MinioServiceImpl {

    @Autowired
    private MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucketName;

    @Value("${minio.endpoint}")
    private String endpoint;

    /**
     * 上传文件
     */
    public String uploadFile(MultipartFile file, String folder) {
        try {
            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String fileName = UUID.randomUUID().toString() + extension;
            
            // 构建对象名称
            String objectName = folder + "/" + fileName;
            
            // 上传文件
            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build()
            );
            
            // 返回文件访问URL
            return getFileUrl(objectName);
            
        } catch (Exception e) {
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 上传文件（字节数组）
     */
    public String uploadFile(byte[] fileData, String fileName, String contentType, String folder) {
        try {
            // 构建对象名称
            String objectName = folder + "/" + fileName;
            
            // 上传文件
            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .stream(new java.io.ByteArrayInputStream(fileData), fileData.length, -1)
                    .contentType(contentType)
                    .build()
            );
            
            return getFileUrl(objectName);
            
        } catch (Exception e) {
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 下载文件
     */
    public InputStream downloadFile(String objectName) {
        try {
            return minioClient.getObject(
                GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("文件下载失败: " + e.getMessage());
        }
    }

    /**
     * 获取文件大小
     * @param objectName 文件对象名
     * @return 文件大小（字节），不存在返回0
     */
    public long getFileSize(String objectName) {
        try {
            StatObjectResponse stat = minioClient.statObject(
                StatObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build()
            );
            return stat.size();
        } catch (io.minio.errors.ErrorResponseException e) {
            if (e.errorResponse().code().equals("NoSuchKey")) {
                return 0L;
            }
            throw new RuntimeException("获取文件大小失败: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("获取文件大小失败: " + e.getMessage());
        }
    }

    /**
     * 分片下载文件，支持断点续传
     * @param objectName 文件对象名
     * @param start 起始字节
     * @param end 结束字节（包含）
     * @return 输入流
     */
    public InputStream downloadFile(String objectName, long start, long end) {
        try {
            long fileSize = getFileSize(objectName);
            if (fileSize == 0) {
                throw new RuntimeException("文件不存在: " + objectName);
            }
            if (start < 0) start = 0;
            if (end >= fileSize) end = fileSize - 1;
            if (start > end) start = 0;
            return minioClient.getObject(
                GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .offset(start)
                    .length(end - start + 1)
                    .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("分片下载失败: " + e.getMessage());
        }
    }

    /**
     * 删除文件
     */
    public void deleteFile(String objectName) {
        try {
            minioClient.removeObject(
                RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("文件删除失败: " + e.getMessage());
        }
    }

    /**
     * 获取文件列表
     */
    public List<String> listFiles(String folder) {
        try {
            List<String> fileList = new ArrayList<>();
            
            Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder()
                    .bucket(bucketName)
                    .prefix(folder + "/")
                    .recursive(true)
                    .build()
            );
            
            for (Result<Item> result : results) {
                Item item = result.get();
                if (!item.isDir()) {
                    fileList.add(item.objectName());
                }
            }
            
            return fileList;
            
        } catch (Exception e) {
            throw new RuntimeException("获取文件列表失败: " + e.getMessage());
        }
    }

    /**
     * 检查文件是否存在
     */
    public boolean fileExists(String objectName) {
        try {
            minioClient.statObject(
                StatObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build()
            );
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取文件URL
     */
    public String getFileUrl(String objectName) {
        return endpoint + "/" + bucketName + "/" + objectName;
    }

    /**
     * 获取预签名URL（用于临时访问）
     */
    public String getPresignedUrl(String objectName, int expirySeconds) {
        try {
            return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(bucketName)
                    .object(objectName)
                    .expiry(expirySeconds)
                    .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("获取预签名URL失败: " + e.getMessage());
        }
    }

    /**
     * 上传课程封面图片
     */
    public String uploadCourseCover(MultipartFile file) {
        return uploadFile(file, "course-covers");
    }

    /**
     * 上传课程视频
     */
    public String uploadCourseVideo(MultipartFile file) {
        return uploadFile(file, "course-videos");
    }

    /**
     * 上传用户头像
     */
    public String uploadUserAvatar(MultipartFile file) {
        return uploadFile(file, "user-avatars");
    }

    /**
     * 上传文档文件
     */
    public String uploadDocument(MultipartFile file) {
        return uploadFile(file, "documents");
    }
} 