package com.cemenghui.course.dao;

import com.cemenghui.course.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * 审核记录数据访问接口
 */
@Repository
public interface ReviewDao extends JpaRepository<Review, Long> {
    /**
     * 获取某课程的审核记录
     * @param courseId 课程ID
     * @return 审核记录列表
     */
    List<Review> findByCourseId(Long courseId);

    /**
     * 获取管理员审核的课程记录
     * @param adminId 管理员ID
     * @return 审核记录列表
     */
    List<Review> findByReviewerId(Long adminId);
} 