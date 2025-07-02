package com.system.service.impl;

import com.system.dto.RegisterRequestDTO;
import com.system.dto.RegistResponseDTO;
import com.system.entity.EnterpriseUser;
import com.system.entity.Enterprise;
import com.system.repository.EnterpriseMapper;
import com.system.repository.EnterpriseUserMapper;
import com.system.service.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

@Service
public class RegisterServiceImpl implements RegisterService {

    @Autowired
    private EnterpriseUserMapper enterpriseUserMapper; // 假设的用户 Mapper
    @Autowired
    private EnterpriseMapper enterpriseInfoMapper; // 假设的企业信息 Mapper

    // 企业联系方式正则：区号-电话号码
    private static final String CONTACT_PATTERN = "^\\d{3,4}-\\d{7,8}$";
    // 密码正则：6-12位数字+字母
    private static final String PASSWORD_PATTERN = "^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d]{6,12}$";

    @Override
    public RegistResponseDTO register(RegisterRequestDTO requestDTO) {
        RegistResponseDTO responseDTO = new RegistResponseDTO();

        // 1. 校验企业ID是否存在（逻辑必须在最前面，且类型一致）
        String enterpriseId = requestDTO.getEnterpriseId() != null ? requestDTO.getEnterpriseId() : "";
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

        // 0. 校验企业ID是否存在，不存在则返回错误信息
        if (requestDTO.getEnterpriseId() != null && !requestDTO.getEnterpriseId().isEmpty()) {
            enterprise = enterpriseInfoMapper.findByEnterpriseId(requestDTO.getEnterpriseId());
            if (enterprise == null) {
                responseDTO.setSuccess(false);
                responseDTO.setMessage("企业不存在");
                return responseDTO;
            }
        }

        // 1. 基础校验（可通过 @Valid 注解在 Controller 层提前拦截，这里做补充校验 ）
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
        // 验证码校验（假设调用工具类或第三方服务 ）
        String verificationCode = requestDTO.getVerificationCode() != null ? requestDTO.getVerificationCode() : "";
        if (!validateCaptcha(verificationCode)) {
            responseDTO.setSuccess(false);
            responseDTO.setMessage("验证码错误或已过期");
            return responseDTO;
        }

        // 2. 组装用户实体，插入数据库
        try {
            EnterpriseUser user = new EnterpriseUser();
            user.setUserId(java.util.UUID.randomUUID().toString().replace("-", ""));
            user.setAccount(requestDTO.getAccount() != null ? requestDTO.getAccount() : "");
            user.setPassword(password);
            user.setRealName(requestDTO.getRealName());
            user.setPhone(requestDTO.getPhone() != null ? requestDTO.getPhone() : "");
            user.setEmail(requestDTO.getEmail() != null ? requestDTO.getEmail() : "");
            user.setEnterpriseId(enterpriseId);
            user.setNickname(requestDTO.getNickname() != null ? requestDTO.getNickname() : user.getRealName());
            user.setStatus("1");
            System.out.println("注册写入数据库的用户实体：" + user);
            enterpriseUserMapper.saveUser(user);
            responseDTO.setSuccess(true);
            responseDTO.setMessage("注册成功");
            responseDTO.setUserId(user.getUserId());
        } catch (Exception e) {
            responseDTO.setSuccess(false);
            responseDTO.setMessage("注册失败：" + e.getMessage());
        }
        return responseDTO;
    }

    @Override
    public boolean validateEnterprise(String enterpriseName) {
        // 从数据库查询企业是否存在（示例逻辑，需根据实际 Mapper 调整 ）
        return enterpriseInfoMapper.countByEnterpriseName(enterpriseName) > 0;
    }

    @Override
    public void syncBusinessInfo(RegisterRequestDTO requestDTO) {
        if (StringUtils.hasText(requestDTO.getEnterpriseName())) {
            // 从数据库查询企业信息（示例逻辑 ）
            // EnterpriseInfo info = enterpriseInfoMapper.findByEnterpriseName(requestDTO.getEnterpriseName());
            // requestDTO.setUnifiedSocialCreditCode(info.getSocialCreditCode()); 
            // requestDTO.setRegistrationAddress(info.getRegisterAddress());
        }
    }

    // 密码格式校验
    private boolean validatePassword(String password) {
        return Pattern.matches(PASSWORD_PATTERN, password);
    }

    // 验证码校验（示例，需对接实际验证码服务 ）
    private boolean validateCaptcha(String captcha) {
        // 省略：调用 Redis 或验证码服务校验逻辑
        return true;
    }
}
