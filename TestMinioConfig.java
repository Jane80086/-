import io.minio.MinioClient;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.messages.Bucket;
import java.util.List;

public class TestMinioConfig {
    public static void main(String[] args) {
        try {
            // 创建MinIO客户端
            MinioClient minioClient = MinioClient.builder()
                    .endpoint("http://localhost:9000")
                    .credentials("minioadmin", "minioadmin")
                    .build();
            
            String bucketName = "course-manager";
            
            // 检查存储桶是否存在
            boolean bucketExists = minioClient.bucketExists(
                BucketExistsArgs.builder()
                    .bucket(bucketName)
                    .build());
            
            System.out.println("存储桶存在: " + bucketExists);
            
            if (!bucketExists) {
                // 创建存储桶
                minioClient.makeBucket(
                    MakeBucketArgs.builder()
                        .bucket(bucketName)
                        .build());
                
                System.out.println("存储桶创建成功: " + bucketName);
            }
            
            // 列出所有存储桶
            List<Bucket> buckets = minioClient.listBuckets();
            System.out.println("存储桶列表:");
            for (Bucket bucket : buckets) {
                System.out.println("- " + bucket.name());
            }
            
        } catch (Exception e) {
            System.err.println("MinIO测试失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 