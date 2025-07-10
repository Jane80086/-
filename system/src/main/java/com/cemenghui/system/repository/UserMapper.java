package com.system.repository;

import com.system.entity.EnterpriseUser;
import com.system.entity.AdminUser;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMapper {
    // EnterpriseUser 通用方法
    @Insert("INSERT INTO enterprise_user (user_id, real_name, enterprise_id, account, password, nickname, phone, email) VALUES (#{userId}, #{realName}, #{enterpriseId}, #{account}, #{password}, #{nickname}, #{phone}, #{email})")
    void saveEnterpriseUser(EnterpriseUser user);

    @Select("SELECT * FROM enterprise_user WHERE account = #{account}")
    EnterpriseUser findEnterpriseByAccount(String account);

    @Select("SELECT * FROM enterprise_user WHERE user_id = #{userId}")
    EnterpriseUser findEnterpriseByUserId(String userId);

    @Update("UPDATE enterprise_user SET real_name = #{realName}, enterprise_id = #{enterpriseId}, account = #{account}, password = #{password}, nickname = #{nickname}, phone = #{phone}, email = #{email} WHERE user_id = #{userId}")
    void updateEnterpriseUser(EnterpriseUser user);

    @Delete("DELETE FROM enterprise_user WHERE user_id = #{userId}")
    void deleteEnterpriseByUserId(String userId);

    // AdminUser 通用方法
    @Insert("INSERT INTO admin_user (user_id, real_name, department, account, password, nickname, phone, email) VALUES (#{userId}, #{realName}, #{department}, #{account}, #{password}, #{nickname}, #{phone}, #{email})")
    void saveAdminUser(AdminUser user);

    @Select("SELECT * FROM admin_user WHERE account = #{account}")
    AdminUser findAdminByAccount(String account);

    @Select("SELECT * FROM admin_user WHERE user_id = #{userId}")
    AdminUser findAdminByUserId(String userId);

    @Update("UPDATE admin_user SET real_name = #{realName}, department = #{department}, account = #{account}, password = #{password}, nickname = #{nickname}, phone = #{phone}, email = #{email} WHERE user_id = #{userId}")
    void updateAdminUser(AdminUser user);

    @Delete("DELETE FROM admin_user WHERE user_id = #{userId}")
    void deleteAdminByUserId(String userId);
}