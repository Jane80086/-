package com.cemenghui.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cemenghui.course.dao.CourseHistoryDao;
import com.cemenghui.course.dao.CourseDao;
import com.cemenghui.course.entity.Course;
import com.cemenghui.course.entity.CourseHistory;
import com.cemenghui.course.service.CourseHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 课程历史记录服务
 */
@Service
public class CourseHistoryServiceImpl implements CourseHistoryService {
    @Autowired
    private CourseHistoryDao courseHistoryDao;
    
    @Autowired
    private CourseDao courseDao;

    /**
     * 记录用户播放课程行为
     * @param userId 用户ID
     * @param courseId 课程ID
     */
    @Override
    public void recordHistory(Long userId, Long courseId) {
        CourseHistory history = new CourseHistory();
        history.setUserId(userId);
        history.setCourseId(courseId);
        courseHistoryDao.insert(history);
    }

    /**
     * 获取用户最近观看课程列表
     * @param userId 用户ID
     * @return 课程列表
     */
    @Cacheable(value = "userHistory", key = "#userId")
    @Override
    public List<Course> getUserHistory(Long userId) {
        // 查询用户历史记录
        LambdaQueryWrapper<CourseHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseHistory::getUserId, userId)
               .orderByDesc(CourseHistory::getViewedAt);
        List<CourseHistory> histories = courseHistoryDao.selectList(wrapper);
        
        // 获取对应的课程信息
        return histories.stream()
                .map(history -> courseDao.selectById(history.getCourseId()))
                .filter(course -> course != null)
                .collect(Collectors.toList());
    }
} 