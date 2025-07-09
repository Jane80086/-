package com.cemenghui.system.service;

import com.cemenghui.system.dto.RegisterRequestDTO;
import com.cemenghui.system.dto.RegistResponseDTO;
import com.cemenghui.system.util.CaptchaUtil;

public interface RegisterService {

    /**
     * 执行注册核心逻辑
     * @param requestDTO 注册请求参数
     * @param checkCaptcha 是否校验验证码
     * @param captchaUtil 验证码工具
     * @return 注册结果
     */
    RegistResponseDTO register(RegisterRequestDTO requestDTO, boolean checkCaptcha, CaptchaUtil captchaUtil);

    /**
     * 校验企业名称是否存在（从数据库查询 ）
     * @param enterpriseName 企业名称
     * @return true=存在，false=不存在
     */
    boolean validateEnterprise(String enterpriseName);

    /**
     * 同步企业信息（从数据库查询并填充到 DTO ）
     * @param requestDTO 包含企业名称的 DTO
     */
    void syncBusinessInfo(RegisterRequestDTO requestDTO);
}