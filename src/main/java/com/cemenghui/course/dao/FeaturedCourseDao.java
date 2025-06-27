package com.cemenghui.course.dao;

import com.cemenghui.course.entity.FeaturedCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 * 首页推荐课程数据访问接口
 */
@Repository
public interface FeaturedCourseDao extends JpaRepository<FeaturedCourse, Long> {
    /**
     * 获取首页推荐课程，按优先级升序排序
     * @return 推荐课程列表
     */
    List<FeaturedCourse> findAllByOrderByPriorityAsc();

    /**
     * 移除某课程的推荐记录
     * @param courseId 课程ID
     */
    void deleteByCourseId(Long courseId);
} 