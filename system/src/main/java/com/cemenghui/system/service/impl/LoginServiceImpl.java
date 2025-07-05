package com.cemenghui.system.service.impl;

import com.cemenghui.entity.User;
import com.cemenghui.system.entity.ThirdPartyAccount;
import com.cemenghui.system.dto.LoginResponseDTO;
import com.cemenghui.system.service.LoginService;
import com.cemenghui.system.repository.UserMapper;
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
    private JWTUtil jwtUtil;
    @Resource
    private PasswordUtil passwordUtil;

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
            user = userMapper.findUserByAccount(thirdParty.getAccount());
        }
        String token = jwtUtil.generateToken(user.getUsername());
        return LoginResponseDTO.success(token, user);
    }

    // 发送动态验证码（用户登录时调用）
    public void sendDynamicCode(String account) {
        String code = String.valueOf((int)((Math.random() * 9 + 1) * 100000)); // 6位数字验证码
        User user = userMapper.findUserByAccount(account);
        if (user != null) {
            // TODO: 可扩展保存到数据库或缓存
            System.out.println("已为用户 " + user.getUsername() + " 发送动态验证码：" + code + " 到手机号：" + user.getPhone());
        }
    }

    @Override
    public LoginResponseDTO login(String account, String password) {
        User user = userMapper.findUserByAccount(account);
        if (user == null) {
            return LoginResponseDTO.fail("用户不存在");
        }

        if (!passwordUtil.matches(password, user.getPassword())) {
            return LoginResponseDTO.fail("密码错误");
        }

        String token = jwtUtil.generateToken(user.getUsername());
        return LoginResponseDTO.success(token, user);
    }

    @Override
    public boolean checkDynamicCode(String account, String dynamicCode) {
        // 示例逻辑：从用户信息/缓存校验动态码
        User user = userMapper.findUserByAccount(account);
        if (user == null) return false;
        // TODO: 实现动态码校验逻辑
        return true; // 临时返回true
    }

    @Override
    public String generateToken(User user) {
        return jwtUtil.generateToken(user.getUsername());
    }

    @Override
    public boolean validateToken(String token) {
        try {
            return jwtUtil.validateToken(token);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String getUserFromToken(String token) {
        return jwtUtil.getAccountFromToken(token);
    }

    // 自动注册第三方用户
    private User registerThirdPartyUser(String platform, String openId) {
        // 简单实现：创建新User并保存，实际项目应完善
        User user = new User();
        user.setUsername(platform + "_" + openId);
        user.setPassword(""); // 第三方登录无需密码
        user.setUserType("NORMAL"); // 设置默认用户类型
        // TODO: 保存到数据库，可扩展更多字段
        return user;
    }
}