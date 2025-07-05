package com.cemenghui.system.repository;

import com.cemenghui.entity.User;
import com.cemenghui.system.entity.ThirdPartyAccount;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserMapper {
    
    // 使用main-app的User实体
    @Select("SELECT * FROM users WHERE account = #{account}")
    User findUserByAccount(String account);

    @Select("SELECT * FROM users WHERE id = #{userId}")
    User findUserById(Long userId);

    @Select("SELECT * FROM users WHERE enterprise_id = #{enterpriseId}")
    List<User> findUsersByEnterpriseId(String enterpriseId);

    @Update("UPDATE users SET real_name = #{realName}, enterprise_id = #{enterpriseId}, account = #{account}, nickname = #{nickname}, phone = #{phone}, email = #{email}, avatar = #{avatar} WHERE id = #{id}")
    void updateUser(User user);

    @Delete("DELETE FROM users WHERE id = #{userId}")
    void deleteUserById(Long userId);

    // 查找第三方账号
    @Select("SELECT * FROM system_third_party_account WHERE platform = #{platform} AND open_id = #{openId}")
    ThirdPartyAccount findThirdPartyAccount(@Param("platform") String platform, @Param("openId") String openId);

    @Select("SELECT * FROM system_third_party_account WHERE user_id = #{userId}")
    List<ThirdPartyAccount> findThirdPartyAccountsByUserId(Long userId);

    @Insert("INSERT INTO system_third_party_account (third_party_id, open_id, platform, account, user_id) VALUES (#{thirdPartyId}, #{openId}, #{platform}, #{account}, #{userId})")
    void saveThirdPartyAccount(ThirdPartyAccount account);

    @Delete("DELETE FROM system_third_party_account WHERE user_id = #{userId}")
    void deleteThirdPartyAccountsByUserId(Long userId);
}