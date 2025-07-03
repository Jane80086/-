package com.cemenghui.course.service;

import com.cemenghui.course.entity.FeaturedCourse;
import java.util.List;

/**
 * 首页推荐课程服务接口
 */
public interface FeaturedCourseService {
    /**
     * 设置课程推荐位
     * @param courseId 课程ID
     * @param priority 优先级
     * @return 是否设置成功
     */
    boolean promoteToFeatured(Long courseId, int priority);

    /**
     * 从首页撤销推荐
     * @param courseId 课程ID
     * @return 是否撤销成功
     */
    boolean removeFromFeatured(Long courseId);

    /**
     * 查看首页课程列表
     * @return 推荐课程列表
     */
    List<FeaturedCourse> listFeaturedCourses();
}
