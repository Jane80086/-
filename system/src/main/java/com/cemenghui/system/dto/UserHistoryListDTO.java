package com.cemenghui.system.dto;

import com.cemenghui.system.entity.UserModifyHistory;
import lombok.Data;

import java.util.List;

@Data
public class UserHistoryListDTO {
    private List<UserModifyHistory> records;  // 历史记录列表
    private int total;                        // 总记录数
    private int pages;                        // 总页数
    private int current;                      // 当前页
    private int size;                         // 每页大小
} 