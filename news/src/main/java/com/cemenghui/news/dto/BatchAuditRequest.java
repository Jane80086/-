package com.cemenghui.news.dto;

import lombok.Data;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class BatchAuditRequest {

    @NotEmpty(message = "新闻ID列表不能为空")
    private List<Long> newsIds;

    @Valid
    private AuditRequest auditRequest;
}