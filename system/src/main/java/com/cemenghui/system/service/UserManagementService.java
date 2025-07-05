package com.cemenghui.system.service;

import com.cemenghui.entity.User;
import com.cemenghui.system.dto.UserListDTO;
import com.cemenghui.system.dto.UserQueryDTO;
import com.cemenghui.system.entity.UserModifyHistory;
import com.cemenghui.system.entity.UserTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public interface UserManagementService {

    // 获取用户列表
    UserListDTO getUserList(UserQueryDTO query);

    // 导出用户列表为Excel
    void exportUserList(UserQueryDTO query, HttpServletResponse response) throws IOException;

    // 模板化创建用户
    String createUserByTemplate(User user, String templateId);

    // 普通添加用户
    String createUser(User user);

    /**
     * 更新用户信息
     */
    boolean updateUser(User user);

    // 获取用户修改历史
    List<UserModifyHistory> getUserModifyHistory(Long userId);

    // 分配权限
    boolean assignPermissions(Long userId, Set<String> permissions);

    // 继承角色权限
    boolean inheritRolePermissions(Long userId, String roleName);

    // 获取所有用户模板
    List<UserTemplate> getAllTemplates();

    // 同步企业工商信息
    User syncEnterpriseInfo(String enterpriseName);

    // 根据企业ID同步企业工商信息
    User syncEnterpriseInfoById(String enterpriseId);

    // 根据企业ID查询企业用户列表
    List<User> getEnterpriseUsersByEnterpriseId(String enterpriseId);

    // 删除用户
    boolean deleteUser(Long userId);

    // 根据ID查询用户
    User getUserById(Long userId);

    // 通过账号查询用户
    User getUserByAccount(String account);

    // 通过账号更新用户
    boolean updateUserByAccount(User user);

    /**
     * 导出用户列表到Excel
     * @return Excel文件的字节数组
     */
    byte[] exportUsersToExcel() throws IOException;
}
