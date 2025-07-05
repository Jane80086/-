package com.cemenghui.course.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cemenghui.entity.User; // main-app
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserDao extends BaseMapper<User> {
    // 只保留必要方法，全部基于users表
} 