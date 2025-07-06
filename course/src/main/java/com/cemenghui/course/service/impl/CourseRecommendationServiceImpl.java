package com.cemenghui.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cemenghui.course.dao.CourseDao;
import com.cemenghui.course.dao.UserCourseDao;
import com.cemenghui.course.entity.Course;
import com.cemenghui.course.entity.UserCourse;
import com.cemenghui.course.service.CourseRecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 课程推荐服务
 * 集成MCP服务器提供智能推荐功能，同时提供本地备用实现
 */
@Service
public class CourseRecommendationServiceImpl implements CourseRecommendationService {

    @Autowired
    private CourseDao courseDao;

    @Autowired
    private UserCourseDao userCourseDao;

    @Override
    public List<Course> recommendCoursesForUser(Long userId, int limit) {
        // 1. 获取用户已购课程的分类
        List<Long> purchasedCourseIds = userCourseDao.selectList(
            new QueryWrapper<UserCourse>().eq("user_id", userId)
        ).stream().map(UserCourse::getCourseId).collect(Collectors.toList());

        Set<String> userCategories = new HashSet<>();
        if (!purchasedCourseIds.isEmpty()) {
            List<Course> purchasedCourses = courseDao.selectBatchIds(purchasedCourseIds);
            for (Course c : purchasedCourses) {
                if (c.getCategory() != null) userCategories.add(c.getCategory());
            }
        }

        // 2. 推荐同类热门课程
        List<Course> interestCourses = new ArrayList<>();
        if (!userCategories.isEmpty()) {
            QueryWrapper<Course> wrapper = new QueryWrapper<>();
            wrapper.in("category", userCategories).orderByDesc("like_count");
            interestCourses = courseDao.selectList(wrapper).stream().limit(limit).collect(Collectors.toList());
        }

        // 3. 补充热门课程
        QueryWrapper<Course> hotWrapper = new QueryWrapper<>();
        hotWrapper.orderByDesc("like_count");
        List<Course> hotCourses = courseDao.selectList(hotWrapper).stream().limit(limit).collect(Collectors.toList());

        // 4. 补充最新课程
        QueryWrapper<Course> newWrapper = new QueryWrapper<>();
        newWrapper.orderByDesc("create_time");
        List<Course> newCourses = courseDao.selectList(newWrapper).stream().limit(limit).collect(Collectors.toList());

        // 5. 合并去重
        LinkedHashSet<Course> result = new LinkedHashSet<>();
        result.addAll(interestCourses);
        result.addAll(hotCourses);
        result.addAll(newCourses);

        // 6. 返回前limit个
        return result.stream().limit(limit).collect(Collectors.toList());
    }
} 