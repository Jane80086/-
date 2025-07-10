package com.cemenghui.system.controller;

import com.cemenghui.entity.User;
import com.cemenghui.system.entity.EnterpriseUser;
import com.cemenghui.system.service.UserManagementService;
import com.cemenghui.system.util.CaptchaUtil;
import com.cemenghui.system.dto.LoginRequestDTO;
import com.cemenghui.system.dto.LoginResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    private com.cemenghui.system.util.JWTUtil jwtUtil;

    // 测试接口
    @GetMapping("/test")
    public String test() {
        return "LoginController is working!";
    }

    // 同步企业工商信息
    @GetMapping("/syncEnterpriseInfo")
    public User syncEnterpriseInfo(@RequestParam String enterpriseName) {
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
        System.out.println("收到登录请求，账号: [" + account + "]，密码: [" + password + "]");
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
        // 验证码校验略
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
            String jwt = jwtUtil.generateToken(admin);
            dto.setToken(jwt);
            dto.setUser(admin);
            return ResponseEntity.ok(dto);
        } else {
            // 企业用户
            com.cemenghui.system.entity.EnterpriseUser user = userMapper.findEnterpriseByAccount(account);
            System.out.println("SQL查到的企业用户: " + user);
            if (user == null) {
                dto.setSuccess(false);
                dto.setMessage("用户账号不存在");
                return ResponseEntity.ok(dto);
            }
            System.out.println("数据库密码: [" + user.getPassword() + "]");
            if (!password.equals(user.getPassword())) {
                dto.setSuccess(false);
                dto.setMessage("密码错误");
                return ResponseEntity.ok(dto);
            }
            dto.setSuccess(true);
            dto.setMessage("登录成功");
            String jwt = jwtUtil.generateToken(account);
            dto.setToken(jwt);
            dto.setUser(user);
            return ResponseEntity.ok(dto);
        }
    }
}
