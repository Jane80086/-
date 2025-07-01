package com.cemenghui.course.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cemenghui.course.entity.CourseHistory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 课程历史记录数据访问接口
 */
@Mapper
public interface CourseHistoryDao extends BaseMapper<CourseHistory> {
    // 使用MyBatis-Plus的QueryWrapper和LambdaQueryWrapper进行查询
    // 无需自定义SQL，BaseMapper提供的方法已足够
} 