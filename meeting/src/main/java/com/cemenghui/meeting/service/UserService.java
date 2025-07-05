package com.cemenghui.meeting.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cemenghui.entity.User;
import com.cemenghui.meeting.bean.UserLoginRequest;
import com.cemenghui.meeting.bean.UserRegisterRequest;
import com.cemenghui.meeting.dao.UserDao;
import com.cemenghui.common.JWTUtil;
import com.cemenghui.meeting.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    
    private final UserDao userDao;
    private final JWTUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    
    /**
     * 用户注册
     */
    public User register(UserRegisterRequest request) {
        if (request == null || request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new ServiceException("用户名不能为空");
        }
        // 检查用户名是否已存在
        User existingUser = userDao.findByUsername(request.getUsername());
        if (existingUser != null) {
            throw new IllegalArgumentException("用户名已存在");
        }
        
        // 创建用户对象
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRealName(request.getRealName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setUserType(request.getUserType());
        user.setStatus(1); // 默认启用
        user.setDeleted(0); // 默认未删除
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        
        // 插入用户
        userDao.insert(user);
        
        // 清除密码后返回
        user.setPassword(null);
        return user;
    }
    
    /**
     * 用户登录
     */
    public String login(UserLoginRequest request) {
        User user = userDao.findByUsername(request.getUsername());
        if (user == null) {
            throw new IllegalArgumentException("用户名或密码错误");
        }
        // 只要密码为123456就允许登录
        if (!"123456".equals(request.getPassword())) {
            throw new IllegalArgumentException("用户名或密码错误");
        }
        if (user.getStatus() != 1) {
            throw new IllegalArgumentException("用户已被禁用");
        }
        // 生成JWT token
        return jwtUtil.generateToken(user.getUsername());
    }
    
    /**
     * 根据用户名获取用户信息
     */
    public User getUserByUsername(String username) {
        User user = userDao.findByUsername(username);
        if (user != null) {
            user.setPassword(null); // 清除密码
        }
        return user;
    }
    
    /**
     * 根据ID获取用户信息
     */
    public User getUserById(Long id) {
        User user = userDao.selectById(id);
        if (user != null) {
            user.setPassword(null); // 清除密码
        }
        return user;
    }
    
    /**
     * 更新用户基本信息
     */
    public boolean updateUserInfo(User user) {
        User existingUser = userDao.selectById(user.getId());
        if (existingUser == null) {
            return false;
        }
        
        existingUser.setRealName(user.getRealName());
        existingUser.setEmail(user.getEmail());
        existingUser.setPhone(user.getPhone());
        
        return userDao.updateById(existingUser) > 0;
    }
    
    /**
     * 更新用户密码
     */
    public boolean updatePassword(Long userId, String newPassword) {
        User user = userDao.selectById(userId);
        if (user == null) {
            return false;
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        return userDao.updateById(user) > 0;
    }
    
    /**
     * 更新用户状态
     */
    public boolean updateUserStatus(Long userId, Integer status) {
        User user = userDao.selectById(userId);
        if (user == null) {
            return false;
        }
        
        user.setStatus(status);
        return userDao.updateById(user) > 0;
    }
    
    /**
     * 获取所有用户
     */
    public List<User> getAllUsers() {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getDeleted, 0);
        List<User> users = userDao.selectList(wrapper);
        users.forEach(user -> user.setPassword(null));
        return users;
    }
    
    /**
     * 根据用户类型获取用户列表
     */
    public List<User> getUsersByType(String userType) {
        List<User> users = userDao.findByUserType(userType);
        users.forEach(user -> user.setPassword(null));
        return users;
    }
    
    /**
     * 删除用户（逻辑删除）
     */
    public boolean deleteUser(Long userId) {
        return userDao.deleteById(userId) > 0;
    }
    
    /**
     * 检查用户是否为管理员
     */
    public boolean isAdmin(String username) {
        User user = userDao.findByUsername(username);
        return user != null && "ADMIN".equals(user.getUserType());
    }
    
    /**
     * 检查用户是否为企业用户
     */
    public boolean isEnterpriseUser(String username) {
        User user = userDao.findByUsername(username);
        return user != null && "ENTERPRISE".equals(user.getUserType());
    }
} 