package com.cemenghui.system.service;

import com.cemenghui.common.User;
import com.cemenghui.system.dto.LoginResponseDTO;
import com.cemenghui.system.dto.LoginResponseDTO; // 改用DTO

/**
 * 登录业务逻辑接口
 */
public interface LoginService {

    /**
     * 用户登录方法
     *
     * @param account  账号
     * @param password 密码
     * @return LoginResponseDTO 登录结果（含用户信息、token等）
     */
    LoginResponseDTO login(String account, String password);

    /**
     * 校验动态验证码（若需）
     * @param account     账号
     * @param dynamicCode 动态码
     * @return 是否校验通过
     */
    boolean checkDynamicCode(String account, String dynamicCode);

    /**
     * 生成登录令牌（如JWT）
     * @param user 用户实体
     * @return 令牌字符串
     */
    String generateToken(User user);

    /**
     * 校验令牌有效性
     * @param token 令牌
     * @return 是否有效
     */
    boolean validateToken(String token);

    /**
     * 从令牌解析用户信息
     *
     * @param token 令牌
     * @return 用户实体（无效时返回null）
     */
    String getUserFromToken(String token);
}