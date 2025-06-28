"""
创建配置文件
"""

import os
import json
from pathlib import Path

def create_mcp_config():
    """创建MCP配置文件"""
    config = {
        "mcpServers": {
            "simple": {
                "command": "python",
                "args": ["simple_mcp_server.py"],
                "env": {}
            },
            "course": {
                "command": "python",
                "args": ["mcp_course_server.py"],
                "env": {}
            },
            "qa": {
                "command": "python",
                "args": ["mcp_qa_server.py"],
                "env": {}
            },
            "video_optimizer": {
                "command": "python",
                "args": ["mcp_video_optimizer_server.py"],
                "env": {}
            },
            "course_optimizer": {
                "command": "python",
                "args": ["ai/mcp_course_optimizer.py"],
                "env": {}
            }
        },
        "api": {
            "base_url": "http://localhost:8080/api",
            "timeout": 30
        },
        "minio": {
            "endpoint": "http://localhost:9000",
            "access_key": "minioadmin",
            "secret_key": "minioadmin",
            "bucket": "course-files"
        }
    }
    
    with open("mcp_config.json", "w", encoding="utf-8") as f:
        json.dump(config, f, indent=2, ensure_ascii=False)
    
    print("✅ MCP配置文件已创建: mcp_config.json")

def create_env_file():
    """创建环境变量文件"""
    env_content = """# 数据库配置
DB_HOST=localhost
DB_PORT=54321
DB_NAME=course_management
DB_USER=SYSTEM
DB_PASSWORD=123456

# Redis配置
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_DB=0

# MinIO配置
MINIO_ENDPOINT=http://localhost:9000
MINIO_ACCESS_KEY=minioadmin
MINIO_SECRET_KEY=minioadmin
MINIO_BUCKET=course-files

# API配置
API_BASE_URL=http://localhost:8080/api
API_TIMEOUT=30

# MCP服务器配置
MCP_SIMPLE_PORT=6277
MCP_COURSE_PORT=6278
MCP_QA_PORT=6279
MCP_VIDEO_PORT=6280
MCP_OPTIMIZER_PORT=6281

# JWT配置
JWT_SECRET=your_jwt_secret_key_please_change
JWT_EXPIRATION=86400000

# 日志配置
LOG_LEVEL=INFO
LOG_FILE=logs/app.log
"""
    
    with open(".env", "w", encoding="utf-8") as f:
        f.write(env_content)
    
    print("✅ 环境变量文件已创建: .env")

def create_docker_compose():
    """创建Docker Compose文件"""
    docker_compose = """version: '3.8'

services:
  # 数据库服务
  database:
    image: kingbase:latest
    container_name: course_db
    environment:
      - KINGBASE_DB=course_management
      - KINGBASE_USER=SYSTEM
      - KINGBASE_PASSWORD=123456
    ports:
      - "54321:54321"
    volumes:
      - db_data:/var/lib/kingbase
    networks:
      - course_network

  # Redis服务
  redis:
    image: redis:7-alpine
    container_name: course_redis
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data
    networks:
      - course_network

  # MinIO服务
  minio:
    image: minio/minio:latest
    container_name: course_minio
    environment:
      - MINIO_ROOT_USER=minioadmin
      - MINIO_ROOT_PASSWORD=minioadmin
    ports:
      - "9000:9000"
      - "9001:9001"
    volumes:
      - minio_data:/data
    command: server /data --console-address ":9001"
    networks:
      - course_network

  # Spring Boot应用
  app:
    build: .
    container_name: course_app
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=docker
    depends_on:
      - database
      - redis
      - minio
    networks:
      - course_network

volumes:
  db_data:
  redis_data:
  minio_data:

networks:
  course_network:
    driver: bridge
"""
    
    with open("docker-compose.yml", "w", encoding="utf-8") as f:
        f.write(docker_compose)
    
    print("✅ Docker Compose文件已创建: docker-compose.yml")

def create_dockerfile():
    """创建Dockerfile"""
    dockerfile = """# 使用OpenJDK 17作为基础镜像
FROM openjdk:17-jdk-slim

# 设置工作目录
WORKDIR /app

# 复制Maven包装器
COPY mvnw .
COPY mvnw.cmd .
COPY .mvn .mvn

# 复制pom.xml
COPY pom.xml .

# 下载依赖
RUN ./mvnw dependency:go-offline -B

# 复制源代码
COPY src src

# 构建应用
RUN ./mvnw clean package -DskipTests

# 暴露端口
EXPOSE 8080

# 启动应用
CMD ["java", "-jar", "target/course-0.0.1-SNAPSHOT.jar"]
"""
    
    with open("Dockerfile", "w", encoding="utf-8") as f:
        f.write(dockerfile)
    
    print("✅ Dockerfile已创建: Dockerfile")

def create_logs_directory():
    """创建日志目录"""
    logs_dir = Path("logs")
    logs_dir.mkdir(exist_ok=True)
    
    # 创建.gitkeep文件
    (logs_dir / ".gitkeep").touch()
    
    print("✅ 日志目录已创建: logs/")

def main():
    """主函数"""
    print("=" * 50)
    print("创建配置文件")
    print("=" * 50)
    
    create_mcp_config()
    create_env_file()
    create_docker_compose()
    create_dockerfile()
    create_logs_directory()
    
    print("=" * 50)
    print("所有配置文件创建完成！")
    print("=" * 50)

if __name__ == "__main__":
    main() 