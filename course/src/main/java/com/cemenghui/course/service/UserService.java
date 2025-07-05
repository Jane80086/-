package com.cemenghui.course.service;

import com.cemenghui.entity.User;
import java.util.List;
import java.util.Optional;

/**
 * Course模块用户服务接口
 * 继承main-app的UserService，添加course模块特有的方法
 */
public interface UserService extends com.cemenghui.service.UserService {
    
    /**
     * 根据用户名查找用户（返回Optional）
     */
    Optional<User> findByUsername(String username);
    
    /**
     * 保存用户
     */
    User saveUser(User user);
    
    /**
     * 检查用户是否存在
     */
    boolean existsById(Long id);
    
    /**
     * 获取用户总数
     */
    long count();
    
    /**
     * 分页获取用户
     */
    List<User> getUsersByPage(int page, int size);
    
    /**
     * 获取所有用户
     */
    List<User> getAllUsers();
    
    /**
     * 检查用户名是否存在
     */
    boolean existsByUsername(String username);
    
    /**
     * 根据用户名删除用户
     */
    void deleteByUsername(String username);
} 