package com.cemenghui.system.repository;

import com.cemenghui.system.entity.EnterpriseUser;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnterpriseUserMapper {

    /**
     * 注册企业用户
     * @param user 企业用户实体
     */
    @Insert("INSERT INTO enterprise_user (user_id, real_name, enterprise_id, account, password, nickname, phone, email, department, create_time) VALUES (#{userId}, #{realName}, #{enterpriseId}, #{account}, #{password}, #{nickname}, #{phone}, #{email}, #{department}, NOW())")
    void saveUser(EnterpriseUser user);

    /**
     * 根据账号查询企业用户
     * @param account 账号
     * @return 企业用户实体
     */
    @Select("SELECT * FROM enterprise_user WHERE account = #{account}")
    EnterpriseUser findByAccount(String account);

    /**
     * 根据用户ID查询企业用户
     * @param userId 用户ID
     * @return 企业用户实体
     */
    @Select("SELECT * FROM enterprise_user WHERE user_id = #{userId}")
    EnterpriseUser findByUserId(String userId);

    /**
     * 更新用户信息
     * @param user 包含待更新字段的用户实体
     */
    @Update("UPDATE enterprise_user SET real_name = #{realName}, enterprise_id = #{enterpriseId}, account = #{account}, password = #{password}, nickname = #{nickname}, phone = #{phone}, email = #{email}, department = #{department} WHERE user_id = #{userId}")
    void update(EnterpriseUser user);

    /**
     * 删除用户
     * @param userId 用户ID
     */
    @Delete("DELETE FROM enterprise_user WHERE user_id = #{userId}")
    void deleteByUserId(String userId);

    /**
     * 根据企业ID查询企业用户
     * @param enterpriseId 企业ID
     * @return 企业用户列表
     */
    @Select("SELECT * FROM enterprise_user WHERE enterprise_id = #{enterpriseId}")
    List<EnterpriseUser> findByEnterpriseId(String enterpriseId);

    /**
     * 根据企业ID统计企业用户数量
     * @param enterpriseId 企业ID
     * @return 用户数量
     */
    @Select("SELECT COUNT(*) FROM enterprise_user WHERE enterprise_id = #{enterpriseId}")
    int countByEnterpriseId(String enterpriseId);
}