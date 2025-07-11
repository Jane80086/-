package com.cemenghui.system.service.impl;

import com.cemenghui.system.dto.RegisterRequestDTO;
import com.cemenghui.system.dto.RegistResponseDTO;
import com.cemenghui.system.entity.EnterpriseUser;
import com.cemenghui.system.entity.Enterprise;
import com.cemenghui.system.entity.AdminUser;
import com.cemenghui.system.repository.EnterpriseMapper;
import com.cemenghui.system.repository.EnterpriseUserMapper;
import com.cemenghui.system.repository.AdminUserMapper;
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
    private RedisUtil redisUtil;

    // 企业联系方式正则：区号-电话号码
    private static final String CONTACT_PATTERN = "^\\d{3,4}-\\d{7,8}$";
    // 密码正则：6-12位数字+字母
    private static final String PASSWORD_PATTERN = "^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d]{6,12}$";

    @Override
    public RegistResponseDTO register(RegisterRequestDTO requestDTO, CaptchaUtil captchaUtil) {
        RegistResponseDTO responseDTO = new RegistResponseDTO();

        String username = requestDTO.getAccount() != null ? requestDTO.getAccount() : "";
        boolean isAdmin = username.length() >= 4 && username.substring(0, 4).equals("0000");

        // 1. 仅普通用户校验企业ID和企业存在性，管理员可不填
        String enterpriseId = requestDTO.getEnterpriseId() != null ? requestDTO.getEnterpriseId() : "";
        if (!isAdmin) {
            System.out.println("注册时收到企业ID: [" + enterpriseId + "]");
            if (enterpriseId.isEmpty()) {
                responseDTO.setSuccess(false);
                responseDTO.setMessage("企业ID不能为空");
                return responseDTO;
            }
            Enterprise enterprise = enterpriseInfoMapper.findByEnterpriseId(enterpriseId);
            System.out.println("查到的企业: " + enterprise);
            if (enterprise == null) {
                responseDTO.setSuccess(false);
                responseDTO.setMessage("企业不存在");
                return responseDTO;
            }
        }

        // 1. 基础校验（可通过 @Valid 注解在 Controller 层提前拦截，这里做补充校验）
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
        String contact = requestDTO.getEnterpriseContact() != null ? requestDTO.getEnterpriseContact() : "";
        if (!contact.isEmpty() && !Pattern.matches(CONTACT_PATTERN, contact)) {
            responseDTO.setSuccess(false);
            responseDTO.setMessage("企业联系方式格式错误，需为 区号-电话号码");
            return responseDTO;
        }
        // 验证码校验（从Redis获取）
        String verificationCode = requestDTO.getVerificationCode();
        String uuid = requestDTO.getUuid(); // 使用前端传递的uuid
        System.out.println("注册验证码校验 - 验证码: [" + verificationCode + "], UUID: [" + uuid + "]");
        
        try {
            String storedCaptcha = redisUtil.get("captcha:" + uuid);
            System.out.println("从Redis获取的验证码: [" + storedCaptcha + "]");
            
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
            System.out.println("验证码验证结果: [" + isValid + "]");
            
            if (!isValid) {
                responseDTO.setSuccess(false);
                responseDTO.setMessage("验证码错误");
                return responseDTO;
            }
        } catch (Exception e) {
            System.out.println("验证码校验异常: " + e.getMessage());
            e.printStackTrace();
            responseDTO.setSuccess(false);
            responseDTO.setMessage("验证码校验失败: " + e.getMessage());
            return responseDTO;
        }

        // 2. 判断账号前4位，决定注册为管理员还是普通用户
        try {
            if (isAdmin) {
                // 注册前查重
                AdminUser exist = adminUserMapper.findAdminByAccount(username);
                if (exist != null) {
                    responseDTO.setSuccess(false);
                    responseDTO.setMessage("账号已存在，请更换账号");
                    return responseDTO;
                }
                // 注册为管理员
                AdminUser adminUser = new AdminUser();
                adminUser.setId(null); // 让数据库自动生成ID
                adminUser.setUsername(username);
                adminUser.setPassword(password);
                adminUser.setRealName(requestDTO.getRealName());
                adminUser.setPhone(requestDTO.getPhone() != null ? requestDTO.getPhone() : "");
                adminUser.setEmail(requestDTO.getEmail() != null ? requestDTO.getEmail() : "");
                adminUser.setNickname(requestDTO.getNickname() != null ? requestDTO.getNickname() : adminUser.getRealName());
                adminUser.setDepartment(requestDTO.getDepartment() != null ? requestDTO.getDepartment() : "");
                adminUser.setStatus(1); // 启用状态
                System.out.println("注册写入数据库的管理员实体：" + adminUser);
                adminUserMapper.save(adminUser);
                responseDTO.setSuccess(true);
                responseDTO.setMessage("注册成功（管理员）");
                responseDTO.setUserId(adminUser.getId() == null ? UUID.randomUUID().toString() : adminUser.getId().toString());
            } else {
                // 注册为普通用户
                EnterpriseUser user = new EnterpriseUser();
                user.setId(null); // 让数据库自动生成ID
                user.setUsername(username);
                user.setPassword(password);
                user.setRealName(requestDTO.getRealName());
                user.setPhone(requestDTO.getPhone() != null ? requestDTO.getPhone() : "");
                user.setEmail(requestDTO.getEmail() != null ? requestDTO.getEmail() : "");
                user.setEnterpriseId(enterpriseId);
                user.setNickname(requestDTO.getNickname() != null ? requestDTO.getNickname() : user.getRealName());
                user.setStatus(1); // 启用状态
                System.out.println("注册写入数据库的用户实体：" + user);
                enterpriseUserMapper.saveUser(user);
                responseDTO.setSuccess(true);
                responseDTO.setMessage("注册成功");
                responseDTO.setUserId(user.getId() == null ? UUID.randomUUID().toString() : user.getId().toString());
            }
        } catch (Exception e) {
            responseDTO.setSuccess(false);
            responseDTO.setMessage("注册失败：" + e.getMessage());
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
