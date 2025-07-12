package com.cemenghui.system.repository;

import com.cemenghui.entity.User;
import com.cemenghui.system.entity.AdminUser;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminUserMapper {

    /**
     * 根据账号查询管理员用户
     *
     * @param username 管理员账号
     * @return 管理员用户实体
     */
    @Select("SELECT * FROM users WHERE username = #{username} AND user_type = 'ADMIN' AND deleted = 0")
    AdminUser findByAccount(String username);

    /**
     * 管理员登录记录
     * @param adminId 管理员ID
     * @param loginTime 登录时间
     */
    void recordLogin(@Param("adminId") Long adminId, @Param("loginTime") String loginTime);

    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("INSERT INTO users (username, password, real_name, email, phone, user_type, status, department, nickname, avatar, is_remembered, create_time, update_time, deleted) VALUES (#{username}, #{password}, #{realName}, #{email}, #{phone}, #{userType}, #{status}, #{department}, #{nickname}, #{avatar}, #{isRemembered}, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0)")
    void save(AdminUser user);

    @Select("SELECT * FROM users WHERE id = #{userId} AND user_type = 'ADMIN' AND deleted = 0")
    User findByUserId(Long userId);

    @Update("UPDATE users SET real_name = #{realName}, email = #{email}, phone = #{phone}, status = #{status}, department = #{department}, nickname = #{nickname}, avatar = #{avatar}, is_remembered = #{isRemembered}, update_time = CURRENT_TIMESTAMP WHERE id = #{id} AND user_type = 'ADMIN'")
    void update(User user);

    @Update("UPDATE users SET deleted = 1, update_time = CURRENT_TIMESTAMP WHERE id = #{userId} AND user_type = 'ADMIN'")
    void deleteByUserId(Long userId);

    @Select("SELECT * FROM users WHERE user_type = 'ADMIN' AND deleted = 0")
    List<User> findAll();

    @Select("SELECT * FROM users WHERE username = #{username} AND user_type = 'ENTERPRISE' AND deleted = 0")
    com.cemenghui.system.entity.EnterpriseUser findEnterpriseByAccount(String username);

    @Select("SELECT * FROM users WHERE username = #{username} AND user_type = 'ADMIN' AND deleted = 0")
    AdminUser findAdminByAccount(String username);
}