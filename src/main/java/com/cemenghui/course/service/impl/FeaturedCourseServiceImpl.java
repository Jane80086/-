package com.cemenghui.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cemenghui.course.dao.FeaturedCourseDao;
import com.cemenghui.course.entity.FeaturedCourse;
import com.cemenghui.course.service.FeaturedCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

/**
 * 首页推荐课程服务实现
 */
@Service
public class FeaturedCourseServiceImpl implements FeaturedCourseService {
    @Autowired
    private FeaturedCourseDao featuredCourseDao;

    /**
     * 设置课程推荐位
     * @param courseId 课程ID
     * @param priority 优先级
     * @return 是否设置成功
     */
    @Override
    public boolean promoteToFeatured(Long courseId, int priority) {
        // 检查课程是否存在
        if (featuredCourseDao.selectById(courseId) == null) {
            return false;
        }
        FeaturedCourse featuredCourse = new FeaturedCourse();
        featuredCourse.setCourseId(courseId);
        featuredCourse.setPriority(priority);
        featuredCourseDao.insert(featuredCourse);
        onFeaturedPromoted(courseId);
        return true;
    }

    /**
     * 从首页撤销推荐
     * @param courseId 课程ID
     * @return 是否撤销成功
     */
    @Override
    public boolean removeFromFeatured(Long courseId) {
        // 检查课程是否存在
        if (featuredCourseDao.selectById(courseId) == null) {
            return false;
        }
        LambdaQueryWrapper<FeaturedCourse> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FeaturedCourse::getCourseId, courseId);
        featuredCourseDao.delete(wrapper);
        onFeaturedRemoved(courseId);
        return true;
    }

    /**
     * 查看首页课程列表
     * @return 推荐课程列表
     */
    @Cacheable(value = "featuredCourses")
    @Override
    public List<FeaturedCourse> listFeaturedCourses() {
        LambdaQueryWrapper<FeaturedCourse> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(FeaturedCourse::getPriority);
        return featuredCourseDao.selectList(wrapper);
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