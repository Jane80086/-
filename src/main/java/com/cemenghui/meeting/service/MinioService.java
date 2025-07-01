package com.cemenghui.meeting.service;

import com.cemenghui.meeting.bean.ApiResponse;
import io.minio.*;
import io.minio.http.Method;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class MinioService {

    @Value("${minio.endpoint}")
    private String endpoint;

    @Value("${minio.accessKey}")
    private String accessKey;

    @Value("${minio.secretKey}")
    private String secretKey;

    @Value("${minio.bucket}")
    private String bucket;

    @Value("${minio.presigned-url-expiration:3600}")
    private int presignedUrlExpiration; // 预签名URL过期时间，默认1小时

    private MinioClient minioClient;

    @PostConstruct
    public void init() {
        try {
            minioClient = MinioClient.builder()
                    .endpoint(endpoint)
                    .credentials(accessKey, secretKey)
                    .build();
            
            // 检查并创建bucket
            boolean bucketExists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
            if (!bucketExists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
                log.info("Created bucket: {}", bucket);
            }
            
            // 设置bucket为私有（如果需要）
            setBucketPrivate();
            
        } catch (Exception e) {
            log.error("MinIO初始化失败", e);
        }
    }

    /**
     * 设置bucket为私有
     */
    private void setBucketPrivate() {
        try {
            // 移除所有公共读取策略，使bucket变为私有
            minioClient.deleteBucketPolicy(DeleteBucketPolicyArgs.builder().bucket(bucket).build());
            log.info("Bucket {} 已设置为私有", bucket);
        } catch (Exception e) {
            log.warn("设置bucket为私有失败: {}", e.getMessage());
        }
    }

    public ApiResponse<String> uploadFile(MultipartFile file) {
        try {
            String originalFilename = file.getOriginalFilename();
            
            String objectName = UUID.randomUUID().toString() + "_" + originalFilename;
            
            InputStream inputStream = file.getInputStream();
            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .stream(inputStream, file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build()
            );
            
            // 对于私有bucket，返回对象名称而不是完整URL
            // 前端需要通过API获取预签名URL来访问文件
            return ApiResponse.success(objectName);
            
        } catch (Exception e) {
            log.error("文件上传失败", e);
            return ApiResponse.error(500, "文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 生成预签名URL用于访问私有文件
     */
    public ApiResponse<String> getPresignedUrl(String objectName) {
        try {
            String presignedUrl = minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(bucket)
                    .object(objectName)
                    .expiry(presignedUrlExpiration, TimeUnit.SECONDS)
                    .build()
            );
            
            return ApiResponse.success(presignedUrl);
        } catch (Exception e) {
            log.error("生成预签名URL失败", e);
            return ApiResponse.error(500, "生成预签名URL失败: " + e.getMessage());
        }
    }

    /**
     * 批量获取预签名URL
     */
    public ApiResponse<java.util.Map<String, String>> getBatchPresignedUrls(java.util.List<String> objectNames) {
        try {
            java.util.Map<String, String> urlMap = new java.util.HashMap<>();
            for (String objectName : objectNames) {
                String presignedUrl = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                        .method(Method.GET)
                        .bucket(bucket)
                        .object(objectName)
                        .expiry(presignedUrlExpiration, TimeUnit.SECONDS)
                        .build()
                );
                urlMap.put(objectName, presignedUrl);
            }
            return ApiResponse.success(urlMap);
        } catch (Exception e) {
            log.error("批量生成预签名URL失败", e);
            return ApiResponse.error(500, "批量生成预签名URL失败: " + e.getMessage());
        }
    }

    public ApiResponse<Boolean> deleteFile(String objectName) {
        try {
            minioClient.removeObject(
                RemoveObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .build()
            );
            return ApiResponse.success(true);
        } catch (Exception e) {
            log.error("文件删除失败", e);
            return ApiResponse.error(500, "文件删除失败: " + e.getMessage());
        }
    }

    /**
     * 检查文件是否存在
     */
    public boolean fileExists(String objectName) {
        try {
            minioClient.statObject(
                StatObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .build()
            );
            return true;
        } catch (Exception e) {
            return false;
        }
    }
} 