package com.cemenghui.news.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class NewsVO {
    private Long id;
    private String title;
    private String image;
    private String content;
    private String summary;
    private String author;
    private LocalDateTime createTime;
    private Integer status;
    private String statusText;
    private Integer viewCount;
    private Boolean canEdit;
    private Boolean canDelete;
    private Boolean isOwner;

    public String getFormattedCreateTime() {
        if (createTime == null) return "";
        return createTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public String getStatusDisplayName() {
        if (status == null) return "未知";
        switch (status) {
            case 0: return "待审核";
            case 1: return "已发布";
            case 2: return "已拒绝";
            case 3: return "已删除";
            default: return "未知";
        }
    }
}