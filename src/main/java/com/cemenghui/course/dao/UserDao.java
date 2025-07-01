package com.cemenghui.course.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cemenghui.course.common.User;
import com.cemenghui.course.common.AdminUser;
import com.cemenghui.course.common.EnterpriseUser;
import com.cemenghui.course.common.NormalUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户数据访问接口
 */
@Mapper
public interface UserDao extends BaseMapper<User> {
    // 使用MyBatis-Plus的QueryWrapper和LambdaQueryWrapper进行查询
    // 无需自定义SQL，BaseMapper提供的方法已足够

    // 查找所有管理员用户
    // 查找所有企业用户
    // 查找所有普通用户
} 