package com.system.controller;

import com.system.dto.RegisterRequestDTO;
import com.system.dto.RegistResponseDTO;
import com.system.service.RegisterService;
import com.system.util.CaptchaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/register")
@Validated // 开启方法参数校验
public class RegisterController {

    @Autowired
    private RegisterService registerService;

    /**
     * 企业用户注册接口
     *
     * @param requestDTO  注册请求参数（包含企业名称、账号、密码等 ）
     * @param captchaUtil
     * @return 注册结果（成功/失败、提示信息 ）
     */
    @PostMapping
    public RegistResponseDTO register(@RequestBody @Valid RegisterRequestDTO requestDTO, CaptchaUtil captchaUtil) {
        // 调用服务层执行注册逻辑
        return registerService.register(requestDTO, captchaUtil);
    }

    /**
     * 校验企业名称是否存在（用于前端实时校验 ）
     * @param enterpriseName 企业名称
     * @return true=存在，false=不存在
     */
    @PostMapping("/checkEnterpriseName")
    public boolean validateEnterpriseName(String enterpriseName) {
        return registerService.validateEnterprise(enterpriseName);
    }

    /**
     * 同步企业信息（输入企业名称后，预填充统一社会信用代码等 ）
     * @param requestDTO 包含企业名称的请求体
     * @return 补充了企业信息的 DTO
     */
    @PostMapping("/syncBusinessInfo")
    public RegisterRequestDTO syncBusinessInfo(@RequestBody RegisterRequestDTO requestDTO) {
        registerService.syncBusinessInfo(requestDTO);
        return requestDTO;
    }
    
}