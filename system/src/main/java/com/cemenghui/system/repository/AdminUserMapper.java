package com.cemenghui.system.repository;

import com.cemenghui.system.entity.AdminUser;
import com.cemenghui.system.entity.EnterpriseUser;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminUserMapper {

    /**
     * 根据账号查询管理员用户
     * @param account 管理员账号
     * @return 管理员用户实体
     */
    @Select("SELECT * FROM admin_user WHERE account = #{account}")
    AdminUser findByAccount(String account);

    /**
     * 管理员登录记录
     * @param adminId 管理员ID
     * @param loginTime 登录时间
     */
    void recordLogin(@Param("adminId") Long adminId, @Param("loginTime") String loginTime);

    @Insert("INSERT INTO admin_user (user_id, real_name, department, account, password, nickname, phone, email) VALUES (#{userId}, #{realName}, #{department}, #{account}, #{password}, #{nickname}, #{phone}, #{email})")
    void save(AdminUser user);

    @Select("SELECT * FROM admin_user WHERE user_id = #{userId}")
    AdminUser findByUserId(String userId);

    @Update("UPDATE admin_user SET real_name = #{realName}, department = #{department}, account = #{account}, password = #{password}, nickname = #{nickname}, phone = #{phone}, email = #{email} WHERE user_id = #{userId}")
    void update(AdminUser user);

    @Delete("DELETE FROM admin_user WHERE user_id = #{userId}")
    void deleteByUserId(String userId);

    @Select("SELECT * FROM admin_user")
    List<AdminUser> findAll();

    @Select("SELECT * FROM enterprise_user WHERE account = #{account}")
    EnterpriseUser findEnterpriseByAccount(String account);

    @Select("SELECT * FROM admin_user WHERE account = #{account}")
    AdminUser findAdminByAccount(String account);
}