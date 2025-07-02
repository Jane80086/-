package com.cemenghui.news.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefineResponse {
    private boolean success;
    private String message;
    private String refinedContent; // 润色后的内容
}