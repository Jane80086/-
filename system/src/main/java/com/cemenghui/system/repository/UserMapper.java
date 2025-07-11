package com.cemenghui.system.repository;

import com.cemenghui.system.entity.EnterpriseUser;
import com.cemenghui.system.entity.AdminUser;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMapper {
    // EnterpriseUser 通用方法
    @Insert("INSERT INTO users (username, password, real_name, email, phone, user_type, status, department, nickname, avatar, is_remembered, enterprise_id, dynamic_code, create_time, update_time, deleted) VALUES (#{username}, #{password}, #{realName}, #{email}, #{phone}, 'ENTERPRISE', #{status}, #{department}, #{nickname}, #{avatar}, #{isRemembered}, #{enterpriseId}, #{dynamicCode}, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0)")
    void saveEnterpriseUser(EnterpriseUser user);

    @Select("SELECT * FROM users WHERE username = #{username} AND user_type = 'ENTERPRISE' AND deleted = 0")
    EnterpriseUser findEnterpriseByAccount(String username);

    @Select("SELECT * FROM users WHERE id = #{userId} AND user_type = 'ENTERPRISE' AND deleted = 0")
    EnterpriseUser findEnterpriseByUserId(Long userId);

    @Update("UPDATE users SET real_name = #{realName}, email = #{email}, phone = #{phone}, status = #{status}, department = #{department}, nickname = #{nickname}, avatar = #{avatar}, is_remembered = #{isRemembered}, enterprise_id = #{enterpriseId}, dynamic_code = #{dynamicCode}, update_time = CURRENT_TIMESTAMP WHERE id = #{id} AND user_type = 'ENTERPRISE'")
    void updateEnterpriseUser(EnterpriseUser user);

    @Update("UPDATE users SET deleted = 1, update_time = CURRENT_TIMESTAMP WHERE id = #{userId} AND user_type = 'ENTERPRISE'")
    void deleteEnterpriseByUserId(Long userId);

    // AdminUser 通用方法
    @Insert("INSERT INTO users (username, password, real_name, email, phone, user_type, status, department, nickname, avatar, is_remembered, create_time, update_time, deleted) VALUES (#{username}, #{password}, #{realName}, #{email}, #{phone}, 'ADMIN', #{status}, #{department}, #{nickname}, #{avatar}, #{isRemembered}, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0)")
    void saveAdminUser(AdminUser user);

    @Select("SELECT * FROM users WHERE username = #{username} AND user_type = 'ADMIN' AND deleted = 0")
    AdminUser findAdminByAccount(String username);

    @Select("SELECT * FROM users WHERE id = #{userId} AND user_type = 'ADMIN' AND deleted = 0")
    AdminUser findAdminByUserId(Long userId);

    @Update("UPDATE users SET real_name = #{realName}, email = #{email}, phone = #{phone}, status = #{status}, department = #{department}, nickname = #{nickname}, avatar = #{avatar}, is_remembered = #{isRemembered}, update_time = CURRENT_TIMESTAMP WHERE id = #{id} AND user_type = 'ADMIN'")
    void updateAdminUser(AdminUser user);

    @Update("UPDATE users SET deleted = 1, update_time = CURRENT_TIMESTAMP WHERE id = #{userId} AND user_type = 'ADMIN'")
    void deleteAdminByUserId(Long userId);

    // 普通用户通用方法
    @Insert("INSERT INTO users (username, password, real_name, email, phone, user_type, status, department, nickname, avatar, is_remembered, create_time, update_time, deleted) VALUES (#{username}, #{password}, #{realName}, #{email}, #{phone}, 'NORMAL', #{status}, #{department}, #{nickname}, #{avatar}, #{isRemembered}, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0)")
    void saveUser(com.cemenghui.system.entity.User user);

    @Select("SELECT * FROM users WHERE username = #{username} AND user_type = 'NORMAL' AND deleted = 0")
    com.cemenghui.system.entity.User findByAccount(String username);

    @Select("SELECT * FROM users WHERE id = #{userId} AND user_type = 'NORMAL' AND deleted = 0")
    com.cemenghui.system.entity.User findByUserId(Long userId);

    @Update("UPDATE users SET real_name = #{realName}, email = #{email}, phone = #{phone}, status = #{status}, department = #{department}, nickname = #{nickname}, avatar = #{avatar}, is_remembered = #{isRemembered}, update_time = CURRENT_TIMESTAMP WHERE id = #{id} AND user_type = 'NORMAL'")
    void updateUser(com.cemenghui.system.entity.User user);

    @Update("UPDATE users SET deleted = 1, update_time = CURRENT_TIMESTAMP WHERE id = #{userId} AND user_type = 'NORMAL'")
    void deleteByUserId(Long userId);
}
