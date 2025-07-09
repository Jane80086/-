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
import java.util.ArrayList;
import java.util.List;

/**
 * 登录业务实现
 */
@Service
public class LoginServiceImpl implements LoginService {

    @Resource
    private UserMapper userMapper;
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
            // 注意：如果 registerThirdPartyUser 方法没有将用户保存到数据库并获取到 ID，
            // 那么 user.getId() 可能会是 null。请确保注册流程能生成并返回一个带 ID 的 User 对象。
        } else {
            user = userMapper.findUserByAccount(thirdParty.getAccount());
        }

        List<String> roles = new ArrayList<>();
        if (user != null && user.getUserType() != null) {
            roles.add(user.getUserType().toUpperCase());
        } else {
            roles.add("NORMAL"); // 默认角色
        }

        // ==== 核心修改点2：thirdPartyLogin 也应该传入 userId ====
        // 确保 user.getId() 在这里不为 null
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), roles);
        return LoginResponseDTO.success(token, user);
    }

    // 发送动态验证码（用户登录时调用）
    public void sendDynamicCode(String account) {
        String code = String.valueOf((int)((Math.random() * 9 + 1) * 100000));
        User user = userMapper.findUserByAccount(account);
        if (user != null) {
            System.out.println("已为用户 " + user.getUsername() + " 发送动态验证码：" + code + " 到手机号：" + user.getPhone());
        }
    }

    @Override
    public LoginResponseDTO login(String account, String password) {
        User user = userMapper.findUserByAccount(account);
        if (user == null) {
            return LoginResponseDTO.fail("用户不存在");
        }
        System.out.println("用户账号: " + account);
        System.out.println("用户输入的明文密码: " + password);
        System.out.println("数据库中存储的加密密码: " + user.getPassword());

        if (!passwordUtil.matches(password, user.getPassword())) {
            return LoginResponseDTO.fail("密码错误");
        }

        List<String> roles = new ArrayList<>();
        if (user.getUserType() != null) {
            roles.add(user.getUserType().toUpperCase());
        } else {
            roles.add("NORMAL");
        }

        // ==== 核心修改点3：调用 JWTUtil.generateToken 时传入 user.getId() 和角色信息 ====
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), roles); // **传入 user.getId()**
        return LoginResponseDTO.success(token, user);
    }

    @Override
    public boolean checkDynamicCode(String account, String dynamicCode) {
        User user = userMapper.findUserByAccount(account);
        if (user == null) return false;
        return true;
    }

    @Override
    public String generateToken(User user) {
        // ==== 核心修改点4：确保这个方法也传入 user.getId() 和角色信息 ====
        List<String> roles = new ArrayList<>();
        if (user.getUserType() != null) {
            roles.add(user.getUserType().toUpperCase());
        } else {
            roles.add("NORMAL");
        }
        return jwtUtil.generateToken(user.getId(), user.getUsername(), roles); // **传入 user.getId()**
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
        User user = new User();
        user.setUsername(platform + "_" + openId);
        user.setPassword(""); // 第三方登录无需密码
        user.setUserType("NORMAL"); // 设置默认用户类型
        // TODO: 这里需要将 user 保存到数据库，并确保 user.getId() 能获取到生成的 ID
        // 例如：userMapper.insert(user);
        // 如果 insert 方法会自动填充 ID，那么 user 对象就会有 ID 了
        return user;
    }
}