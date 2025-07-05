package com.cemenghui.service.impl;

import com.cemenghui.dao.UserDao;
import com.cemenghui.entity.User;
import com.cemenghui.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 统一用户服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    
    private final UserDao userDao;
    
    @Override
    public User findById(Long id) {
        return userDao.selectById(id);
    }
    
    @Override
    public User findByUsername(String username) {
        return userDao.findByUsername(username);
    }
    
    @Override
    public User findByEmail(String email) {
        return userDao.findByEmail(email);
    }
    
    @Override
    public User findByPhone(String phone) {
        return userDao.findByPhone(phone);
    }
    
    @Override
    public List<User> findByUserType(String userType) {
        return userDao.findByUserType(userType);
    }
    
    @Override
    public List<User> findByEnterpriseId(String enterpriseId) {
        return userDao.findByEnterpriseId(enterpriseId);
    }
    
    @Override
    public List<User> findEnabledUsers() {
        return userDao.findEnabledUsers();
    }
    
    @Override
    public List<User> findDisabledUsers() {
        return userDao.findDisabledUsers();
    }
    
    @Override
    @Transactional
    public User createUser(User user) {
        // 设置创建时间和更新时间
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        
        // 加密密码
        if (user.getPassword() != null) {
            user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
        }
        
        userDao.insert(user);
        log.info("创建用户成功: {}", user.getUsername());
        return user;
    }
    
    @Override
    @Transactional
    public User updateUser(User user) {
        // 设置更新时间
        user.setUpdateTime(LocalDateTime.now());
        
        // 如果密码不为空，则加密密码
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
        }
        
        userDao.updateById(user);
        log.info("更新用户成功: {}", user.getUsername());
        return user;
    }
    
    @Override
    @Transactional
    public boolean deleteUser(Long id) {
        User user = userDao.selectById(id);
        if (user == null) {
            return false;
        }
        
        userDao.deleteById(id);
        log.info("删除用户成功: {}", user.getUsername());
        return true;
    }
    
    @Override
    @Transactional
    public boolean enableUser(Long id) {
        User user = userDao.selectById(id);
        if (user == null) {
            return false;
        }
        
        user.setStatus(User.Status.ENABLED.getCode());
        user.setUpdateTime(LocalDateTime.now());
        userDao.updateById(user);
        log.info("启用用户成功: {}", user.getUsername());
        return true;
    }
    
    @Override
    @Transactional
    public boolean disableUser(Long id) {
        User user = userDao.selectById(id);
        if (user == null) {
            return false;
        }
        
        user.setStatus(User.Status.DISABLED.getCode());
        user.setUpdateTime(LocalDateTime.now());
        userDao.updateById(user);
        log.info("禁用用户成功: {}", user.getUsername());
        return true;
    }
    
    @Override
    public User validateLogin(String username, String password) {
        User user = userDao.findByUsername(username);
        if (user == null) {
            return null;
        }
        
        // 验证密码
        String encryptedPassword = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!encryptedPassword.equals(user.getPassword())) {
            return null;
        }
        
        // 检查用户状态
        if (!user.isEnabled()) {
            return null;
        }
        
        return user;
    }
    
    @Override
    public boolean isUsernameExists(String username) {
        return userDao.findByUsername(username) != null;
    }
    
    @Override
    public boolean isEmailExists(String email) {
        return userDao.findByEmail(email) != null;
    }
    
    @Override
    public boolean isPhoneExists(String phone) {
        return userDao.findByPhone(phone) != null;
    }
    
    @Override
    public List<User> findAdminUsers() {
        return userDao.findByUserType(User.UserType.ADMIN.name());
    }
    
    @Override
    public List<User> findEnterpriseUsers() {
        return userDao.findByUserType(User.UserType.ENTERPRISE.name());
    }
    
    @Override
    public List<User> findNormalUsers() {
        return userDao.findByUserType(User.UserType.NORMAL.name());
    }
    
    @Override
    public List<User> findSystemUsers() {
        return userDao.findByUserType(User.UserType.SYSTEM.name());
    }
    
    @Override
    public String getNicknameById(Long id) {
        User user = userDao.selectById(id);
        return user != null ? user.getNickname() : null;
    }
    
    @Override
    public String getAvatarById(Long id) {
        User user = userDao.selectById(id);
        return user != null ? user.getAvatar() : null;
    }
} 