package com.cemenghui.system.service.impl;

import com.cemenghui.system.entity.AdminUser;
import com.cemenghui.system.entity.EnterpriseUser;
import com.cemenghui.system.dto.LoginResponseDTO;
import com.cemenghui.system.service.LoginService;
import com.cemenghui.system.repository.UserMapper;
import com.cemenghui.system.repository.AdminUserMapper;
import com.cemenghui.system.repository.EnterpriseUserMapper;
import com.cemenghui.system.util.JWTUtil;
import com.cemenghui.system.util.PasswordUtil;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;

/**
 * 登录业务实现
 */
@Service
public class LoginServiceImpl implements LoginService {

    @Resource
    private UserMapper userMapper; // 用户数据访问
    @Resource
    private AdminUserMapper adminUserMapper;
    @Resource
    private EnterpriseUserMapper enterpriseUserMapper;
    @Resource
    private JWTUtil jwtUtil;
    @Resource
    private PasswordUtil passwordUtil;

    // 企业用户登录
    public LoginResponseDTO enterpriseLogin(String username, String password, boolean rememberMe, String dynamicCode) {
        EnterpriseUser user = enterpriseUserMapper.findByAccount(username);
        if (user == null) return LoginResponseDTO.fail("用户不存在");

        if (!passwordUtil.matches(password, user.getPassword())) {
            return LoginResponseDTO.fail("密码错误");
        }

        // 校验动态验证码（假设已发送到用户手机/邮箱/APP）
        if (!dynamicCode.equals(user.getDynamicCode())) {
            return LoginResponseDTO.fail("动态验证码错误");
        }

        // 记住密码功能（可选：设置cookie/token或更新数据库字段）
        if (rememberMe) {
            user.setIsRemembered(true);
            enterpriseUserMapper.update(user);
        }

        String token = jwtUtil.generateToken(user.getUsername());
        return LoginResponseDTO.success(token, user);
    }

    // 超级管理员登录
    public LoginResponseDTO adminLogin(String username, String password, String dynamicCode) {
        AdminUser admin = adminUserMapper.findByAccount(username);
        if (admin == null) return LoginResponseDTO.fail("管理员不存在");

        if (!passwordUtil.matches(password, admin.getPassword())) {
            return LoginResponseDTO.fail("密码错误");
        }

        // 校验动态验证码（假设已发送到绑定设备）
        if (!dynamicCode.equals(admin.getDynamicCode())) {
            return LoginResponseDTO.fail("动态验证码错误");
        }

        String token = jwtUtil.generateToken(admin.getUsername());
        return LoginResponseDTO.success(token, admin);
    }


    @Override
    public LoginResponseDTO login(String username, String password) {
        return null;
    }

    @Override
    public boolean checkDynamicCode(String username, String dynamicCode) {
        // 示例逻辑：从用户信息/缓存校验动态码
        EnterpriseUser user = userMapper.findEnterpriseByAccount(username);
        if (user == null) return false;
        return dynamicCode.equals(user.getDynamicCode()); // 假设EnterpriseUser有dynamicCode字段
    }

    @Override
    public String generateToken(EnterpriseUser user) {
        return jwtUtil.generateToken(user.getUsername());
    }

    @Override
    public boolean validateToken(String token) {
        return false;
    }

    @Override
    public String getUserFromToken(String token) {
        return jwtUtil.getAccountFromToken(token);
    }

    // 自动注册第三方用户
    private EnterpriseUser registerThirdPartyUser(String platform, String openId) {
        // 用 EnterpriseUser 替代 User，保证类型兼容
        EnterpriseUser user = new EnterpriseUser();
        user.setUsername(platform + "_" + openId);
        user.setPassword(""); // 第三方登录无需密码
        // TODO: 保存到数据库，可扩展更多字段
        userMapper.saveEnterpriseUser(user);
        return user;
    }
}