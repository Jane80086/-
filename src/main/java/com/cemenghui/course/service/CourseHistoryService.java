package com.cemenghui.course.service;

import com.cemenghui.course.entity.Course;
import java.util.List;

/**
 * 课程历史记录服务接口
 */
public interface CourseHistoryService {
    /**
     * 记录用户播放课程行为
     * @param userId 用户ID
     * @param courseId 课程ID
     */
    void recordHistory(Long userId, Long courseId);

    /**
     * 获取用户最近观看课程列表
     * @param userId 用户ID
     * @return 课程列表
     */
    List<Course> getUserHistory(Long userId);
} 