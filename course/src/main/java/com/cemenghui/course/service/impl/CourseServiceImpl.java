package com.cemenghui.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cemenghui.course.dao.CourseDao;
import com.cemenghui.course.dao.CommentDao;
import com.cemenghui.course.dao.CourseReviewHistoryDao;
import com.cemenghui.course.entity.Course;
import com.cemenghui.course.entity.Comment;
import com.cemenghui.course.entity.CourseReviewHistory;
import com.cemenghui.course.service.CourseService;
import com.cemenghui.course.service.NotFoundException;
import com.cemenghui.course.service.AIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

/**
 * 课程服务实现
 */
@Service
public class CourseServiceImpl implements CourseService {
    
    @Autowired
    private CourseDao courseDao;
    
    @Autowired
    private AIService aiService;
    
    @Autowired
    private CommentDao commentDao;

    @Autowired
    private CourseReviewHistoryDao reviewHistoryDao;

    /**
     * 获取所有课程列表
     * @return 课程列表
     */
    @Override
    public List<Course> listCourses() {
        return courseDao.selectList(null);
    }

    /**
     * 根据关键词搜索课程
     * @param keyword 关键词
     * @return 课程列表
     */
    @Override
    public List<Course> searchCourses(String keyword) {
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(Course::getTitle, keyword);
        return courseDao.selectList(wrapper);
    }

    /**
     * 获取课程详情
     * @param courseId 课程ID
     * @return 课程详情
     * @throws NotFoundException 未找到课程时抛出
     */
    @Cacheable(value = "courseDetail", key = "#courseId")
    @Override
    public Course getCourseDetail(Long courseId) throws NotFoundException {
        Course course = courseDao.selectById(courseId);
        if (course == null) {
            throw new NotFoundException("课程不存在: " + courseId);
        }
        return course;
    }

    /**
     * 播放课程内容
     * @param courseId 课程ID
     * @return 流对象
     * @throws IOException IO异常
     */
    @Override
    public Stream playCourse(Long courseId) throws IOException {
        // 播放课程内容逻辑
        return null;
    }

    /**
     * 获取热搜词，调用AI
     * @return 热搜关键词列表
     */
    @Override
    public List<String> getHotSearchTrends() {
        try {
            return aiService.getHotSearchTrends();
        } catch (Exception e) {
            // 如果AI服务不可用，返回默认热搜词
            return List.of("Java", "Python", "Spring Boot", "数据库", "前端开发");
        }
    }

    /**
     * 课程浏览事件
     * @param userId 用户ID
     * @param courseId 课程ID
     */
    protected void onCourseViewed(Long userId, Long courseId) {
        // 记录用户浏览历史
    }

    @Override
    @Transactional
    public boolean favoriteCourse(Long courseId, Long userId) {
        Course course = courseDao.selectById(courseId);
        if (course == null) return false;
        if (course.getFavoriteCount() == null) course.setFavoriteCount(0);
        course.setFavoriteCount(course.getFavoriteCount() + 1); // 简化：不做用户去重
        courseDao.updateById(course);
        return true;
    }

    @Override
    @Transactional
    public boolean likeCourse(Long courseId, Long userId) {
        Course course = courseDao.selectById(courseId);
        if (course == null) return false;
        if (course.getLikeCount() == null) course.setLikeCount(0);
        course.setLikeCount(course.getLikeCount() + 1);
        courseDao.updateById(course);
        return true;
    }

    @Override
    @Transactional
    public boolean commentCourse(Long courseId, Long userId, String content) {
        Comment comment = new Comment();
        comment.setCourseId(courseId);
        comment.setUserId(userId);
        comment.setContent(content);
        commentDao.insert(comment);
        return true;
    }

    @Override
    public IPage<Comment> getCourseComments(Long courseId, Page<Comment> page) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getCourseId, courseId)
               .orderByDesc(Comment::getCreateTime);
        return commentDao.selectPage(page, wrapper);
    }

    @Override
    @Transactional
    public boolean submitCourseForReview(Long courseId) {
        Course course = courseDao.selectById(courseId);
        if (course == null) {
            System.err.println("提交审核失败：课程不存在，id=" + courseId);
            return false;
        }
        if (!"DRAFT".equals(course.getStatus())) { // 草稿状态
            System.err.println("提交审核失败：课程状态不是DRAFT，当前状态=" + course.getStatus());
            return false;
        }
        course.setStatus("PENDING"); // 待审核
        course.setUpdateTime(java.time.LocalDateTime.now());
        courseDao.updateById(course);
        return true;
    }

    @Override
    @Transactional
    public boolean approveCourse(Long courseId, Long adminId) {
        Course course = courseDao.selectById(courseId);
        if (course == null) {
            System.err.println("审核通过失败：课程不存在，id=" + courseId);
            return false;
        }
        if (!"PENDING".equals(course.getStatus())) { // 待审核状态
            System.err.println("审核通过失败：课程状态不是PENDING，当前状态=" + course.getStatus());
            return false;
        }
        course.setStatus("PUBLISHED"); // 已审核通过
        course.setUpdateTime(java.time.LocalDateTime.now());
        courseDao.updateById(course);
        // 记录审核历史
        CourseReviewHistory history = new CourseReviewHistory();
        history.setCourseId(courseId);
        history.setAdminId(adminId);
        history.setAction("APPROVED");
        history.setReviewTime(java.time.LocalDateTime.now());
        reviewHistoryDao.insert(history);
        return true;
    }

    @Override
    @Transactional
    public boolean rejectCourse(Long courseId, Long adminId, String reason) {
        Course course = courseDao.selectById(courseId);
        if (course == null) {
            System.err.println("审核拒绝失败：课程不存在，id=" + courseId);
            return false;
        }
        if (!"PENDING".equals(course.getStatus())) { // 待审核状态
            System.err.println("审核拒绝失败：课程状态不是PENDING，当前状态=" + course.getStatus());
            return false;
        }
        course.setStatus("REJECTED"); // 已拒绝
        course.setUpdateTime(java.time.LocalDateTime.now());
        courseDao.updateById(course);
        // 记录审核历史
        CourseReviewHistory history = new CourseReviewHistory();
        history.setCourseId(courseId);
        history.setAdminId(adminId);
        history.setAction("REJECTED");
        history.setReason(reason);
        history.setReviewTime(java.time.LocalDateTime.now());
        reviewHistoryDao.insert(history);
        return true;
    }

    @Override
    public List<Course> getPendingCourses() {
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Course::getStatus, "PENDING"); // 0表示待审核状态
        wrapper.orderByDesc(Course::getCreateTime);
        return courseDao.selectList(wrapper);
    }
} 