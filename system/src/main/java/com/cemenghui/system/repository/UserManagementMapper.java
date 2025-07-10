package com.system.repository;

import com.system.dto.UserHistoryQueryDTO;
import com.system.dto.UserQueryDTO;
import com.system.entity.EnterpriseUser;
import com.system.entity.UserModifyHistory;
import com.system.entity.UserTemplate;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserManagementMapper {

    // 查询用户列表
    List<EnterpriseUser> getUserList(UserQueryDTO query);

    // 查询用户总数
    int getUserCount(UserQueryDTO query);

    // 根据ID查询用户
    EnterpriseUser getUserById(String userId);

    // 创建用户
    void createUserByTemplate(EnterpriseUser user);

    // 记录用户修改历史
    void recordModifyHistory(UserModifyHistory history);

    // 查询用户模板
    UserTemplate getUserTemplate(String templateId);

    // 查询所有用户模板
    List<UserTemplate> getAllTemplates();

    // 分配权限
    void addPermission(@Param("userId") String userId, @Param("permission") String permission);

    // 继承角色权限
    void inheritPermissions(@Param("userId") String userId, @Param("roleName") String roleName);

    // 更新用户信息
    void updateUser(EnterpriseUser user);

    // 删除用户
    void deleteUserById(String userId);

    @Select("SELECT * FROM enterprise_user WHERE account = #{account}")
    EnterpriseUser getUserByAccount(String account);

    @Update("UPDATE enterprise_user SET real_name = #{realName}, account = #{account}, password = #{password}, nickname = #{nickname}, enterprise_id = #{enterpriseId}, phone = #{phone}, email = #{email}, department = #{department}, update_time = NOW() WHERE account = #{account}")
    void updateUserByAccount(EnterpriseUser user);

    // 分页查询用户修改历史
    List<UserModifyHistory> getUserModifyHistoryPaged(UserHistoryQueryDTO query);

    // 统计用户修改历史总数
    int countUserModifyHistory(UserHistoryQueryDTO query);

    // 根据历史ID获取历史记录
    UserModifyHistory getHistoryById(String historyId);
}
