package com.cemenghui.course.service;

import com.cemenghui.course.entity.Review;
import java.util.List;

/**
 * 审核服务接口
 */
public interface ReviewService {
    /**
     * 审核通过
     * @param courseId 课程ID
     * @param reviewerId 审核人ID
     * @return 审核记录
     */
    Review approveCourse(Long courseId, Long reviewerId);

    /**
     * 审核驳回
     * @param courseId 课程ID
     * @param reviewerId 审核人ID
     * @param reason 驳回原因
     * @return 审核记录
     */
    Review rejectCourse(Long courseId, Long reviewerId, String reason);

    /**
     * 查询历史审核记录
     * @param courseId 课程ID
     * @return 审核记录列表
     */
    List<Review> getReviewLog(Long courseId);
} 