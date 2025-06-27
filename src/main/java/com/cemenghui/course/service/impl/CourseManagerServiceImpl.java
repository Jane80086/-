package com.cemenghui.course.service.impl;

import com.cemenghui.course.dao.CourseDao;
import com.cemenghui.course.entity.Course;
import com.cemenghui.course.service.AIService;
import com.cemenghui.course.service.NotFoundException;
import com.cemenghui.course.service.AIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 企业课程管理服务实现
 */
@Service
public class CourseManagerServiceImpl implements com.cemenghui.course.service.CourseManagerService {
    @Autowired
    private CourseDao courseRepo;
    @Autowired
    private AIService aiService;
    @Autowired
    private MCPServiceImpl mcpServiceImpl;

    /**
     * 创建新课程
     * @param course 课程对象
     * @return 创建后的课程
     */
    @Override
    public Course createCourse(Course course) {
        return courseRepo.save(course);
    }

    /**
     * 编辑课程信息
     * @param id 课程ID
     * @param updated 更新后的课程对象
     * @return 编辑后的课程
     * @throws NotFoundException 未找到课程时抛出
     */
    @Override
    public Course editCourse(Long id, Course updated) throws NotFoundException {
        Optional<Course> optional = courseRepo.findById(id);
        if (!optional.isPresent()) {
            throw new NotFoundException("课程未找到: " + id);
        }
        Course course = optional.get();
        course.edit(updated.getTitle(), updated.getDescription(), updated.getCoverImage());
        return courseRepo.save(course);
    }

    /**
     * 删除课程
     * @param courseId 课程ID
     * @return 是否删除成功
     */
    @Override
    public boolean deleteCourse(Long courseId) {
        if (!courseRepo.existsById(courseId)) {
            return false;
        }
        courseRepo.deleteById(courseId);
        return true;
    }

    /**
     * 提交审核请求
     * @param courseId 课程ID
     * @return 是否提交成功
     */
    @Override
    public boolean submitForReview(Long courseId) {
        Optional<Course> optional = courseRepo.findById(courseId);
        if (!optional.isPresent()) {
            return false;
        }
        Course course = optional.get();
        try {
            course.submitForReview();
            courseRepo.save(course);
            onCourseSubmitted(courseId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 使用AI优化课程信息
     * @param course 课程对象
     * @return 优化后的课程
     */
    @Override
    public Course optimizeCourseInfo(Course course) throws AIException {
        try {
            // 使用MCP服务优化课程内容
            MCPServiceImpl.CourseOptimizationResult result = mcpServiceImpl.optimizeCourseContent(
                course, 
                "初学者", // 可以从课程属性或参数中获取
                "技术"    // 可以从课程属性或参数中获取
            );
            
            // 应用优化结果
            if (result != null) {
                course.setTitle(result.getOptimizedTitle());
                course.setDescription(result.getOptimizedDescription());
            }
            
            return course;
        } catch (Exception e) {
            throw new AIException("课程优化失败: " + e.getMessage());
        }
    }

    /**
     * 使用MCP优化课程内容（新增方法）
     * @param courseId 课程ID
     * @param targetAudience 目标受众
     * @param courseType 课程类型
     * @return 优化结果
     * @throws NotFoundException 课程未找到时抛出
     */
    public MCPServiceImpl.CourseOptimizationResult optimizeCourseWithMCP(Long courseId, String targetAudience, String courseType) throws NotFoundException {
        Optional<Course> optional = courseRepo.findById(courseId);
        if (!optional.isPresent()) {
            throw new NotFoundException("课程未找到: " + courseId);
        }
        
        Course course = optional.get();
        return mcpServiceImpl.optimizeCourseContent(course, targetAudience, courseType);
    }

    /**
     * 分析课程SEO效果（新增方法）
     * @param title 课程标题
     * @param description 课程描述
     * @return SEO分析结果
     */
    public MCPServiceImpl.SEOAnalysisResult analyzeCourseSEO(String title, String description) {
        return mcpServiceImpl.analyzeCourseSEO(title, description);
    }

    /**
     * 生成课程模板（新增方法）
     * @param courseType 课程类型
     * @param targetAudience 目标受众
     * @return 课程模板
     */
    public MCPServiceImpl.CourseTemplateResult generateCourseTemplates(String courseType, String targetAudience) {
        return mcpServiceImpl.generateCourseTemplates(courseType, targetAudience);
    }

    /**
     * 课程提交审核事件
     * @param courseId 课程ID
     */
    protected void onCourseSubmitted(Long courseId) {
        // 通知审核模块，可扩展事件发布
    }
}
