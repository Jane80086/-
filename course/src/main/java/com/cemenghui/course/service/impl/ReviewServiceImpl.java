package com.cemenghui.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cemenghui.course.dao.CourseDao;
import com.cemenghui.course.dao.ReviewDao;
import com.cemenghui.course.entity.Review;
import com.cemenghui.course.entity.Course;
import com.cemenghui.course.entity.ReviewStatus;
import com.cemenghui.course.service.ReviewService;
import com.cemenghui.course.service.UserService;
import com.cemenghui.entity.User;
import com.cemenghui.course.vo.CoursePublishRecordVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 审核服务实现
 */
@Service
public class ReviewServiceImpl implements ReviewService {
    @Autowired
    private ReviewDao reviewDao;
    @Autowired
    private CourseDao courseDao;
    @Autowired
    private UserService userService;

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
        // 查课程名
        Course course = courseDao.selectById(courseId);
        if (course != null) {
            review.setResourceName(course.getTitle());
        }
        // 查审核人昵称
        String reviewerName = userService.getNicknameById(reviewerId);
        review.setReviewerName(reviewerName);
        review.setStatus(ReviewStatus.APPROVED.name());
        review.setReviewedAt(java.time.LocalDateTime.now());
        reviewDao.insert(review);
        // 更新课程状态
        if (course != null) {
            course.setStatus("PUBLISHED");
            courseDao.updateById(course);
        }
        onReviewApproved(courseId);
        return review;
    }

    /**
     * 审核驳回
     * @param courseId 课程ID
     * @param reviewerId 审核人ID
     * @param reason 驳回原因
     * @return 审核记录
     */
    @Override
    public Review rejectCourse(Long courseId, Long reviewerId, String reason) {
        Review review = new Review();
        review.setCourseId(courseId);
        review.setReviewerId(reviewerId);
        // 查课程名
        Course course = courseDao.selectById(courseId);
        if (course != null) {
            review.setResourceName(course.getTitle());
        }
        // 查审核人昵称
        String reviewerName = userService.getNicknameById(reviewerId);
        review.setReviewerName(reviewerName);
        review.setStatus(ReviewStatus.REJECTED.name());
        review.setComment(reason);
        review.setReviewedAt(java.time.LocalDateTime.now());
        reviewDao.insert(review);
        // 更新课程状态
        if (course != null) {
            course.setStatus("REJECTED");
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
    public List<CoursePublishRecordVO> getReviewLog(Long courseId) {
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Review::getCourseId, courseId);
        List<Review> reviews = reviewDao.selectList(wrapper);
        return reviews.stream().map(r -> {
            CoursePublishRecordVO vo = new CoursePublishRecordVO();
            vo.setCourseName(r.getResourceName());
            vo.setPublishTime(r.getReviewedAt() != null ? r.getReviewedAt().toString() : "");
            vo.setReviewer(r.getReviewerName());
            String status = r.getStatus();
            vo.setReviewResult("审核中");
            if ("APPROVED".equalsIgnoreCase(status)) vo.setReviewResult("通过");
            else if ("REJECTED".equalsIgnoreCase(status)) vo.setReviewResult("未通过");
            vo.setRemark(r.getComment());
            return vo;
        }).collect(Collectors.toList());
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