package com.cemenghui.course.config;

import io.minio.MinioClient;
import io.minio.messages.Bucket;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class MinioConfig {

    @Value("${minio.endpoint}")
    private String endpoint;

    @Value("${minio.access-key}")
    private String accessKey;

    @Value("${minio.secret-key}")
    private String secretKey;

    @Value("${minio.bucket}")
    private String bucketName;

    /**
     * 创建MinIO客户端并初始化存储桶
     */
    @Bean
    public MinioClient minioClient() {
        MinioClient minioClient = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
        
        // 初始化存储桶
        initBucket(minioClient);
        
        return minioClient;
    }

    /**
     * 初始化存储桶
     */
    private void initBucket(MinioClient minioClient) {
        try {
            // 检查存储桶是否存在
            boolean bucketExists = minioClient.bucketExists(
                io.minio.BucketExistsArgs.builder()
                    .bucket(bucketName)
                    .build());
            
            if (!bucketExists) {
                // 创建存储桶
                minioClient.makeBucket(
                    io.minio.MakeBucketArgs.builder()
                        .bucket(bucketName)
                        .build());
                
                System.out.println("MinIO存储桶创建成功: " + bucketName);
            } else {
                System.out.println("MinIO存储桶已存在: " + bucketName);
            }
            
            // 列出所有存储桶
            List<Bucket> buckets = minioClient.listBuckets();
            System.out.println("MinIO存储桶列表:");
            for (Bucket bucket : buckets) {
                System.out.println("- " + bucket.name());
            }
            
        } catch (Exception e) {
            System.err.println("MinIO初始化失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 