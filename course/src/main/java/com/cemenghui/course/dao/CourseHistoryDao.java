package com.cemenghui.course.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cemenghui.course.entity.CourseHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

/**
 * 课程历史记录数据访问接口
 * 对应新的history_records表结构
 */
@Mapper
public interface CourseHistoryDao extends BaseMapper<CourseHistory> {
    
    /**
     * 根据课程ID查找历史记录
     */
    @Select("SELECT * FROM history_records WHERE resource_type = 'COURSE' AND resource_id = #{courseId} ORDER BY record_time DESC")
    List<CourseHistory> findByCourseId(@Param("courseId") Long courseId);
    
    /**
     * 根据用户ID查找历史记录
     */
    @Select("SELECT * FROM history_records WHERE resource_type = 'COURSE' AND user_id = #{userId} ORDER BY record_time DESC")
    List<CourseHistory> findByUserId(@Param("userId") Long userId);
    
    /**
     * 查找用户查看课程的历史记录
     */
    @Select("SELECT * FROM history_records WHERE resource_type = 'COURSE' AND action = 'VIEW' AND user_id = #{userId} ORDER BY record_time DESC")
    List<CourseHistory> findViewHistoryByUserId(@Param("userId") Long userId);
    
    /**
     * 查找课程的所有查看记录
     */
    @Select("SELECT * FROM history_records WHERE resource_type = 'COURSE' AND action = 'VIEW' AND resource_id = #{courseId} ORDER BY record_time DESC")
    List<CourseHistory> findViewHistoryByCourseId(@Param("courseId") Long courseId);
} 