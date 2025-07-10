package com.cemenghui.system.dto;

import lombok.Data;

@Data
public class UserQueryDTO {
    private String keyword;         // 搜索关键词（用户名、手机号）
    private String status;          // 用户状态
    private Integer currentPage = 1; // 当前页码
    private Integer pageSize = 20;  // 每页数量
    private String sortField;       // 排序字段
    private String sortOrder;       // 排序方向（asc/desc）
    private Integer size;
    private Integer page;
    private String realName;        // 用户真实姓名（用于模糊搜索）
    private String email;           // 用户邮箱
    private String account;         // 用户账号
    private String enterpriseName;  // 企业名称
    private String enterpriseType;  // 企业类型
    private String phone;           // 手机号码
}
