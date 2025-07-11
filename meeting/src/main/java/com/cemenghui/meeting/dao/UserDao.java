package com.cemenghui.meeting.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cemenghui.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserDao extends BaseMapper<User> {
    
    @Insert("INSERT INTO user_info (username, password, real_name, email, phone, user_type, status, create_time, update_time, deleted) " +
            "VALUES (#{username}, #{password}, #{realName}, #{email}, #{phone}, #{userType}, #{status}, NOW(), NOW(), 0)")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);
    
    @Select("SELECT * FROM users WHERE id = #{id} AND status = 1 AND deleted = 0")
    User findById(Long id);
    
    /**
     * 根据用户名查找用户
     */
    @Select("SELECT * FROM users WHERE username = #{username} AND status = 1 AND deleted = 0")
    User findByUsername(String username);
    
    @Update("UPDATE users SET real_name = #{realName}, email = #{email}, phone = #{phone}, update_time = NOW() " +
            "WHERE id = #{id}")
    int updateBasicInfo(User user);
    
    @Update("UPDATE users SET password = #{password}, update_time = NOW() WHERE id = #{id}")
    int updatePassword(@Param("id") Long id, @Param("password") String password);
    
    @Update("UPDATE users SET status = #{status}, update_time = NOW() WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
    
    /**
     * 查找所有启用的用户
     */
    @Select("SELECT * FROM users WHERE status = 1 AND deleted = 0 ORDER BY create_time DESC")
    List<User> findAllActive();
    
    /**
     * 根据用户类型查找用户
     */
    @Select("SELECT * FROM users WHERE user_type = #{userType} AND status = 1 AND deleted = 0 ORDER BY create_time DESC")
    List<User> findByUserType(String userType);
    
    @Delete("DELETE FROM users WHERE id = #{id}")
    int deleteById(Long id);
} 