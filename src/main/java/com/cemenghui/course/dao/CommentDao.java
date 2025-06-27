package com.cemenghui.course.dao;

import com.cemenghui.course.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentDao extends JpaRepository<Comment, Long> {
    Page<Comment> findByCourseId(Long courseId, Pageable pageable);
} 