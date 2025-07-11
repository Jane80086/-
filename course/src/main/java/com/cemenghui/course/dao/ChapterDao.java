package com.cemenghui.course.dao;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cemenghui.course.entity.Chapter;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface ChapterDao extends BaseMapper<Chapter> {
    @Select("SELECT * FROM chapters WHERE course_id = #{courseId}")
    List<Chapter> findByCourseId(Long courseId);
} 