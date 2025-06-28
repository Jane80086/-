package com.cemenghui.meeting.dao;

import com.cemenghui.meeting.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserDao {
    
    @Insert("INSERT INTO user_info (username, password, real_name, email, phone, user_type, status) " +
            "VALUES (#{username}, #{password}, #{realName}, #{email}, #{phone}, #{userType}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);
    
    @Select("SELECT * FROM user_info WHERE id = #{id}")
    User findById(Long id);
    
    @Select("SELECT * FROM user_info WHERE username = #{username}")
    User findByUsername(String username);
    
    @Select("SELECT * FROM user_info WHERE username = #{username} AND password = #{password}")
    User findByUsernameAndPassword(@Param("username") String username, @Param("password") String password);
    
    @Update("UPDATE user_info SET real_name = #{realName}, email = #{email}, phone = #{phone} WHERE id = #{id}")
    int updateBasicInfo(User user);
    
    @Update("UPDATE user_info SET password = #{password} WHERE id = #{id}")
    int updatePassword(@Param("id") Long id, @Param("password") String password);
    
    @Update("UPDATE user_info SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
    
    @Delete("DELETE FROM user_info WHERE id = #{id}")
    int deleteById(Long id);
    
    @Select("SELECT * FROM user_info ORDER BY create_time DESC")
    List<User> findAll();
    
    @Select("SELECT * FROM user_info WHERE user_type = #{userType} ORDER BY create_time DESC")
    List<User> findByUserType(String userType);
    
    @Select("SELECT * FROM user_info WHERE status = #{status} ORDER BY create_time DESC")
    List<User> findByStatus(Integer status);
} 