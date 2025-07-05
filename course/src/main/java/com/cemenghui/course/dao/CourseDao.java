package com.cemenghui.course.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cemenghui.course.entity.Course;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

/**
 * 课程数据访问接口
 * 对应新的courses表结构
 */
@Mapper
public interface CourseDao extends BaseMapper<Course> {
    
    /**
     * 根据讲师ID查找课程
     */
    @Select("SELECT * FROM courses WHERE instructor_id = #{instructorId} AND deleted = 0")
    List<Course> findByInstructorId(@Param("instructorId") Long instructorId);
    
    /**
     * 根据状态查找课程
     */
    @Select("SELECT * FROM courses WHERE status = #{status} AND deleted = 0")
    List<Course> findByStatus(@Param("status") String status);
    
    /**
     * 根据分类查找课程
     */
    @Select("SELECT * FROM courses WHERE category = #{category} AND deleted = 0")
    List<Course> findByCategory(@Param("category") String category);
    
    /**
     * 根据课程级别查找课程
     */
    @Select("SELECT * FROM courses WHERE course_level = #{level} AND deleted = 0")
    List<Course> findByLevel(@Param("level") String level);
    
    /**
     * 查找已发布的课程
     */
    @Select("SELECT * FROM courses WHERE status = 'PUBLISHED' AND deleted = 0")
    List<Course> findPublishedCourses();
    
    /**
     * 查找待审核的课程
     */
    @Select("SELECT * FROM courses WHERE status = 'PENDING' AND deleted = 0")
    List<Course> findPendingCourses();
    
    /**
     * 查找草稿状态的课程
     */
    @Select("SELECT * FROM courses WHERE status = 'DRAFT' AND deleted = 0")
    List<Course> findDraftCourses();
    
    /**
     * 根据标题模糊查询课程
     */
    @Select("SELECT * FROM courses WHERE title LIKE CONCAT('%', #{title}, '%') AND deleted = 0")
    List<Course> findByTitleLike(@Param("title") String title);
}
