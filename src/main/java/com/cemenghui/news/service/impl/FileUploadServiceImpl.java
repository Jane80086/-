package com.cemenghui.news.service.impl;

import com.cemenghui.news.service.FileUploadService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

@Slf4j
@Service
public class FileUploadServiceImpl implements FileUploadService {

    // 上传路径（可在 application.yml 中配置，如：upload.path=/uploads/images）
    @Value("${upload.path:/uploads/images}")
    private String uploadPath;

    // 允许的图片类型
    private static final String[] ALLOWED_TYPES = {"jpg", "jpeg", "png", "gif", "webp"};

    // 最大文件大小 5MB
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    @Override
    public String uploadImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }

        if (!validateImageFile(file)) {
            throw new IllegalArgumentException("不支持的图片格式或文件过大");
        }

        String ext = FilenameUtils.getExtension(file.getOriginalFilename());
        String fileName = UUID.randomUUID().toString().replace("-", "") + "." + ext;

        File dir = new File(uploadPath);
        if (!dir.exists()) {
            boolean mkdirs = dir.mkdirs();
            if (!mkdirs) throw new RuntimeException("创建上传目录失败");
        }

        File dest = new File(dir, fileName);
        try {
            file.transferTo(dest);
        } catch (IOException e) {
            log.error("上传文件失败", e);
            throw new RuntimeException("上传失败：" + e.getMessage());
        }

        return getImageUrl(fileName);
    }

    @Override
    public boolean deleteImage(String path) {
        if (!StringUtils.hasText(path)) return false;

        File file = new File(uploadPath, path);
        return file.exists() && file.delete();
    }

    @Override
    public boolean validateImageFile(MultipartFile file) {
        if (file.getSize() > MAX_FILE_SIZE) return false;

        String ext = FilenameUtils.getExtension(file.getOriginalFilename()).toLowerCase();
        return Arrays.asList(ALLOWED_TYPES).contains(ext);
    }

    @Override
    public String getImageUrl(String path) {
        // 假设前端通过 http://localhost:8080/uploads/images/ 访问图片
        return "/uploads/images/" + path;
    }
}
