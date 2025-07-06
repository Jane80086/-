package com.cemenghui.course.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cemenghui.course.entity.UserCourse;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserCourseDao extends BaseMapper<UserCourse> {
} 