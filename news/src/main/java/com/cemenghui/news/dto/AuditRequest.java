package com.cemenghui.news.dto;

import lombok.Data;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class AuditRequest {
    @NotNull(message = "审核状态不能为空")
    // Status can now be 1 (Publish) or 2 (Reject)
    @Min(value = 1, message = "审核状态值错误，应为1或2")
    @Max(value = 2, message = "审核状态值错误，应为1或2")
    private Integer status;

    private String comment;
}