package com.cemenghui.course.service;

import com.cemenghui.course.entity.Course;

/**
 * 企业课程管理服务接口
 */
public interface CourseManagerService {
    /**
     * 创建新课程
     * @param course 课程对象
     * @return 创建后的课程
     */
    Course createCourse(Course course);

    /**
     * 编辑课程信息
     * @param id 课程ID
     * @param updated 更新后的课程对象
     * @return 编辑后的课程
     * @throws NotFoundException 未找到课程时抛出
     */
    Course editCourse(Long id, Course updated) throws NotFoundException;

    /**
     * 删除课程
     * @param courseId 课程ID
     * @return 是否删除成功
     */
    boolean deleteCourse(Long courseId);

    /**
     * 提交审核请求
     * @param courseId 课程ID
     * @return 是否提交成功
     */
    boolean submitForReview(Long courseId);

    /**
     * 使用AI优化课程信息
     * @param course 课程对象
     * @return 优化后的课程
     */
    Course optimizeCourseInfo(Course course) throws AIException;

    /**
     * 下架课程
     * @param courseId 课程ID
     * @return 是否下架成功
     */
    boolean unpublishCourse(Long courseId);
} 