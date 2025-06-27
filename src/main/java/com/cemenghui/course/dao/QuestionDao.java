package com.cemenghui.course.dao;

import com.cemenghui.course.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * 问答数据访问接口
 */
@Repository
public interface QuestionDao extends JpaRepository<Question, Long> {
    /**
     * 获取某课程的所有问答
     * @param courseId 课程ID
     * @return 问答列表
     */
    List<Question> findByCourseId(Long courseId);

    /**
     * 获取某用户的所有提问
     * @param userId 用户ID
     * @return 问答列表
     */
    List<Question> findByUserId(Long userId);
} 