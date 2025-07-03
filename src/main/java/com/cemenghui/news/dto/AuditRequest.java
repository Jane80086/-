package com.cemenghui.news.dto;

import lombok.Data;
import javax.validation.constraints.*;

@Data
public class AuditRequest {

    @NotNull(message = "审核状态不能为空")
    @Min(value = 1, message = "审核状态值错误")
    @Max(value = 2, message = "审核状态值错误")
    private Integer status;

    private String comment;
}
