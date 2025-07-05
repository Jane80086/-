package com.cemenghui.system.repository;

import com.cemenghui.entity.User;
import com.cemenghui.system.dto.UserQueryDTO;
import com.cemenghui.system.entity.UserModifyHistory;
import com.cemenghui.system.entity.UserTemplate;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserManagementMapper {

    // 查询用户列表
    List<User> getUserList(UserQueryDTO query);

    // 查询用户总数
    int getUserCount(UserQueryDTO query);

    // 根据ID查询用户
    User getUserById(Long userId);

    // 创建用户
    void createUserByTemplate(User user);

    // 记录用户修改历史
    void recordModifyHistory(UserModifyHistory history);

    // 查询用户模板
    UserTemplate getUserTemplate(String templateId);

    // 查询所有用户模板
    List<UserTemplate> getAllTemplates();

    // 分配权限
    void addPermission(@Param("userId") Long userId, @Param("permission") String permission);

    // 继承角色权限
    void inheritPermissions(@Param("userId") Long userId, @Param("roleName") String roleName);

    // 更新用户信息
    void updateUser(User user);

    // 删除用户
    void deleteUserById(Long userId);

    @Select("SELECT * FROM users WHERE account = #{account}")
    User getUserByAccount(String account);

    @Update("UPDATE users SET real_name = #{realName}, account = #{account}, enterprise_id = #{enterpriseId}, phone = #{phone}, email = #{email}, avatar = #{avatar}, update_time = NOW() WHERE account = #{account}")
    void updateUserByAccount(User user);

    /**
     * 获取所有用户数据（用于Excel导出）
     * @return 用户列表
     */
    @Select("SELECT * FROM users ORDER BY create_time DESC")
    List<User> getAllUsers();

    /**
     * 根据企业ID查询用户列表
     * @param enterpriseId 企业ID
     * @return 用户列表
     */
    @Select("SELECT * FROM users WHERE enterprise_id = #{enterpriseId}")
    List<User> getUsersByEnterpriseId(@Param("enterpriseId") String enterpriseId);
}
