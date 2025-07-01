package com.cemenghui.course.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cemenghui.course.entity.Review;
import org.apache.ibatis.annotations.Mapper;

/**
 * 审核记录数据访问接口
 */
@Mapper
public interface ReviewDao extends BaseMapper<Review> {
    // 使用MyBatis-Plus的QueryWrapper和LambdaQueryWrapper进行查询
    // 无需自定义SQL，BaseMapper提供的方法已足够
} 