package com.cemenghui.system.repository;

import com.cemenghui.system.dto.UserHistoryQueryDTO;
import com.cemenghui.system.dto.UserQueryDTO;
import com.cemenghui.system.entity.EnterpriseUser;
import com.cemenghui.system.entity.UserModifyHistory;
import com.cemenghui.system.entity.UserTemplate;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserManagementMapper {

    // 添加缺失的field_name字段到system_user_modify_history表
    void addFieldNameColumn();

    // 查询用户列表
    List<EnterpriseUser> getUserList(UserQueryDTO query);

    // 查询用户总数
    int getUserCount(UserQueryDTO query);

    // 根据ID查询用户
    EnterpriseUser getUserById(Long userId);

    // 创建用户
    int createUserByTemplate(EnterpriseUser user);

    // 记录用户修改历史
    void recordModifyHistory(UserModifyHistory history);

    // 查询用户模板
    UserTemplate getUserTemplate(String templateId);

    // 查询所有用户模板
    List<UserTemplate> getAllTemplates();

    // 分配权限
    void addPermission(@Param("userId") Long userId, @Param("permission") String permission);

    // 更新用户信息
    int updateUser(EnterpriseUser user);

    // 删除用户
    void deleteUserById(Long userId);

    @Select("SELECT * FROM users WHERE username = #{username} AND user_type = 'ENTERPRISE' AND deleted = 0")
    EnterpriseUser getUserByUsername(String username);

    @Update("UPDATE users SET real_name = #{realName}, username = #{username}, password = #{password}, nickname = #{nickname}, enterprise_id = #{enterpriseId}, phone = #{phone}, email = #{email}, department = #{department}, update_time = CURRENT_TIMESTAMP WHERE username = #{username} AND user_type = 'ENTERPRISE'")
    void updateUserByUsername(EnterpriseUser user);

    // 分页查询用户修改历史
    List<UserModifyHistory> getUserModifyHistoryPaged(UserHistoryQueryDTO query);

    // 统计用户修改历史总数
    int countUserModifyHistory(UserHistoryQueryDTO query);

    // 根据历史ID获取历史记录
    UserModifyHistory getHistoryById(Long historyId);
}
