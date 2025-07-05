package com.cemenghui.system.service.impl;

import com.cemenghui.entity.User;
import com.cemenghui.system.dto.UserListDTO;
import com.cemenghui.system.dto.UserQueryDTO;
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
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.ByteArrayOutputStream;

@Service
public class UserManagementServiceImpl implements UserManagementService {

    @Autowired
    private UserManagementMapper userManagementMapper;

    @Autowired
    private EnterpriseMapper enterpriseMapper;

    @Override
    public UserListDTO getUserList(UserQueryDTO query) {
        UserListDTO result = new UserListDTO();
        List<User> userList = userManagementMapper.getUserList(query);
        int total = userManagementMapper.getUserCount(query);

        result.setRecords(userList);
        result.setTotal(total);
        result.setPages((int) Math.ceil((double) total / query.getPageSize()));

        return result;
    }

    @Override
    public void exportUserList(UserQueryDTO query, HttpServletResponse response) throws IOException {
        List<User> userList = userManagementMapper.getUserList(query);
        if (CollectionUtils.isEmpty(userList)) {
            return;
        }

        // 定义Excel表头
        List<String> headers = Arrays.asList(
                "用户ID", "用户名", "真实姓名", "昵称", "企业ID", "手机号码", "邮箱", "用户类型", "状态", "创建时间", "更新时间"
        );

        // 转换数据格式
        List<List<String>> data = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (User user : userList) {
            List<String> row = Arrays.asList(
                    user.getId() != null ? user.getId().toString() : "",
                    user.getUsername(),
                    user.getRealName(),
                    user.getNickname(),
                    user.getEnterpriseId(),
                    user.getPhone(),
                    user.getEmail(),
                    user.getUserType(),
                    user.getStatus() != null ? user.getStatus().toString() : "",
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
    public String createUserByTemplate(User user, String templateId) {
        // 查询模板
        UserTemplate template = userManagementMapper.getUserTemplate(templateId);
        if (template != null) {
            // 应用模板默认值
            if (template.getDefaultValues() != null) {
                // TODO: 实现模板默认值应用逻辑
            }
            // 继承模板角色权限
            if (template.getRole() != null) {
                userManagementMapper.inheritPermissions(user.getId(), template.getRole());
            }
            // 添加模板预设权限
            if (!CollectionUtils.isEmpty(template.getPermissions())) {
                template.getPermissions().forEach(permission -> {
                    userManagementMapper.addPermission(user.getId(), permission);
                });
            }
        }
        // 创建用户
        userManagementMapper.createUserByTemplate(user);
        return user.getId() != null ? user.getId().toString() : "";
    }

    @Transactional
    @Override
    public boolean updateUser(User user) {
        user.setUpdateTime(LocalDateTime.now());
        userManagementMapper.updateUser(user);
        return true;
    }

    @Override
    public List<UserModifyHistory> getUserModifyHistory(Long userId) {
        // 实际项目中使用SELECT语句查询历史记录，这里简化为返回空列表
        return new ArrayList<>();
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
        if (roleName == null) {
            return false;
        }

        userManagementMapper.inheritPermissions(userId, roleName);
        return true;
    }

    @Override
    public List<UserTemplate> getAllTemplates() {
        return userManagementMapper.getAllTemplates();
    }

    // 记录修改历史的辅助方法
    private void recordModifyHistory(User original, User updated, Long operatorId) {
        String historyId = UUIDUtil.generateUUID();
        // 比较并记录修改的字段
        if (!Objects.equals(original.getPhone(), updated.getPhone())) {
            UserModifyHistory history = new UserModifyHistory();
            history.setHistoryId(historyId);
            history.setUserId(original.getId());
            history.setFieldName("phone");
            history.setOldValue(original.getPhone());
            history.setNewValue(updated.getPhone());
            history.setOperatorId(operatorId);
            history.setModifyTime(LocalDateTime.now());
            userManagementMapper.recordModifyHistory(history);
        }
        // 可以继续添加其他字段的比较
    }

    @Override
    public User syncEnterpriseInfo(String enterpriseName) {
        // 调用企业信息查询API（这里简化实现）
        Enterprise enterprise = enterpriseMapper.findByEnterpriseName(enterpriseName);
        if (enterprise == null) {
            return null;
        }

        User user = new User();
        user.setEnterpriseId(enterprise.getEnterpriseId());
        // TODO: 设置其他企业相关信息
        return user;
    }

    @Override
    public User syncEnterpriseInfoById(String enterpriseId) {
        // 调用企业信息查询API（这里简化实现）
        Enterprise enterprise = enterpriseMapper.findByEnterpriseId(enterpriseId);
        if (enterprise == null) {
            return null;
        }

        User user = new User();
        user.setEnterpriseId(enterprise.getEnterpriseId());
        // TODO: 设置其他企业相关信息
        return user;
    }

    @Override
    public List<User> getEnterpriseUsersByEnterpriseId(String enterpriseId) {
        // 查询企业下的所有用户
        List<User> users = userManagementMapper.getUsersByEnterpriseId(enterpriseId);
        
        // 可以在这里添加额外的业务逻辑
        for (User user : users) {
            // TODO: 设置企业相关信息
        }
        
        return users;
    }

    @Transactional
    @Override
    public String createUser(User user) {
        // 设置默认值
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        user.setStatus(1); // 默认启用
        
        // 创建用户
        userManagementMapper.createUserByTemplate(user);
        
        // 返回用户ID
        User inserted = userManagementMapper.getUserById(user.getId());
        return inserted != null ? inserted.getId().toString() : "";
    }

    @Override
    public boolean deleteUser(Long userId) {
        // 实际项目中可能需要软删除
        userManagementMapper.deleteUserById(userId);
        return true;
    }

    @Override
    public User getUserById(Long userId) {
        return userManagementMapper.getUserById(userId);
    }

    @Override
    public User getUserByAccount(String account) {
        return userManagementMapper.getUserByAccount(account);
    }

    @Override
    public boolean updateUserByAccount(User user) {
        user.setUpdateTime(LocalDateTime.now());
        userManagementMapper.updateUserByAccount(user);
        return true;
    }

    @Override
    public byte[] exportUsersToExcel() throws IOException {
        List<User> users = userManagementMapper.getAllUsers();
        
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("用户列表");
            
            // 创建表头
            Row headerRow = sheet.createRow(0);
            String[] headers = {"用户ID", "用户名", "真实姓名", "昵称", "企业ID", "手机号码", "邮箱", "用户类型", "状态", "创建时间"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }
            
            // 填充数据
            int rowNum = 1;
            for (User user : users) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(user.getId() != null ? user.getId().toString() : "");
                row.createCell(1).setCellValue(user.getUsername());
                row.createCell(2).setCellValue(user.getRealName());
                row.createCell(3).setCellValue(user.getNickname());
                row.createCell(4).setCellValue(user.getEnterpriseId());
                row.createCell(5).setCellValue(user.getPhone());
                row.createCell(6).setCellValue(user.getEmail());
                row.createCell(7).setCellValue(user.getUserType());
                row.createCell(8).setCellValue(user.getStatus() != null ? user.getStatus().toString() : "");
                row.createCell(9).setCellValue(user.getCreateTime() != null ? user.getCreateTime().toString() : "");
            }
            
            // 输出到字节数组
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }
}
