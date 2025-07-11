package com.cemenghui.news.dto;

import lombok.Data;

@Data
public class PageRequest {
    private Integer page = 1;
    private Integer pageSize = 10;
    private String sortBy = "create_time";
    private String sortOrder = "desc";

    public Integer getOffset() {
        return (page - 1) * pageSize;
    }

    public Integer getValidPageSize() {
        if (pageSize == null || pageSize <= 0) {
            return 10;
        }
        return Math.min(pageSize, 100);
    }
}