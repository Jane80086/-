package com.cemenghui.news.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SearchRequest {
    private String keyword;
    private String author;
    private String summary;
    private Integer status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer page = 1;
    private Integer pageSize = 10;
    private String sortBy = "create_time";
    private String sortOrder = "desc";

    public Integer getOffset() {
        return (page - 1) * pageSize;
    }
}
