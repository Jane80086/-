package com.cemenghui.system.controller;

import com.cemenghui.common.EnterpriseUser;
import com.cemenghui.system.service.UserManagementService;
import com.cemenghui.system.util.JWTUtil;
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
    public ResultVO<EnterpriseUser> getCurrentUser(@RequestHeader("Authorization") String token) {
        String account = jwtUtil.getAccountFromToken(token);
        EnterpriseUser user = userService.getUserByAccount(account);
        return ResultVO.success(user);
    }

    @PutMapping("/current")
    public ResultVO<Boolean> updateCurrentUser(@RequestBody EnterpriseUser user, @RequestHeader("Authorization") String token) {
        String account = jwtUtil.getAccountFromToken(token);
        user.setAccount(account);
        boolean result = userService.updateUserByAccount(user);
        return ResultVO.success(result);
    }
} 