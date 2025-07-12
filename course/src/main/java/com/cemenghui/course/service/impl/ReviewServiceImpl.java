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
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    public Review approveCourse(Long courseId, Long reviewerId) {
        Course course = courseDao.selectById(courseId);
        if (course == null || !"PENDING".equals(course.getStatus())) {
            return null;
        }
        Review review = new Review();
        review.setCourseId(courseId);
        review.setReviewerId(reviewerId);
        // 查课程名
        review.setResourceName(course.getTitle());
        // 查审核人昵称
        String reviewerName = userService.getNicknameById(reviewerId);
        review.setReviewerName(reviewerName);
        review.setStatus(ReviewStatus.APPROVED.name());
        review.setReviewedAt(java.time.LocalDateTime.now());
        int insertCount = reviewDao.insert(review);
        System.out.println("审核记录插入结果: " + insertCount + ", 审核对象: " + review);
        // 更新课程状态
        course.setStatus("PUBLISHED");
        int updateCount = courseDao.updateById(course);
        System.out.println("课程状态更新结果: " + updateCount + ", 课程对象: " + course);
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
    @Transactional
    public Review rejectCourse(Long courseId, Long reviewerId, String reason) {
        Course course = courseDao.selectById(courseId);
        if (course == null || !"PENDING".equals(course.getStatus())) {
            return null;
        }
        Review review = new Review();
        review.setCourseId(courseId);
        review.setReviewerId(reviewerId);
        // 查课程名
        review.setResourceName(course.getTitle());
        // 查审核人昵称
        String reviewerName = userService.getNicknameById(reviewerId);
        review.setReviewerName(reviewerName);
        review.setStatus(ReviewStatus.REJECTED.name());
        review.setComment(reason);
        review.setReviewedAt(java.time.LocalDateTime.now());
        int insertCount = reviewDao.insert(review);
        System.out.println("审核记录插入结果: " + insertCount + ", 审核对象: " + review);
        // 更新课程状态
        course.setStatus("REJECTED");
        int updateCount = courseDao.updateById(course);
        System.out.println("课程状态更新结果: " + updateCount + ", 课程对象: " + course);
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