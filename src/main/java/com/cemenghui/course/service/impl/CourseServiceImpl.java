package com.cemenghui.course.service.impl;

import com.cemenghui.course.dao.CourseDao;
import com.cemenghui.course.entity.Course;
import com.cemenghui.course.service.CourseService;
import com.cemenghui.course.service.NotFoundException;
import com.cemenghui.course.service.AIService;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

/**
 * 课程服务实现
 */
@Service
public class CourseServiceImpl implements CourseService {
    private CourseDao courseRepo;
    private AIService aiService;

    /**
     * 获取所有课程列表
     * @return 课程列表
     */
    @Override
    public List<Course> listCourses() {
        // 获取所有课程逻辑
        return null;
    }

    /**
     * 根据关键词搜索课程
     * @param keyword 关键词
     * @return 课程列表
     */
    @Override
    public List<Course> searchCourses(String keyword) {
        // 搜索课程逻辑
        return null;
    }

    /**
     * 获取课程详情
     * @param courseId 课程ID
     * @return 课程详情
     * @throws NotFoundException 未找到课程时抛出
     */
    @Cacheable(value = "courseDetail", key = "#courseId")
    @Override
    public Course getCourseDetail(Long courseId) throws NotFoundException {
        // 获取课程详情逻辑
        // onCourseViewed 可在此处调用
        return null;
    }

    /**
     * 播放课程内容
     * @param courseId 课程ID
     * @return 流对象
     * @throws IOException IO异常
     */
    @Override
    public Stream playCourse(Long courseId) throws IOException {
        // 播放课程内容逻辑
        return null;
    }

    /**
     * 获取热搜词，调用AI
     * @return 热搜关键词列表
     */
    @Override
    public List<String> getHotSearchTrends() {
        // 获取热搜词逻辑
        return null;
    }

    /**
     * 课程浏览事件
     * @param userId 用户ID
     * @param courseId 课程ID
     */
    protected void onCourseViewed(Long userId, Long courseId) {
        // 记录用户浏览历史
    }
} 