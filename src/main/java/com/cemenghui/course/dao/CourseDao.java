package com.cemenghui.course.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cemenghui.course.entity.Course;
import com.cemenghui.course.entity.CourseStatus;
import org.apache.ibatis.annotations.Mapper;

/**
 * 课程数据访问接口
 */
@Mapper
public interface CourseDao extends BaseMapper<Course> {
    // 使用MyBatis-Plus的QueryWrapper和LambdaQueryWrapper进行查询
    // 无需自定义SQL，BaseMapper提供的方法已足够
}
