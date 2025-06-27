package com.cemenghui.course.service;

import com.cemenghui.course.entity.Course;
import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.cemenghui.course.entity.Comment;

/**
 * 课程服务接口
 */
public interface CourseService {
    /**
     * 获取所有课程列表
     * @return 课程列表
     */
    List<Course> listCourses();

    /**
     * 根据关键词搜索课程
     * @param keyword 关键词
     * @return 课程列表
     */
    List<Course> searchCourses(String keyword);

    /**
     * 获取课程详情
     * @param courseId 课程ID
     * @return 课程详情
     * @throws NotFoundException 未找到课程时抛出
     */
    Course getCourseDetail(Long courseId) throws NotFoundException;

    /**
     * 播放课程内容
     * @param courseId 课程ID
     * @return 流对象
     * @throws IOException IO异常
     */
    Stream playCourse(Long courseId) throws IOException;

    /**
     * 获取热搜词
     * @return 热搜关键词列表
     */
    List<String> getHotSearchTrends();

    /**
     * 收藏/取消收藏
     */
    boolean favoriteCourse(Long courseId, Long userId);

    /**
     * 点赞/取消点赞
     */
    boolean likeCourse(Long courseId, Long userId);

    /**
     * 发表评论
     */
    boolean commentCourse(Long courseId, Long userId, String content);

    /**
     * 获取课程评论分页
     */
    Page<Comment> getCourseComments(Long courseId, Pageable pageable);
} 