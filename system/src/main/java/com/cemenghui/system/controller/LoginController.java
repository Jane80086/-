package com.cemenghui.system.controller;

import com.cemenghui.entity.User;
import com.cemenghui.system.service.UserManagementService;
import com.cemenghui.system.util.CaptchaUtil;
import com.cemenghui.system.dto.LoginRequestDTO;
import com.cemenghui.system.dto.LoginResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.cemenghui.common.JWTUtil;

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
    private JWTUtil jwtUtil;

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
    public User syncEnterpriseInfoById(@RequestParam String enterpriseId) {
        return userManagementService.syncEnterpriseInfoById(enterpriseId);
    }

    // 根据企业ID查询企业用户列表
    @GetMapping("/enterpriseUsers")
    public List<User> getEnterpriseUsersByEnterpriseId(@RequestParam String enterpriseId) {
        return userManagementService.getEnterpriseUsersByEnterpriseId(enterpriseId);
    }

    @RestControllerAdvice
    public class GlobalExceptionHandler {
        @ExceptionHandler(Exception.class)
        public ResponseEntity<String> handleAll(Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("全局异常: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequest) {
        try{
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
            
            // 统一使用main-app的User实体
            User user = userMapper.findUserByAccount(account);
            System.out.println("SQL查到的用户: " + user);
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
            String jwt = jwtUtil.generateToken(user.getUsername());
            dto.setToken(jwt);
            dto.setUser(user);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            e.printStackTrace(); // 控制台输出详细堆栈
            LoginResponseDTO dto = new LoginResponseDTO();
            dto.setSuccess(false);
            dto.setMessage("服务器异常: " + e.getMessage());
            return ResponseEntity.status(500).body(dto);
        }
    }
}