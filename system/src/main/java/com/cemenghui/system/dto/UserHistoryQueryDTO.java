package com.cemenghui.system.dto;

import lombok.Data;

@Data
public class UserHistoryQueryDTO {
    private String userId;          // 用户ID
    private String operatorId;      // 操作人ID
    private String fieldName;       // 修改字段
    private String startTime;       // 开始时间
    private String endTime;         // 结束时间
    private Integer page = 1;       // 当前页码
    private Integer pageSize = 10;  // 每页数量
    private String sortField;       // 排序字段
    private String sortOrder;       // 排序方向（asc/desc）
} 