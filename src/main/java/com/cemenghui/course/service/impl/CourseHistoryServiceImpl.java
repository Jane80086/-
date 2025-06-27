package com.cemenghui.course.service.impl;

import com.cemenghui.course.dao.CourseHistoryDao;
import com.cemenghui.course.entity.Course;
import com.cemenghui.course.service.CourseHistoryService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 课程历史记录服务
 */
@Service
public class CourseHistoryServiceImpl implements CourseHistoryService {
    private CourseHistoryDao dao;

    /**
     * 记录用户播放课程行为
     * @param userId 用户ID
     * @param courseId 课程ID
     */
    @Override
    public void recordHistory(Long userId, Long courseId) {
        // 记录历史逻辑
    }

    /**
     * 获取用户最近观看课程列表
     * @param userId 用户ID
     * @return 课程列表
     */
    @Cacheable(value = "userHistory", key = "#userId")
    @Override
    public List<Course> getUserHistory(Long userId) {
        // 查询历史逻辑
        return null;
    }
} 