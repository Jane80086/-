package com.cemenghui.system.service.impl;

import com.cemenghui.system.dto.UserHistoryListDTO;
import com.cemenghui.system.dto.UserHistoryQueryDTO;
import com.cemenghui.system.dto.UserListDTO;
import com.cemenghui.system.dto.UserQueryDTO;
import com.cemenghui.system.entity.EnterpriseUser;
import com.cemenghui.system.entity.UserModifyHistory;
import com.cemenghui.system.entity.UserTemplate;
import com.cemenghui.system.repository.UserManagementMapper;
import com.cemenghui.system.service.UserManagementService;
import com.cemenghui.system.util.ExcelUtil;
import com.cemenghui.system.util.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import com.cemenghui.system.repository.EnterpriseMapper;
import com.cemenghui.system.entity.Enterprise;
import com.cemenghui.system.repository.EnterpriseUserMapper;

@Service
public class UserManagementServiceImpl implements UserManagementService {

    @Autowired
    private UserManagementMapper userManagementMapper;

    @Autowired
    private EnterpriseMapper enterpriseMapper;

    @Autowired
    private EnterpriseUserMapper enterpriseUserMapper;

    @Override
    public UserListDTO getUserList(UserQueryDTO query) {
        UserListDTO result = new UserListDTO();
        List<EnterpriseUser> userList = userManagementMapper.getUserList(query);
        int total = userManagementMapper.getUserCount(query);

        result.setRecords(userList);
        result.setTotal(total);
        result.setPages((int) Math.ceil((double) total / query.getPageSize()));

        return result;
    }

    @Override
    public void exportUserList(UserQueryDTO query, HttpServletResponse response) throws IOException {
        List<EnterpriseUser> userList = userManagementMapper.getUserList(query);
        if (CollectionUtils.isEmpty(userList)) {
            return;
        }

        // 定义Excel表头
        List<String> headers = Arrays.asList(
                "用户ID", "真实姓名", "账号", "昵称", "企业ID", "企业名称", "手机号码", "邮箱", "创建时间", "更新时间"
        );

        // 转换数据格式
        List<List<String>> data = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (EnterpriseUser user : userList) {
            List<String> row = Arrays.asList(
                    user.getId() != null ? user.getId().toString() : "",
                    user.getRealName(),
                    user.getUsername(),
                    user.getNickname(),
                    user.getEnterpriseId(),
                    user.getEnterpriseName(),
                    user.getPhone(),
                    user.getEmail(),
                    user.getCreateTime() != null ? user.getCreateTime().toString() : "",
                    user.getUpdateTime() != null ? user.getUpdateTime().toString() : ""
            );
            data.add(row);
        }

        // 导出Excel
        new ExcelUtil().exportExcel(headers, data, "用户列表", response);
    }

    @Transactional
    @Override
    public String createUserByTemplate(EnterpriseUser user, String templateId) {
        // 查询模板
        UserTemplate template = userManagementMapper.getUserTemplate(templateId);
        if (template == null) {
            return null;
        }
        // 生成用户ID
        Long userId = System.currentTimeMillis(); // 临时ID生成方式
        user.setId(userId);
        // 应用模板默认值
        if (template.getDefaultValues() != null) {
            template.getDefaultValues().forEach((key, value) -> {
                switch (key) {
                    case "realName":
                        user.setRealName((String) value); break;
                    case "nickname":
                        user.setNickname((String) value); break;
                    case "enterpriseId":
                        user.setEnterpriseId((String) value); break;
                    case "enterpriseName":
                        user.setEnterpriseName((String) value); break;
                    case "phone":
                        user.setPhone((String) value); break;
                    case "email":
                        user.setEmail((String) value); break;
                    case "username":
                        user.setUsername((String) value); break;
                    case "password":
                        user.setPassword((String) value); break;
                    default:
                        // 其它字段可扩展
                }
            });
        }
        // 继承模板角色权限
        // if (template.getRole() != null) {
        //     userManagementMapper.inheritPermissions(userId, template.getRole());
        // }
        // 添加模板预设权限
        if (!CollectionUtils.isEmpty(template.getPermissions())) {
            template.getPermissions().forEach(permission -> {
                userManagementMapper.addPermission(userId, permission);
            });
        }
        // 创建用户
        userManagementMapper.createUserByTemplate(user);
        return userId.toString();
    }

    @Transactional
    @Override
    public boolean updateUser(EnterpriseUser user) {
        // 获取原始用户信息
        EnterpriseUser originalUser = userManagementMapper.getUserById(user.getId());

        // 更新用户信息
        user.setUpdateTime(LocalDateTime.now());
        userManagementMapper.updateUser(user);

        // 记录修改历史
        if (originalUser != null) {
            recordModifyHistory(originalUser, user, user.getId().toString()); // 这里可以传入实际的操作人ID
        }

        return true;
    }

    @Override
    public List<UserModifyHistory> getUserModifyHistory(Long userId) {
        // 实际项目中使用SELECT语句查询历史记录，这里简化为返回空列表
        return new ArrayList<>();
    }

    @Override
    public UserHistoryListDTO getUserModifyHistoryPaged(UserHistoryQueryDTO query) {
        UserHistoryListDTO result = new UserHistoryListDTO();
        List<UserModifyHistory> historyList = userManagementMapper.getUserModifyHistoryPaged(query);
        int total = userManagementMapper.countUserModifyHistory(query);

        result.setRecords(historyList);
        result.setTotal(total);
        result.setPages((int) Math.ceil((double) total / query.getPageSize()));
        result.setCurrent(query.getPage());
        result.setSize(query.getPageSize());

        return result;
    }

    @Transactional
    @Override
    public boolean restoreUserHistory(Long historyId) {
        try {
            // 1. 根据历史ID获取历史记录
            UserModifyHistory history = userManagementMapper.getHistoryById(historyId);
            if (history == null) {
                System.err.println("[ERROR] 历史记录不存在: historyId=" + historyId);
                return false;
            }

            // 2. 获取当前用户信息
            EnterpriseUser currentUser = userManagementMapper.getUserById(history.getUserId());
            if (currentUser == null) {
                System.err.println("[ERROR] 用户不存在: userId=" + history.getUserId());
                return false;
            }

            // 3. 复制当前用户的所有信息，然后只更新需要恢复的字段
            EnterpriseUser userToUpdate = new EnterpriseUser();
            userToUpdate.setId(currentUser.getId());
            userToUpdate.setRealName(currentUser.getRealName());
            userToUpdate.setUsername(currentUser.getUsername());
            userToUpdate.setPassword(currentUser.getPassword());
            userToUpdate.setNickname(currentUser.getNickname());
            userToUpdate.setEnterpriseId(currentUser.getEnterpriseId());
            userToUpdate.setPhone(currentUser.getPhone());
            userToUpdate.setEmail(currentUser.getEmail());
            userToUpdate.setStatus(currentUser.getStatus());

            // 4. 根据字段名恢复对应的值
            switch (history.getFieldName()) {
                case "realName":
                    userToUpdate.setRealName(history.getOldValue());
                    break;
                case "nickname":
                    userToUpdate.setNickname(history.getOldValue());
                    break;
                case "phone":
                    userToUpdate.setPhone(history.getOldValue());
                    break;
                case "email":
                    userToUpdate.setEmail(history.getOldValue());
                    break;
                case "enterpriseId":
                    userToUpdate.setEnterpriseId(history.getOldValue());
                    break;
                default:
                    System.err.println("[ERROR] 不支持的字段恢复: " + history.getFieldName());
                    return false;
            }

            // 5. 更新用户信息
            userToUpdate.setUpdateTime(LocalDateTime.now());
            userManagementMapper.updateUser(userToUpdate);

            // 6. 记录恢复操作的历史
            UserModifyHistory restoreHistory = new UserModifyHistory();
            restoreHistory.setHistoryId(UUIDUtil.generateUUID());
            restoreHistory.setUserId(history.getUserId());
            restoreHistory.setFieldName(history.getFieldName());
            restoreHistory.setOldValue(history.getNewValue()); // 当前值
            restoreHistory.setNewValue(history.getOldValue()); // 恢复的值
            restoreHistory.setOperatorId(history.getOperatorId()); // 使用原操作人ID
            userManagementMapper.recordModifyHistory(restoreHistory);

            System.out.println("[INFO] 成功恢复历史记录: historyId=" + historyId + ", field=" + history.getFieldName());
            return true;
        } catch (Exception e) {
            System.err.println("[ERROR] 恢复历史记录失败: " + e.getMessage());
            return false;
        }
    }

    @Transactional
    @Override
    public boolean assignPermissions(Long userId, Set<String> permissions) {
        if (CollectionUtils.isEmpty(permissions)) {
            return false;
        }

        // 先删除原有权限（简化实现，实际项目可能需要增量更新）
        // 然后添加新权限
        permissions.forEach(permission -> {
            userManagementMapper.addPermission(userId, permission);
        });

        return true;
    }

    @Transactional
    @Override
    public boolean inheritRolePermissions(Long userId, String roleName) {
        // 已废弃：role相关逻辑已移除
        return false;
    }

    @Override
    public List<UserTemplate> getAllTemplates() {
        return userManagementMapper.getAllTemplates();
    }

    // 记录修改历史的辅助方法
    private void recordModifyHistory(EnterpriseUser original, EnterpriseUser updated, String operatorId) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String historyId = UUIDUtil.generateUUID();
        // 比较并记录修改的字段
        if (!Objects.equals(original.getPhone(), updated.getPhone())) {
            UserModifyHistory history = new UserModifyHistory();
            history.setHistoryId(historyId);
            history.setUserId(original.getId());
            history.setFieldName("phone");
            history.setOldValue(original.getPhone());
            history.setNewValue(updated.getPhone());
            history.setOperatorId(operatorId != null ? Long.valueOf(operatorId) : null);
            userManagementMapper.recordModifyHistory(history);
        }
        if (!Objects.equals(original.getEmail(), updated.getEmail())) {
            UserModifyHistory history = new UserModifyHistory();
            history.setHistoryId(UUIDUtil.generateUUID());
            history.setUserId(original.getId());
            history.setFieldName("email");
            history.setOldValue(original.getEmail());
            history.setNewValue(updated.getEmail());
            history.setOperatorId(operatorId != null ? Long.valueOf(operatorId) : null);
            userManagementMapper.recordModifyHistory(history);
        }
        if (!Objects.equals(original.getRealName(), updated.getRealName())) {
            UserModifyHistory history = new UserModifyHistory();
            history.setHistoryId(UUIDUtil.generateUUID());
            history.setUserId(original.getId());
            history.setFieldName("realName");
            history.setOldValue(original.getRealName());
            history.setNewValue(updated.getRealName());
            history.setOperatorId(operatorId != null ? Long.valueOf(operatorId) : null);
            userManagementMapper.recordModifyHistory(history);
        }
        if (!Objects.equals(original.getNickname(), updated.getNickname())) {
            UserModifyHistory history = new UserModifyHistory();
            history.setHistoryId(UUIDUtil.generateUUID());
            history.setUserId(original.getId());
            history.setFieldName("nickname");
            history.setOldValue(original.getNickname());
            history.setNewValue(updated.getNickname());
            history.setOperatorId(operatorId != null ? Long.valueOf(operatorId) : null);
            userManagementMapper.recordModifyHistory(history);
        }
    }

    // 同步企业工商信息
    @Override
    public EnterpriseUser syncEnterpriseInfo(String enterpriseName) {
        if (enterpriseName == null || enterpriseName.trim().isEmpty()) {
            return null;
        }

        try {
            Enterprise enterprise = enterpriseMapper.findByEnterpriseName(enterpriseName);
            if (enterprise != null) {
                EnterpriseUser enterpriseUser = new EnterpriseUser();
                enterpriseUser.setEnterpriseId(enterprise.getEnterpriseId() != null ? enterprise.getEnterpriseId().toString() : null);
                enterpriseUser.setEnterpriseName(enterprise.getEnterpriseName());
                enterpriseUser.setEnterprise(enterprise);
                return enterpriseUser;
            }
        } catch (Exception e) {
            System.err.println("同步企业工商信息失败：" + e.getMessage());
        }
        return null;
    }

    // 根据企业ID同步企业工商信息
    @Override
    public EnterpriseUser syncEnterpriseInfoById(String enterpriseId) {
        if (enterpriseId == null || enterpriseId.trim().isEmpty()) {
            return null;
        }

        try {
            Enterprise enterprise = enterpriseMapper.findByEnterpriseId(enterpriseId);
            if (enterprise != null) {
                EnterpriseUser enterpriseUser = new EnterpriseUser();
                enterpriseUser.setEnterpriseId(enterprise.getEnterpriseId() != null ? enterprise.getEnterpriseId().toString() : null);
                enterpriseUser.setEnterpriseName(enterprise.getEnterpriseName());
                enterpriseUser.setEnterprise(enterprise);
                return enterpriseUser;
            }
        } catch (Exception e) {
            System.err.println("根据企业ID同步企业工商信息失败：" + e.getMessage());
        }
        return null;
    }

    // 根据企业ID查询企业用户列表
    @Override
    public List<EnterpriseUser> getEnterpriseUsersByEnterpriseId(String enterpriseId) {
        if (enterpriseId == null || enterpriseId.trim().isEmpty()) {
            return new ArrayList<>();
        }

        try {
            List<EnterpriseUser> users = enterpriseUserMapper.findByEnterpriseId(enterpriseId);
            // 为每个用户加载企业信息
            for (EnterpriseUser user : users) {
                Enterprise enterprise = enterpriseMapper.findByEnterpriseId(enterpriseId);
                user.setEnterprise(enterprise);
            }
            return users;
        } catch (Exception e) {
            System.err.println("根据企业ID查询企业用户失败：" + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Transactional
    @Override
    public Long createUser(EnterpriseUser user) {
        Long userId = System.currentTimeMillis(); // 临时ID生成方式
        user.setId(userId);
        System.out.println("[DEBUG] 尝试插入用户: " + user);
        try {
            userManagementMapper.createUserByTemplate(user);
            // 插入后立即查询确认
            EnterpriseUser inserted = userManagementMapper.getUserById(userId);
            if (inserted == null) {
                System.err.println("[ERROR] 用户插入失败，数据库无记录: userId=" + userId);
                throw new RuntimeException("用户插入失败，数据库无记录");
            } else {
                System.out.println("[DEBUG] 用户插入成功: " + inserted);
            }
            return userId;
        } catch (Exception e) {
            System.err.println("[ERROR] 用户插入异常: " + e.getMessage());
            throw new RuntimeException("用户插入异常: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteUser(Long userId) {
        try {
            System.out.println("[DEBUG] 尝试删除用户: userId=" + userId);
            int before = userManagementMapper.getUserCount(new com.cemenghui.system.dto.UserQueryDTO());
            userManagementMapper.deleteUserById(userId);
            int after = userManagementMapper.getUserCount(new com.cemenghui.system.dto.UserQueryDTO());
            System.out.println("[DEBUG] 删除前用户数: " + before + ", 删除后用户数: " + after);
            return true;
        } catch (Exception e) {
            System.err.println("[ERROR] 删除用户异常: " + e.getMessage());
            return false;
        }
    }

    @Override
    public EnterpriseUser getUserById(Long userId) {
        return userManagementMapper.getUserById(userId);
    }

    @Override
    public EnterpriseUser getUserByUsername(String username) {
        return userManagementMapper.getUserByUsername(username);
    }

    @Override
    public boolean updateUserByUsername(EnterpriseUser user) {
        user.setUpdateTime(java.time.LocalDateTime.now());
        userManagementMapper.updateUserByUsername(user);
        return true;
    }
    
    @Override
    public EnterpriseUser getUserByAccount(String account) {
        return userManagementMapper.getUserByUsername(account);
    }
    
    @Override
    public boolean updateUserByAccount(EnterpriseUser user) {
        return updateUserByUsername(user);
    }
}
