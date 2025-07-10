package com.cemenghui.system.dto;

import com.cemenghui.system.entity.EnterpriseUser;
import lombok.Data;

import java.util.List;

@Data
public class UserListDTO {
    private long total;             // 总记录数
    private int pages;              // 总页数
    private List<EnterpriseUser> records; // 用户列表
}
