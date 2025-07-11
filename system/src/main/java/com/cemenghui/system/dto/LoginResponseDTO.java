package com.cemenghui.system.dto;

import com.cemenghui.system.entity.AdminUser;
import com.cemenghui.system.entity.EnterpriseUser;
import com.cemenghui.system.entity.User;
import lombok.Data;

@Data
public class LoginResponseDTO {

    // 登录是否成功
    private boolean success;
    // 提示消息
    private String message;
    // 令牌（用于后续请求认证）
    private String token;
    // 用户类型：admin, enterprise
    private String userType;
    // 企业用户信息
    private EnterpriseUser enterpriseUser;
    // 管理员用户信息
    private AdminUser adminUser;
    // 普通用户信息
    private User normalUser;

    /**
     * 构建企业用户登录成功响应
     * @param token    认证令牌
     * @param user 企业用户信息
     * @return 登录成功的响应DTO
     */
    public static LoginResponseDTO success(String token, EnterpriseUser user) {
        LoginResponseDTO dto = new LoginResponseDTO();
        dto.setSuccess(true);
        dto.setMessage("登录成功");
        dto.setToken(token);
        dto.setUserType("enterprise");
        dto.setEnterpriseUser(user);
        dto.setAdminUser(null);
        return dto;
    }

    /**
     * 构建管理员用户登录成功响应
     * @param token    认证令牌
     * @param admin 管理员用户信息
     * @return 登录成功的响应DTO
     */
    public static LoginResponseDTO success(String token, AdminUser admin) {
        LoginResponseDTO dto = new LoginResponseDTO();
        dto.setSuccess(true);
        dto.setMessage("登录成功");
        dto.setToken(token);
        dto.setUserType("admin");
        dto.setAdminUser(admin);
        dto.setEnterpriseUser(null);
        return dto;
    }

    /**
     * 构建普通用户登录成功响应
     * @param token    认证令牌
     * @param user 普通用户信息
     * @return 登录成功的响应DTO
     */
    public static LoginResponseDTO success(String token, User user) {
        LoginResponseDTO dto = new LoginResponseDTO();
        dto.setSuccess(true);
        dto.setMessage("登录成功");
        dto.setToken(token);
        dto.setUserType("user");
        dto.setNormalUser(user);
        dto.setEnterpriseUser(null);
        dto.setAdminUser(null);
        return dto;
    }

    /**
     * 构建登录失败响应
     * @param message 失败提示信息
     * @return 登录失败的响应DTO
     */
    public static LoginResponseDTO fail(String message) {
        LoginResponseDTO dto = new LoginResponseDTO();
        dto.setSuccess(false);
        dto.setMessage(message);
        dto.setToken(null);
        dto.setUserType(null);
        dto.setEnterpriseUser(null);
        dto.setAdminUser(null);
        return dto;
    }

    /**
     * 获取用户信息（通用方法）
     * @return 用户对象
     */
    public Object getUser() {
        if ("admin".equals(userType)) {
            return adminUser;
        } else if ("enterprise".equals(userType)) {
            return enterpriseUser;
        } else if ("user".equals(userType)) {
            return normalUser;
        }
        return null;
    }
}
