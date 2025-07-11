package com.cemenghui.system.controller;

import com.cemenghui.system.entity.EnterpriseUser;
import com.cemenghui.system.service.UserManagementService;
import com.cemenghui.system.util.CaptchaUtil;
import com.cemenghui.system.util.RedisUtil;
import com.cemenghui.system.dto.LoginRequestDTO;
import com.cemenghui.system.dto.LoginResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import com.cemenghui.common.JWTUtil;

@RestController
@RequestMapping("/api/login")
public class LoginController {

    @Autowired
    private UserManagementService userManagementService;

    @Autowired
    private CaptchaUtil captchaUtil;

    @Autowired
    private com.cemenghui.system.repository.UserMapper userMapper;

    @Autowired
    private com.cemenghui.system.repository.AdminUserMapper adminUserMapper;

    @Autowired
    private com.cemenghui.common.JWTUtil jwtUtil;

    @Autowired
    private RedisUtil redisUtil;

    // 测试接口
    @GetMapping("/test")
    public String test() {
        return "LoginController is working!";
    }

    // 同步企业工商信息
    @GetMapping("/syncEnterpriseInfo")
    public EnterpriseUser syncEnterpriseInfo(@RequestParam String enterpriseName) {
        return userManagementService.syncEnterpriseInfo(enterpriseName);
    }

    // 根据企业ID同步企业工商信息
    @GetMapping("/syncEnterpriseInfoById")
    public EnterpriseUser syncEnterpriseInfoById(@RequestParam String enterpriseId) {
        return userManagementService.syncEnterpriseInfoById(enterpriseId);
    }

    // 根据企业ID查询企业用户列表
    @GetMapping("/enterpriseUsers")
    public List<EnterpriseUser> getEnterpriseUsersByEnterpriseId(@RequestParam String enterpriseId) {
        return userManagementService.getEnterpriseUsersByEnterpriseId(enterpriseId);
    }

    @PostMapping
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequest) {
        System.out.println("Login API called!");
        String account = loginRequest.getAccount();
        String password = loginRequest.getPassword();
        String verificationCode = loginRequest.getVerificationCode();
        String uuid = loginRequest.getUuid(); // 从请求中获取uuid
        System.out.println("收到登录请求，账号: [" + account + "]，密码: [" + password + "]，验证码: [" + verificationCode + "]，UUID: [" + uuid + "]");
        LoginResponseDTO dto = new LoginResponseDTO();
        if (account == null || account.isEmpty()) {
            dto.setSuccess(false);
            dto.setMessage("账号不能为空");
            return ResponseEntity.badRequest().body(dto);
        }
        if (password == null || password.isEmpty()) {
            dto.setSuccess(false);
            dto.setMessage("密码不能为空");
            return ResponseEntity.badRequest().body(dto);
        }
        if (verificationCode == null || verificationCode.isEmpty()) {
            dto.setSuccess(false);
            dto.setMessage("验证码不能为空");
            return ResponseEntity.badRequest().body(dto);
        }
        // 验证码校验
        if (uuid != null && !uuid.isEmpty()) {
            try {
                System.out.println("登录验证码校验 - 输入验证码: [" + verificationCode + "], UUID: [" + uuid + "]");
                String storedCaptcha = redisUtil.get("captcha:" + uuid);
                System.out.println("登录验证码校验 - Redis验证码: [" + storedCaptcha + "]");
                boolean valid = storedCaptcha != null && captchaUtil.validateCaptcha(verificationCode, storedCaptcha);
                System.out.println("登录验证码校验 - 校验结果: [" + valid + "]");
                if (!valid) {
                    dto.setSuccess(false);
                    dto.setMessage("验证码错误或已过期");
                    return ResponseEntity.ok(dto);
                }
            } catch (Exception e) {
                System.out.println("验证码校验异常: " + e.getMessage());
                dto.setSuccess(false);
                dto.setMessage("验证码校验失败");
                return ResponseEntity.ok(dto);
            }
        }
        if (account.startsWith("0000")) {
            // 管理员
            com.cemenghui.system.entity.AdminUser admin = adminUserMapper.findByAccount(account);
            System.out.println("SQL查到的用户: " + admin);
            if (admin == null) {
                dto.setSuccess(false);
                dto.setMessage("管理员账号不存在");
                return ResponseEntity.ok(dto);
            }
            System.out.println("数据库密码: [" + admin.getPassword() + "]");
            if (!password.equals(admin.getPassword())) {
                dto.setSuccess(false);
                dto.setMessage("密码错误");
                return ResponseEntity.ok(dto);
            }
            dto.setSuccess(true);
            dto.setMessage("登录成功");
            String jwt = jwtUtil.generateToken(admin.getId(), admin.getAccount(), java.util.List.of("admin"));
            dto.setToken(jwt);
            dto.setUserType("admin");
            dto.setAdminUser(admin);
            dto.setEnterpriseUser(null);
            return ResponseEntity.ok(dto);
        } else {
            // 企业用户
            com.cemenghui.system.entity.EnterpriseUser user = userMapper.findEnterpriseByAccount(account);
            System.out.println("SQL查到的企业用户: " + user);
            if (user != null) {
                System.out.println("数据库密码: [" + user.getPassword() + "]");
                if (!password.equals(user.getPassword())) {
                    dto.setSuccess(false);
                    dto.setMessage("密码错误");
                    return ResponseEntity.ok(dto);
                }
                dto.setSuccess(true);
                dto.setMessage("登录成功");
                String jwt = jwtUtil.generateToken(user.getId(), user.getAccount(), java.util.List.of("enterprise"));
                dto.setToken(jwt);
                dto.setUserType("enterprise");
                dto.setEnterpriseUser(user);
                dto.setAdminUser(null);
                return ResponseEntity.ok(dto);
            } else {
                // 查普通用户
                com.cemenghui.system.entity.User normalUser = userMapper.findByAccount(account);
                System.out.println("SQL查到的普通用户: " + normalUser);
                if (normalUser == null) {
                    dto.setSuccess(false);
                    dto.setMessage("用户账号不存在");
                    return ResponseEntity.ok(dto);
                }
                System.out.println("数据库密码: [" + normalUser.getPassword() + "]");
                if (!password.equals(normalUser.getPassword())) {
                    dto.setSuccess(false);
                    dto.setMessage("密码错误");
                    return ResponseEntity.ok(dto);
                }
                dto.setSuccess(true);
                dto.setMessage("登录成功");
                String jwt = jwtUtil.generateToken(normalUser.getId(), normalUser.getAccount(), java.util.List.of("normal"));
                dto.setToken(jwt);
                dto.setUserType("user");
                dto.setNormalUser(normalUser); // 关键：设置normalUser
                dto.setEnterpriseUser(null);
                dto.setAdminUser(null);
                return ResponseEntity.ok(dto);
            }
        }
    }
}