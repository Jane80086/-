package com.cemenghui.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cemenghui.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

/**
 * 统一用户数据访问接口
 * 对应新的users表结构
 */
@Mapper
public interface UserDao extends BaseMapper<User> {
    
    /**
     * 根据用户名查找用户
     */
    @Select("SELECT * FROM users WHERE username = #{username} AND deleted = 0")
    User findByUsername(@Param("username") String username);
    
    /**
     * 根据用户类型查找用户列表
     */
    @Select("SELECT * FROM users WHERE user_type = #{userType} AND deleted = 0")
    List<User> findByUserType(@Param("userType") String userType);
    
    /**
     * 根据企业ID查找企业用户
     */
    @Select("SELECT * FROM users WHERE enterprise_id = #{enterpriseId} AND deleted = 0")
    List<User> findByEnterpriseId(@Param("enterpriseId") String enterpriseId);
    
    /**
     * 根据邮箱查找用户
     */
    @Select("SELECT * FROM users WHERE email = #{email} AND deleted = 0")
    User findByEmail(@Param("email") String email);
    
    /**
     * 根据手机号查找用户
     */
    @Select("SELECT * FROM users WHERE phone = #{phone} AND deleted = 0")
    User findByPhone(@Param("phone") String phone);
    
    /**
     * 查找所有启用的用户
     */
    @Select("SELECT * FROM users WHERE status = 1 AND deleted = 0")
    List<User> findEnabledUsers();
    
    /**
     * 查找所有禁用的用户
     */
    @Select("SELECT * FROM users WHERE status = 0 AND deleted = 0")
    List<User> findDisabledUsers();
} 