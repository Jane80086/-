package com.cemenghui.news.service;

import com.cemenghui.news.entity.UserOperationLog;
import com.cemenghui.news.entity.UserViewLog;

public interface LogService {

    /**
     * 记录操作日志
     */
    void recordOperation(UserOperationLog operationLog);

    /**
     * 记录浏览日志
     */
    void recordView(UserViewLog viewLog);
}