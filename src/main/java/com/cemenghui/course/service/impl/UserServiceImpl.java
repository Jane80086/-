package com.cemenghui.course.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.cemenghui.course.dao.UserDao;
import com.cemenghui.course.entity.User;
import com.cemenghui.course.entity.UserType;
import java.util.List;
import java.util.Optional;

/**
 * 用户服务
 */
@Service
public class UserServiceImpl {
    @Autowired
    private UserDao userDao;

    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return 用户对象（可选）
     */
    public Optional<User> findByUsername(String username) {
        return userDao.findByUsername(username);
    }

    /**
     * 根据用户类型查找用户列表
     * @param userType 用户类型
     * @return 用户列表
     */
    public List<User> findByUserType(UserType userType) {
        return userDao.findByUserType(userType);
    }

    /**
     * 根据ID查找用户
     * @param id 用户ID
     * @return 用户对象或null
     */
    public User getById(Long id) {
        return userDao.findById(id).orElse(null);
    }

    // 事件钩子示例
    protected void onUserLogin(Long Id) {
        // 记录用户登录行为
    }
} 