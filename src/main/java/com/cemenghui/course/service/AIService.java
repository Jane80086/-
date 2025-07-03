package com.cemenghui.course.service;

import com.cemenghui.course.entity.Course;
import com.cemenghui.course.entity.Question;

import java.util.List;

/**
 * AI服务接口
 */
public interface AIService {
    /**
     * 优化课程信息（标题、简介等）
     * @param course 课程对象
     * @return 优化后的课程
     * @throws AIException AI相关异常
     */
    Course optimizeCourseInfo(Course course) throws AIException;

    /**
     * 获取热门关键词列表
     * @return 热门关键词
     * @throws AIException AI相关异常
     */
    List<String> getHotSearchTrends() throws AIException;

    /**
     * 返回AI生成的问答回复内容
     * @param question 问题对象
     * @return AI回复
     * @throws AIException AI相关异常
     */
    String autoReply(Question question) throws AIException;

    /**
     * 为课程打分用于推荐排序
     * @param courseId 课程ID
     * @return 课程分数
     * @throws AIException AI相关异常
     */
    Double scoreCourse(Long courseId) throws AIException;
} 