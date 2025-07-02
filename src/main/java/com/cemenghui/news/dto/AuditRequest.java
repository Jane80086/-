package com.cemenghui.news.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class AuditRequest {

    @NotNull(message = "审核状态不能为空")
    @Min(value = 1, message = "审核状态值错误")
    @Max(value = 2, message = "审核状态值错误")
    private Integer status;

    private String comment;
}
