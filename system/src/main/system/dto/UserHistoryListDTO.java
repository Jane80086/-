package com.system.dto;

import com.system.entity.UserModifyHistory;
import lombok.Data;
import java.util.List;

@Data
public class UserHistoryListDTO {
    private List<UserModifyHistory> records;  // 历史记录列表
    private int total;                        // 总记录数
    private int pages;                        // 总页数
    private int current;                      // 当前页
    private int size;                         // 每页大小

    // 手动添加getter和setter方法
    public List<UserModifyHistory> getRecords() {
        return records;
    }

    public void setRecords(List<UserModifyHistory> records) {
        this.records = records;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
} 