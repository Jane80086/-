package com.cemenghui.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cemenghui.course.dao.CourseDao;
import com.cemenghui.course.dao.ReviewDao;
import com.cemenghui.course.entity.Review;
import com.cemenghui.course.entity.Course;
import com.cemenghui.course.entity.ReviewStatus;
import com.cemenghui.course.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 审核服务实现
 */
@Service
public class ReviewServiceImpl implements ReviewService {
    @Autowired
    private ReviewDao reviewDao;
    @Autowired
    private CourseDao courseDao;

    /**
     * 审核通过
     * @param courseId 课程ID
     * @param reviewerId 审核人ID
     * @return 审核记录
     */
    @Override
    public Review approveCourse(Long courseId, Long reviewerId) {
        Review review = new Review();
        review.setCourseId(courseId);
        review.setReviewerId(reviewerId);
        review.setStatus(ReviewStatus.APPROVED.name());
        reviewDao.insert(review);
        
        // 更新课程状态
        Course course = courseDao.selectById(courseId);
        if (course != null) {
            course.setStatus(1); // 1表示已发布
            courseDao.updateById(course);
        }
        
        onReviewApproved(courseId);
        return review;
    }

    /**
     * 审核驳回
     * @param courseId 课程ID
     * @param reason 驳回原因
     * @return 审核记录
     */
    @Override
    public Review rejectCourse(Long courseId, String reason) {
        Review review = new Review();
        review.setCourseId(courseId);
        review.setStatus(ReviewStatus.REJECTED.name());
        review.setComment(reason);
        reviewDao.insert(review);
        
        // 更新课程状态
        Course course = courseDao.selectById(courseId);
        if (course != null) {
            course.setStatus(2); // 2表示已拒绝
            courseDao.updateById(course);
        }
        
        onReviewRejected(courseId, reason);
        return review;
    }

    /**
     * 查询历史审核记录
     * @param courseId 课程ID
     * @return 审核记录列表
     */
    @Override
    public List<Review> getReviewLog(Long courseId) {
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Review::getCourseId, courseId);
        return reviewDao.selectList(wrapper);
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