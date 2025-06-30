package com.cemenghui.news.dto;

import lombok.Data;
import jakarta.validation.constraints.*;

@Data
public class NewsRequest {

    @NotBlank(message = "标题不能为空")
    @Size(max = 200, message = "标题长度不超过200字符")
    private String title;

    private String image;

    @NotBlank(message = "内容不能为空")
    private String content;

    @Size(max = 500, message = "简介长度不超过500字符")
    private String summary;

    @NotBlank(message = "作者不能为空")
    @Size(max = 100, message = "作者名称不超过100字符")
    private String author;
}