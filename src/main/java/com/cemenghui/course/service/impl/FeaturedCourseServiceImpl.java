package com.cemenghui.course.service.impl;

import com.cemenghui.course.dao.FeaturedCourseDao;
import com.cemenghui.course.entity.FeaturedCourse;
import com.cemenghui.course.service.FeaturedCourseService;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

/**
 * 首页推荐课程服务实现
 */
@Service
public class FeaturedCourseServiceImpl implements FeaturedCourseService {
    private FeaturedCourseDao featuredRepo;

    /**
     * 设置课程推荐位
     * @param courseId 课程ID
     * @param priority 优先级
     * @return 是否设置成功
     */
    @Override
    public boolean promoteToFeatured(Long courseId, int priority) {
        onFeaturedPromoted(courseId);
        return false;
    }

    /**
     * 从首页撤销推荐
     * @param courseId 课程ID
     * @return 是否撤销成功
     */
    @Override
    public boolean removeFromFeatured(Long courseId) {
        onFeaturedRemoved(courseId);
        return false;
    }

    /**
     * 查看首页课程列表
     * @return 推荐课程列表
     */
    @Cacheable(value = "featuredCourses")
    @Override
    public List<FeaturedCourse> listFeaturedCourses() {
        return null;
    }

    /**
     * 推荐课程事件
     * @param courseId 课程ID
     */
    protected void onFeaturedPromoted(Long courseId) {
        // 展示首页课程
    }

    /**
     * 撤销课程事件
     * @param courseId 课程ID
     */
    protected void onFeaturedRemoved(Long courseId) {
        // 更新首页列表
    }
} 