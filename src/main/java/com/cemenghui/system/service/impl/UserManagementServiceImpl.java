package com.system.service.impl;

import com.system.dto.UserListDTO;
import com.system.dto.UserQueryDTO;
import com.system.entity.EnterpriseUser;
import com.system.entity.UserModifyHistory;
import com.system.entity.UserTemplate;
import com.system.repository.UserManagementMapper;
import com.system.service.UserManagementService;
import com.system.util.ExcelUtil;
import com.system.util.UUIDUtil;
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

import com.system.repository.EnterpriseMapper;
import com.system.entity.Enterprise;
import com.system.repository.EnterpriseUserMapper;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.ByteArrayOutputStream;

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
                    user.getUserId(),
                    user.getRealName(),
                    user.getAccount(),
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
        // 生成用户ID
        String userId = UUIDUtil.generateUUID();
        user.setUserId(userId);

        // 查询模板
        UserTemplate template = userManagementMapper.getUserTemplate(templateId);
        if (template != null) {
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
                        case "account":
                            user.setAccount((String) value); break;
                        case "password":
                            user.setPassword((String) value); break;
                        default:
                            // 其它字段可扩展
                    }
                });
            }
            // 继承模板角色权限
            if (template.getRole() != null) {
                userManagementMapper.inheritPermissions(userId, template.getRole());
            }
            // 添加模板预设权限
            if (!CollectionUtils.isEmpty(template.getPermissions())) {
                template.getPermissions().forEach(permission -> {
                    userManagementMapper.addPermission(userId, permission);
                });
            }
        }
        // 创建用户
        userManagementMapper.createUserByTemplate(user);
        return userId;
    }

    @Transactional
    @Override
    public boolean updateUser(EnterpriseUser user) {
        user.setUpdateTime(LocalDateTime.now().toString());
        userManagementMapper.updateUser(user);
        return true;
    }

    @Override
    public List<UserModifyHistory> getUserModifyHistory(String userId) {
        // 实际项目中使用SELECT语句查询历史记录，这里简化为返回空列表
        return new ArrayList<>();
    }

    @Transactional
    @Override
    public boolean assignPermissions(String userId, Set<String> permissions) {
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
    public boolean inheritRolePermissions(String userId, String roleName) {
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
    private void recordModifyHistory(EnterpriseUser original, EnterpriseUser updated, String operatorId) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String historyId = UUIDUtil.generateUUID();
        // 比较并记录修改的字段
        if (!Objects.equals(original.getPhone(), updated.getPhone())) {
            UserModifyHistory history = new UserModifyHistory();
            history.setHistoryId(historyId);
            history.setUserId(original.getUserId());
            history.setFieldName("phone");
            history.setOldValue(original.getPhone());
            history.setNewValue(updated.getPhone());
            history.setOperatorId(operatorId);
            userManagementMapper.recordModifyHistory(history);
        }
        if (!Objects.equals(original.getEmail(), updated.getEmail())) {
            UserModifyHistory history = new UserModifyHistory();
            history.setHistoryId(UUIDUtil.generateUUID());
            history.setUserId(original.getUserId());
            history.setFieldName("email");
            history.setOldValue(original.getEmail());
            history.setNewValue(updated.getEmail());
            history.setOperatorId(operatorId);
            userManagementMapper.recordModifyHistory(history);
        }
        if (!Objects.equals(original.getRealName(), updated.getRealName())) {
            UserModifyHistory history = new UserModifyHistory();
            history.setHistoryId(UUIDUtil.generateUUID());
            history.setUserId(original.getUserId());
            history.setFieldName("realName");
            history.setOldValue(original.getRealName());
            history.setNewValue(updated.getRealName());
            history.setOperatorId(operatorId);
            userManagementMapper.recordModifyHistory(history);
        }
        if (!Objects.equals(original.getNickname(), updated.getNickname())) {
            UserModifyHistory history = new UserModifyHistory();
            history.setHistoryId(UUIDUtil.generateUUID());
            history.setUserId(original.getUserId());
            history.setFieldName("nickname");
            history.setOldValue(original.getNickname());
            history.setNewValue(updated.getNickname());
            history.setOperatorId(operatorId);
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
    public String createUser(EnterpriseUser user) {
        String userId = UUIDUtil.generateUUID();
        user.setUserId(userId);
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
    public boolean deleteUser(String userId) {
        try {
            System.out.println("[DEBUG] 尝试删除用户: userId=" + userId);
            int before = userManagementMapper.getUserCount(new com.system.dto.UserQueryDTO());
            userManagementMapper.deleteUserById(userId);
            int after = userManagementMapper.getUserCount(new com.system.dto.UserQueryDTO());
            System.out.println("[DEBUG] 删除前用户数: " + before + ", 删除后用户数: " + after);
            return true;
        } catch (Exception e) {
            System.err.println("[ERROR] 删除用户异常: " + e.getMessage());
            return false;
        }
    }

    @Override
    public EnterpriseUser getUserById(String userId) {
        return userManagementMapper.getUserById(userId);
    }

    @Override
    public EnterpriseUser getUserByAccount(String account) {
        return userManagementMapper.getUserByAccount(account);
    }

    @Override
    public boolean updateUserByAccount(EnterpriseUser user) {
        user.setUpdateTime(java.time.LocalDateTime.now().toString());
        userManagementMapper.updateUserByAccount(user);
        return true;
    }

    @Override
    public byte[] exportUsersToExcel() throws IOException {
        // 获取所有用户数据
        List<EnterpriseUser> users = userManagementMapper.getAllUsers();
        
        // 创建Excel工作簿
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("用户列表");
            
            // 创建标题行样式
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            
            // 创建标题行
            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "账号", "真实姓名", "邮箱", "手机号", "企业ID", "部门", "状态", "创建时间"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // 填充数据行
            int rowNum = 1;
            for (EnterpriseUser user : users) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(user.getId() != null ? user.getId().toString() : "");
                row.createCell(1).setCellValue(user.getAccount() != null ? user.getAccount() : "");
                row.createCell(2).setCellValue(user.getRealName() != null ? user.getRealName() : "");
                row.createCell(3).setCellValue(user.getEmail() != null ? user.getEmail() : "");
                row.createCell(4).setCellValue(user.getPhone() != null ? user.getPhone() : "");
                row.createCell(5).setCellValue(user.getEnterpriseId() != null ? user.getEnterpriseId() : "");
                row.createCell(6).setCellValue(user.getDepartment() != null ? user.getDepartment() : "");
                row.createCell(7).setCellValue("1".equals(user.getStatus()) ? "启用" : "禁用");
                row.createCell(8).setCellValue(user.getCreateTime() != null ? user.getCreateTime() : "");
            }
            
            // 自动调整列宽
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            // 将工作簿写入字节数组
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }
}
