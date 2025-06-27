package com.cemenghui.course.controller;

import com.cemenghui.course.common.User;
import com.cemenghui.course.service.impl.UserServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@Validated
public class UserController {
    @Autowired
    private UserServiceImpl userService;

    @PostMapping("/add")
    public String addUser(@RequestBody @Valid User user) {
        // 这里只做示例，实际应调用service保存
        return "用户添加成功: " + user.getUsername();
    }

    @PostMapping("/update")
    public String updateUser(@RequestBody @Valid User user) {
        // 这里只做示例，实际应调用service更新
        return "用户更新成功: " + user.getUsername();
    }
} 