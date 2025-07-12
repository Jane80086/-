package com.cemenghui.system.repository;

import com.cemenghui.entity.EnterpriseUser;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnterpriseUserMapper {

    /**
     * 注册企业用户
     * @param user 企业用户实体
     */
    @Insert("INSERT INTO users (username, password, real_name, email, phone, user_type, status, department, nickname, avatar, is_remembered, enterprise_id, dynamic_code, create_time, update_time, deleted) VALUES (#{username}, #{password}, #{realName}, #{email}, #{phone}, 'ENTERPRISE', #{status}, #{department}, #{nickname}, #{avatar}, #{isRemembered}, #{enterpriseId}, #{dynamicCode}, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0)")
    void saveUser(EnterpriseUser user);

    /**
     * 根据账号查询企业用户
     *
     * @param username 账号
     * @return 企业用户实体
     */
    @Select("SELECT * FROM users WHERE username = #{username} AND user_type = 'ENTERPRISE' AND deleted = 0")
    com.cemenghui.system.entity.EnterpriseUser findByAccount(String username);

    /**
     * 根据用户ID查询企业用户
     * @param userId 用户ID
     * @return 企业用户实体
     */
    @Select("SELECT * FROM users WHERE id = #{userId} AND user_type = 'ENTERPRISE' AND deleted = 0")
    EnterpriseUser findByUserId(Long userId);

    /**
     * 更新用户信息
     * @param user 包含待更新字段的用户实体
     */
    @Update("UPDATE users SET real_name = #{realName}, email = #{email}, phone = #{phone}, status = #{status}, department = #{department}, nickname = #{nickname}, avatar = #{avatar}, is_remembered = #{isRemembered}, enterprise_id = #{enterpriseId}, dynamic_code = #{dynamicCode}, update_time = CURRENT_TIMESTAMP WHERE id = #{id} AND user_type = 'ENTERPRISE'")
    void update(EnterpriseUser user);

    /**
     * 删除用户
     * @param userId 用户ID
     */
    @Update("UPDATE users SET deleted = 1, update_time = CURRENT_TIMESTAMP WHERE id = #{userId} AND user_type = 'ENTERPRISE'")
    void deleteByUserId(Long userId);

    /**
     * 根据企业ID查询企业用户
     *
     * @param enterpriseId 企业ID
     * @return 企业用户列表
     */
    @Select("SELECT * FROM users WHERE enterprise_id = #{enterpriseId} AND user_type = 'ENTERPRISE' AND deleted = 0")
    List<com.cemenghui.system.entity.EnterpriseUser> findByEnterpriseId(String enterpriseId);

    /**
     * 根据企业ID统计企业用户数量
     * @param enterpriseId 企业ID
     * @return 用户数量
     */
    @Select("SELECT COUNT(*) FROM users WHERE enterprise_id = #{enterpriseId} AND user_type = 'ENTERPRISE' AND deleted = 0")
    int countByEnterpriseId(String enterpriseId);

    void saveUser(com.cemenghui.system.entity.EnterpriseUser user);

    void update(com.cemenghui.system.entity.EnterpriseUser user);
}