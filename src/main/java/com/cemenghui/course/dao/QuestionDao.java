package com.cemenghui.course.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cemenghui.course.entity.Question;
import org.apache.ibatis.annotations.Mapper;

/**
 * 问答数据访问接口
 */
@Mapper
public interface QuestionDao extends BaseMapper<Question> {
    // 使用MyBatis-Plus的QueryWrapper和LambdaQueryWrapper进行查询
    // 无需自定义SQL，BaseMapper提供的方法已足够
} 