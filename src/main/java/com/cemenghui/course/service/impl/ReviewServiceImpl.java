package com.cemenghui.course.service.impl;

import com.cemenghui.course.dao.CourseDao;
import com.cemenghui.course.dao.ReviewDao;
import com.cemenghui.course.entity.Review;
import com.cemenghui.course.service.ReviewService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 审核服务实现
 */
@Service
public class ReviewServiceImpl implements ReviewService {
    private ReviewDao reviewRepo;
    private CourseDao courseRepo;

    /**
     * 审核通过
     * @param courseId 课程ID
     * @param reviewerId 审核人ID
     * @return 审核记录
     */
    @Override
    public Review approveCourse(Long courseId, Long reviewerId) {
        // 审核通过逻辑
        onReviewApproved(courseId);
        return null;
    }

    /**
     * 审核驳回
     * @param courseId 课程ID
     * @param reason 驳回原因
     * @return 审核记录
     */
    @Override
    public Review rejectCourse(Long courseId, String reason) {
        // 审核驳回逻辑
        onReviewRejected(courseId, reason);
        return null;
    }

    /**
     * 查询历史审核记录
     * @param courseId 课程ID
     * @return 审核记录列表
     */
    @Override
    public List<Review> getReviewLog(Long courseId) {
        // 查询审核记录逻辑
        return null;
    }

    /**
     * 审核通过事件
     * @param courseId 课程ID
     */
    protected void onReviewApproved(Long courseId) {
        // 通知企业用户审核通过
    }

    /**
     * 审核驳回事件
     * @param courseId 课程ID
     * @param reason 驳回原因
     */
    protected void onReviewRejected(Long courseId, String reason) {
        // 反馈驳回原因
    }
} 