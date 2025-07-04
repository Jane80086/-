package com.cemenghui.system.dto;

import com.cemenghui.system.entity.EnterpriseUser;
import lombok.Data;

import java.util.List;

@Data
public class UserListDTO {
    private long total;             // 总记录数
    private int pages;              // 总页数
    private List<EnterpriseUser> records; // 用户列表

    // 手动添加getter和setter方法
    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public List<EnterpriseUser> getRecords() {
        return records;
    }

    public void setRecords(List<EnterpriseUser> records) {
        this.records = records;
    }
}
