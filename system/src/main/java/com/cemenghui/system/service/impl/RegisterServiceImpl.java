package com.cemenghui.system.service.impl;

import com.cemenghui.system.dto.RegisterRequestDTO;
import com.cemenghui.system.dto.RegistResponseDTO;
import com.cemenghui.system.entity.EnterpriseUser;
import com.cemenghui.system.entity.Enterprise;
import com.cemenghui.system.entity.AdminUser;
import com.cemenghui.system.entity.User;
import com.cemenghui.system.repository.EnterpriseMapper;
import com.cemenghui.system.repository.EnterpriseUserMapper;
import com.cemenghui.system.repository.AdminUserMapper;
import com.cemenghui.system.repository.UserMapper;
import com.cemenghui.system.service.RegisterService;
import com.cemenghui.system.util.CaptchaUtil;
import com.cemenghui.system.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;
import java.util.UUID;

@Service
public class RegisterServiceImpl implements RegisterService {

    @Autowired
    private EnterpriseUserMapper enterpriseUserMapper; // 假设的用户 Mapper
    @Autowired
    private EnterpriseMapper enterpriseInfoMapper; // 假设的企业信息 Mapper
    @Autowired
    private AdminUserMapper adminUserMapper; // 新增：管理员用户 Mapper
    @Autowired
    private UserMapper userMapper; // 新增：普通用户 Mapper
    @Autowired
    private RedisUtil redisUtil;

    // 企业联系方式正则：区号-电话号码
    private static final String CONTACT_PATTERN = "^\\d{3,4}-\\d{7,8}$";
    // 密码正则：6-12位数字+字母
    private static final String PASSWORD_PATTERN = "^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d]{6,12}$";

    @Override
    public RegistResponseDTO register(RegisterRequestDTO requestDTO, CaptchaUtil captchaUtil) {
        RegistResponseDTO responseDTO = new RegistResponseDTO();

        String username = requestDTO.getAccount() != null ? requestDTO.getAccount() : "";
        if (username == null || username.trim().isEmpty()) {
            responseDTO.setSuccess(false);
            responseDTO.setMessage("账号不能为空");
            return responseDTO;
        }
        String userType = requestDTO.getUserType() != null ? requestDTO.getUserType() : "";
        boolean isAdmin = "admin".equalsIgnoreCase(userType) || (username.length() >= 4 && username.substring(0, 4).equals("0000"));
        boolean isEnterprise = "enterprise".equalsIgnoreCase(userType);
        boolean isUser = "user".equalsIgnoreCase(userType) || (!isAdmin && !isEnterprise);

        // 账号唯一性校验
        if (isAdmin) {
            if (adminUserMapper.findAdminByAccount(username) != null) {
                responseDTO.setSuccess(false);
                responseDTO.setMessage("账号已存在，请更换账号");
                return responseDTO;
            }
        } else if (isEnterprise) {
            if (enterpriseUserMapper.findByAccount(username) != null) {
                responseDTO.setSuccess(false);
                responseDTO.setMessage("账号已存在，请更换账号");
                return responseDTO;
            }
        } else if (isUser) {
            if (userMapper.findByAccount(username) != null) {
                responseDTO.setSuccess(false);
                responseDTO.setMessage("账号已存在，请更换账号");
                return responseDTO;
            }
        }

        // 1. 校验密码格式
        String password = requestDTO.getPassword() != null ? requestDTO.getPassword() : "";
        if (!validatePassword(password)) {
            responseDTO.setSuccess(false);
            responseDTO.setMessage("密码格式错误，需为6-12位数字和字母组合");
            return responseDTO;
        }
        String confirmPassword = requestDTO.getConfirmPassword() != null ? requestDTO.getConfirmPassword() : "";
        if (!password.equals(confirmPassword)) {
            responseDTO.setSuccess(false);
            responseDTO.setMessage("两次输入的密码不一致");
            return responseDTO;
        }

        // 2. 验证码校验
        String verificationCode = requestDTO.getVerificationCode();
        String uuid = requestDTO.getUuid();
        try {
            String storedCaptcha = redisUtil.get("captcha:" + uuid);
            if (verificationCode == null || verificationCode.isEmpty()) {
                responseDTO.setSuccess(false);
                responseDTO.setMessage("验证码不能为空");
                return responseDTO;
            }
            if (storedCaptcha == null || storedCaptcha.isEmpty()) {
                responseDTO.setSuccess(false);
                responseDTO.setMessage("验证码已过期，请重新获取");
                return responseDTO;
            }
            boolean isValid = captchaUtil.validateCaptcha(verificationCode, storedCaptcha);
            if (!isValid) {
                responseDTO.setSuccess(false);
                responseDTO.setMessage("验证码错误");
                return responseDTO;
            }
        } catch (Exception e) {
            responseDTO.setSuccess(false);
            responseDTO.setMessage("验证码校验失败: " + e.getMessage());
            return responseDTO;
        }

        // 3. 按用户类型分别注册
        try {
            if (isAdmin) {
                // 管理员注册
                AdminUser adminUser = new AdminUser();
                adminUser.setId(null);
                adminUser.setUsername(username);
                adminUser.setPassword(password);
                adminUser.setRealName(requestDTO.getRealName());
                adminUser.setPhone(requestDTO.getPhone() != null ? requestDTO.getPhone() : "");
                adminUser.setEmail(requestDTO.getEmail() != null ? requestDTO.getEmail() : "");
                adminUser.setUserType("ADMIN"); // 设置管理员类型
                adminUser.setNickname(requestDTO.getNickname() != null ? requestDTO.getNickname() : adminUser.getRealName());
                adminUser.setDepartment(requestDTO.getDepartment() != null ? requestDTO.getDepartment() : "");
                adminUser.setStatus(1);
                adminUserMapper.save(adminUser);
                responseDTO.setSuccess(true);
                responseDTO.setMessage("注册成功（管理员）");
                responseDTO.setUserId(adminUser.getId() == null ? UUID.randomUUID().toString() : adminUser.getId().toString());
            } else if (isEnterprise) {
                // 企业用户注册
                String enterpriseId = requestDTO.getEnterpriseId() != null ? requestDTO.getEnterpriseId() : "";
                if (enterpriseId.isEmpty()) {
                    responseDTO.setSuccess(false);
                    responseDTO.setMessage("企业ID不能为空");
                    return responseDTO;
                }
                Enterprise enterprise = enterpriseInfoMapper.findByEnterpriseId(enterpriseId);
                if (enterprise == null) {
                    responseDTO.setSuccess(false);
                    responseDTO.setMessage("企业不存在");
                    return responseDTO;
                }
                EnterpriseUser user = new EnterpriseUser();
                user.setId(null);
                user.setUsername(username);
                user.setPassword(password);
                user.setRealName(requestDTO.getRealName());
                user.setPhone(requestDTO.getPhone() != null ? requestDTO.getPhone() : "");
                user.setEmail(requestDTO.getEmail() != null ? requestDTO.getEmail() : "");
                user.setUserType("ENTERPRISE"); // 设置企业用户类型
                user.setEnterpriseId(enterpriseId);
                user.setNickname(requestDTO.getNickname() != null ? requestDTO.getNickname() : user.getRealName());
                user.setStatus(1);
                enterpriseUserMapper.saveUser(user);
                responseDTO.setSuccess(true);
                responseDTO.setMessage("注册成功（企业用户）");
                responseDTO.setUserId(user.getId() == null ? UUID.randomUUID().toString() : user.getId().toString());
            } else if (isUser) {
                // 普通用户注册
                com.cemenghui.system.entity.User user = new com.cemenghui.system.entity.User();
                user.setId(null);
                user.setUsername(username);
                user.setPassword(password);
                user.setRealName(requestDTO.getRealName());
                user.setPhone(requestDTO.getPhone() != null ? requestDTO.getPhone() : "");
                user.setEmail(requestDTO.getEmail() != null ? requestDTO.getEmail() : "");
                user.setUserType("NORMAL"); // 设置普通用户类型
                user.setEnterpriseId(""); // 普通用户无企业ID
                user.setNickname(requestDTO.getNickname() != null ? requestDTO.getNickname() : user.getRealName());
                user.setStatus(1);
                userMapper.saveUser(user);
                responseDTO.setSuccess(true);
                responseDTO.setMessage("注册成功（普通用户）");
                responseDTO.setUserId(user.getId() == null ? UUID.randomUUID().toString() : user.getId().toString());
            } else {
                responseDTO.setSuccess(false);
                responseDTO.setMessage("未知用户类型");
            }
        } catch (Exception e) {
            responseDTO.setSuccess(false);
            responseDTO.setMessage("注册失败: " + e.getMessage());
        }
        return responseDTO;
    }

    @Override
    public boolean validateEnterprise(String enterpriseName) {
        // 从数据库查询企业是否存在（示例逻辑，需根据实际 Mapper 调整）
        return enterpriseInfoMapper.countByEnterpriseName(enterpriseName) > 0;
    }

    @Override
    public void syncBusinessInfo(RegisterRequestDTO requestDTO) {
        if (StringUtils.hasText(requestDTO.getEnterpriseName())) {
            // 从数据库查询企业信息（示例逻辑）
            // EnterpriseInfo info = enterpriseInfoMapper.findByEnterpriseName(requestDTO.getEnterpriseName());
            // requestDTO.setUnifiedSocialCreditCode(info.getSocialCreditCode()); 
            // requestDTO.setRegistrationAddress(info.getRegisterAddress());
        }
    }

    // 密码格式校验
    private boolean validatePassword(String password) {
        return Pattern.matches(PASSWORD_PATTERN, password);
    }

    // 不再需要单独的validateCaptcha方法，直接用captchaUtil
}
