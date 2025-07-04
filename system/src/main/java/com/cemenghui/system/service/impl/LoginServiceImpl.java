package com.cemenghui.system.service.impl;

import com.cemenghui.system.entity.User;
import com.cemenghui.system.entity.AdminUser;
import com.cemenghui.system.entity.EnterpriseUser;
import com.cemenghui.system.entity.ThirdPartyAccount;
import com.cemenghui.system.dto.LoginResponseDTO;
import com.cemenghui.system.service.LoginService;
import com.cemenghui.system.repository.UserMapper;
import com.cemenghui.system.repository.AdminUserMapper;
import com.cemenghui.system.repository.EnterpriseUserMapper;
import com.cemenghui.common.JWTUtil;
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
    public LoginResponseDTO enterpriseLogin(String account, String password, boolean rememberMe, String dynamicCode) {
        EnterpriseUser user = enterpriseUserMapper.findByAccount(account);
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
            user.setRemembered(true);
            enterpriseUserMapper.update(user);
        }

        String token = jwtUtil.generateToken(user.getAccount());
        return LoginResponseDTO.success(token, user);
    }

    // 超级管理员登录
    public LoginResponseDTO adminLogin(String account, String password, String dynamicCode) {
        AdminUser admin = adminUserMapper.findByAccount(account);
        if (admin == null) return LoginResponseDTO.fail("管理员不存在");

        if (!passwordUtil.matches(password, admin.getPassword())) {
            return LoginResponseDTO.fail("密码错误");
        }

        // 校验动态验证码（假设已发送到绑定设备）
        if (!dynamicCode.equals(admin.getDynamicCode())) {
            return LoginResponseDTO.fail("动态验证码错误");
        }

        String token = jwtUtil.generateToken(admin.getAccount());
        return LoginResponseDTO.success(token, admin);
    }

    // 第三方快捷登录
    public LoginResponseDTO thirdPartyLogin(String platform, String openId, String accessToken) {
        // 1. 校验第三方token（略，需调用第三方API）
        // 2. 查找或注册本地用户
        ThirdPartyAccount thirdParty = userMapper.findThirdPartyAccount(platform, openId);
        User user;
        if (thirdParty == null) {
            // 自动注册
            user = registerThirdPartyUser(platform, openId);
        } else {
            user = userMapper.findEnterpriseByAccount(thirdParty.getAccount());
        }
        String token = jwtUtil.generateToken(user.getAccount());
        return LoginResponseDTO.success(token, user);
    }

    // 发送动态验证码（企业/管理员登录时调用）
    public void sendDynamicCode(String account, boolean isAdmin) {
        String code = String.valueOf((int)((Math.random() * 9 + 1) * 100000)); // 6位数字验证码
        if (isAdmin) {
            AdminUser admin = adminUserMapper.findByAccount(account);
            if (admin != null) {
                admin.setDynamicCode(code); // 需在AdminUser中补充setDynamicCode方法
                // TODO: 可扩展保存到数据库或缓存
                System.out.println("已为管理员 " + admin.getAccount() + " 发送动态验证码：" + code + " 到手机号：" + admin.getPhone());
            }
        } else {
            EnterpriseUser user = enterpriseUserMapper.findByAccount(account);
            if (user != null) {
                user.setDynamicCode(code); // 需在EnterpriseUser中补充setDynamicCode方法
                // TODO: 可扩展保存到数据库或缓存
                System.out.println("已为企业用户 " + user.getAccount() + " 发送动态验证码：" + code + " 到手机号：" + user.getPhone());
            }
        }
    }

    @Override
    public LoginResponseDTO login(String account, String password) {
        return null;
    }

    @Override
    public boolean checkDynamicCode(String account, String dynamicCode) {
        // 示例逻辑：从用户信息/缓存校验动态码
        User user = userMapper.findEnterpriseByAccount(account);
        if (user == null) return false;
        return dynamicCode.equals(user.getDynamicCode()); // 假设User有dynamicCode字段
    }

    @Override
    public String generateToken(User user) {
        return jwtUtil.generateToken(user.getAccount());
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
    private User registerThirdPartyUser(String platform, String openId) {
        // 简单实现：创建新User并保存，实际项目应完善
        User user = new User();
        user.setAccount(platform + "_" + openId);
        user.setPassword(""); // 第三方登录无需密码
        // TODO: 保存到数据库，可扩展更多字段
        userMapper.saveEnterpriseUser((EnterpriseUser) user); // 假设用EnterpriseUser
        return user;
    }
}