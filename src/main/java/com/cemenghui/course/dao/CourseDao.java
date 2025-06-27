package com.cemenghui.course.dao;

import com.cemenghui.course.entity.Course;
import com.cemenghui.course.entity.CourseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * 课程数据访问接口
 */
@Repository
public interface CourseDao extends JpaRepository<Course, Long> {
    /**
     * 模糊搜索课程标题
     * @param keyword 关键词
     * @return 课程列表
     */
    List<Course> findByTitleContaining(String keyword);

    /**
     * 查询某个企业用户的课程
     * @param instructorId 用户ID
     * @return 课程列表
     */
    List<Course> findByInstructorId(Long instructorId);

    /**
     * 根据状态查询课程
     * @param status 课程状态
     * @return 课程列表
     */
    List<Course> findByStatus(CourseStatus status);
}
