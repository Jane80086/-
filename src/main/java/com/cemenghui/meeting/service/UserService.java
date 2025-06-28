package com.cemenghui.meeting.service;

import com.cemenghui.meeting.entity.User;
import com.cemenghui.meeting.entity.UserLoginRequest;
import com.cemenghui.meeting.entity.UserRegisterRequest;
import com.cemenghui.meeting.dao.UserDao;
import com.cemenghui.meeting.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserDao userDao;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 用户注册
     */
    public User register(UserRegisterRequest request) {
        // 检查用户名是否已存在
        User existingUser = userDao.findByUsername(request.getUsername());
        if (existingUser != null) {
            throw new IllegalArgumentException("用户名已存在");
        }
        
        // 创建用户对象
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(encryptPassword(request.getPassword()));
        user.setRealName(request.getRealName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setUserType(request.getUserType());
        user.setStatus(1); // 默认启用
        
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
        User user = userDao.findByUsernameAndPassword(
            request.getUsername(), 
            encryptPassword(request.getPassword())
        );
        
        if (user == null) {
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
        User user = userDao.findById(id);
        if (user != null) {
            user.setPassword(null); // 清除密码
        }
        return user;
    }
    
    /**
     * 更新用户基本信息
     */
    public boolean updateUserInfo(User user) {
        return userDao.updateBasicInfo(user) > 0;
    }
    
    /**
     * 更新用户密码
     */
    public boolean updatePassword(Long userId, String newPassword) {
        return userDao.updatePassword(userId, encryptPassword(newPassword)) > 0;
    }
    
    /**
     * 更新用户状态
     */
    public boolean updateUserStatus(Long userId, Integer status) {
        return userDao.updateStatus(userId, status) > 0;
    }
    
    /**
     * 获取所有用户
     */
    public List<User> getAllUsers() {
        List<User> users = userDao.findAll();
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
     * 删除用户
     */
    public boolean deleteUser(Long userId) {
        return userDao.deleteById(userId) > 0;
    }
    
    /**
     * 加密密码
     */
    private String encryptPassword(String password) {
        return DigestUtils.md5DigestAsHex(password.getBytes());
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