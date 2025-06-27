import io.minio.MinioClient;
import io.minio.messages.Bucket;
import java.util.List;

public class TestMinioConnection {
    public static void main(String[] args) {
        try {
            // 创建MinIO客户端
            MinioClient minioClient = MinioClient.builder()
                    .endpoint("http://localhost:9000")
                    .credentials("minioadmin", "minioadmin")
                    .build();
            
            System.out.println("MinIO客户端创建成功");
            
            // 列出所有存储桶
            List<Bucket> buckets = minioClient.listBuckets();
            System.out.println("存储桶列表:");
            for (Bucket bucket : buckets) {
                System.out.println("- " + bucket.name());
            }
            
            // 检查course-files存储桶是否存在
            boolean bucketExists = minioClient.bucketExists(
                io.minio.BucketExistsArgs.builder()
                    .bucket("course-files")
                    .build());
            
            if (!bucketExists) {
                // 创建存储桶
                minioClient.makeBucket(
                    io.minio.MakeBucketArgs.builder()
                        .bucket("course-files")
                        .build());
                System.out.println("course-files存储桶创建成功");
            } else {
                System.out.println("course-files存储桶已存在");
            }
            
            System.out.println("MinIO连接测试成功！");
            
        } catch (Exception e) {
            System.err.println("MinIO连接测试失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 