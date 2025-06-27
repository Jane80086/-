package com.cemenghui.course.dao;

import com.cemenghui.course.entity.CourseHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 * 课程历史记录数据访问接口
 */
@Repository
public interface CourseHistoryDao extends JpaRepository<CourseHistory, Long> {
    /**
     * 查询用户历史记录，按时间降序
     * @param userId 用户ID
     * @return 历史记录列表
     */
    List<CourseHistory> findByUserIdOrderByViewedAtDesc(Long userId);

    /**
     * 清除用户所有历史记录
     * @param userId 用户ID
     */
    void deleteByUserId(Long userId);
} 