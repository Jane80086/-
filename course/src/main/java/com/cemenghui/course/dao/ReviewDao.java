package com.cemenghui.course.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cemenghui.course.entity.Review;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

/**
 * 审核记录数据访问接口
 * 对应新的audit_records表结构
 */
@Mapper
public interface ReviewDao extends BaseMapper<Review> {
    
    /**
     * 根据课程ID查找审核记录
     */
    @Select("SELECT * FROM audit_records WHERE resource_type = 'COURSE' AND resource_id = #{courseId} ORDER BY audit_time DESC")
    List<Review> findByCourseId(@Param("courseId") Long courseId);
    
    /**
     * 根据审核人ID查找审核记录
     */
    @Select("SELECT * FROM audit_records WHERE resource_type = 'COURSE' AND reviewer_id = #{reviewerId} ORDER BY audit_time DESC")
    List<Review> findByReviewerId(@Param("reviewerId") Long reviewerId);
    
    /**
     * 查找待审核的课程
     */
    @Select("SELECT * FROM audit_records WHERE resource_type = 'COURSE' AND status = 'PENDING' ORDER BY create_time ASC")
    List<Review> findPendingReviews();
    
    /**
     * 查找已通过的审核记录
     */
    @Select("SELECT * FROM audit_records WHERE resource_type = 'COURSE' AND status = 'APPROVED' ORDER BY audit_time DESC")
    List<Review> findApprovedReviews();
    
    /**
     * 查找已拒绝的审核记录
     */
    @Select("SELECT * FROM audit_records WHERE resource_type = 'COURSE' AND status = 'REJECTED' ORDER BY audit_time DESC")
    List<Review> findRejectedReviews();
} 