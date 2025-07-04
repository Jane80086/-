package com.cemenghui.course;

import io.minio.MinioClient;
import io.minio.messages.Bucket;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import java.util.List;

//@SpringBootApplication
@ComponentScan(basePackages = {"com.cemenghui.course.config"})
public class TestMinioApplication implements CommandLineRunner {

    private final MinioClient minioClient;

    public TestMinioApplication(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    public static void main(String[] args) {
        SpringApplication.run(TestMinioApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=== MinIO 连接测试 ===");
        
        try {
            // 列出所有存储桶
            List<Bucket> buckets = minioClient.listBuckets();
            System.out.println("存储桶列表:");
            for (Bucket bucket : buckets) {
                System.out.println("- " + bucket.name());
            }
            
            System.out.println("MinIO 连接测试成功！");
            
        } catch (Exception e) {
            System.err.println("MinIO 连接测试失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        // 测试完成后退出
        System.exit(0);
    }
} 