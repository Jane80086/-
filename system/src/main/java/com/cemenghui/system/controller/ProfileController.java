package com.cemenghui.system.controller;



import com.cemenghui.system.entity.EnterpriseUser;

import com.cemenghui.system.service.UserManagementService;

import com.cemenghui.common.JWTUtil;

import com.cemenghui.system.vo.ResultVO;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;



@RestController

@RequestMapping("/api/user")

public class ProfileController {



    @Autowired

    private UserManagementService userService;

    @Autowired

    private JWTUtil jwtUtil;



    @GetMapping("/current")

    public ResultVO<? extends Object> getCurrentUser(@RequestHeader("Authorization") String token) {

        String cleanToken = jwtUtil.extractTokenFromHeader(token);

        if (cleanToken == null) {

            return ResultVO.error("无效的token");

        }

        String account = jwtUtil.getAccountFromToken(cleanToken);

        EnterpriseUser enterpriseUser = userService.getUserByAccount(account);

        return ResultVO.success(enterpriseUser);

    }



    @PutMapping("/current")

    public ResultVO<Boolean> updateCurrentUser(@RequestBody EnterpriseUser user, @RequestHeader("Authorization") String token) {

        String cleanToken = jwtUtil.extractTokenFromHeader(token);

        if (cleanToken == null) {

            return ResultVO.error("无效的token");

        }

        String account = jwtUtil.getAccountFromToken(cleanToken);

        user.setUsername(account);

        boolean result = userService.updateUserByAccount(user);

        return ResultVO.success(result);

    }

} 

