package com.cemenghui.news.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileUploadService {

    /**
     * 上传图片
     */
    String uploadImage(MultipartFile file);

    /**
     * 删除图片
     */
    boolean deleteImage(String path);

    /**
     * 验证图片文件
     */
    boolean validateImageFile(MultipartFile file);

    /**
     * 获取图片URL
     */
    String getImageUrl(String path);
}