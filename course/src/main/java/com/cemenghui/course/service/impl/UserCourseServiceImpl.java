package com.cemenghui.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cemenghui.course.dao.UserCourseDao;
import com.cemenghui.course.entity.UserCourse;
import com.cemenghui.course.service.UserCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserCourseServiceImpl implements UserCourseService {
    @Autowired
    private UserCourseDao userCourseDao;

    @Override
    public boolean purchaseCourse(Long userId, Long courseId) {
        UserCourse uc = new UserCourse();
        uc.setUserId(userId);
        uc.setCourseId(courseId);
        uc.setPurchaseTime(LocalDateTime.now());
        return userCourseDao.insert(uc) > 0;
    }

    @Override
    public boolean deletePurchasedCourse(Long userId, Long courseId) {
        QueryWrapper<UserCourse> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId).eq("course_id", courseId);
        return userCourseDao.delete(wrapper) > 0;
    }

    @Override
    public List<UserCourse> getPurchasedCourses(Long userId) {
        QueryWrapper<UserCourse> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        return userCourseDao.selectList(wrapper);
    }
} 