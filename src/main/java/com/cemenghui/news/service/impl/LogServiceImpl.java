package com.cemenghui.news.service.impl;

import com.cemenghui.news.entity.UserOperationLog;
import com.cemenghui.news.entity.UserViewLog;
import com.cemenghui.news.mapper.UserOperationLogMapper;
import com.cemenghui.news.mapper.UserViewLogMapper;
import com.cemenghui.news.service.LogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogServiceImpl implements LogService {

    private final UserOperationLogMapper operationLogMapper;
    private final UserViewLogMapper viewLogMapper;

    @Override
    public void recordOperation(UserOperationLog operationLog) {
        try {
            operationLogMapper.insert(operationLog);
        } catch (Exception e) {
            log.error("记录操作日志失败", e);
        }
    }

    @Override
    public void recordView(UserViewLog viewLog) {
        try {
            viewLogMapper.insert(viewLog);
        } catch (Exception e) {
            log.error("记录浏览日志失败", e);
        }
    }
}
