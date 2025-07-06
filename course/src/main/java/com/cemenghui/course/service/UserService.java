package com.cemenghui.course.service;

import com.cemenghui.entity.User;
import java.util.List;
import java.util.Optional;

/**
 * Course模块用户服务接口
 * 独立定义course模块需要的用户服务方法
 */
public interface UserService {
    
    /**
     * 根据用户名查找用户（返回Optional）
     */
    Optional<User> findByUsername(String username);
    
    /**
     * 根据ID查找用户
     */
    User findById(Long id);
    
    /**
     * 根据邮箱查找用户
     */
    User findByEmail(String email);
    
    /**
     * 根据手机号查找用户
     */
    User findByPhone(String phone);
    
    /**
     * 根据用户类型查找用户列表
     */
    List<User> findByUserType(String userType);
    
    /**
     * 根据企业ID查找企业用户
     */
    List<User> findByEnterpriseId(String enterpriseId);
    
    /**
     * 查找所有启用的用户
     */
    List<User> findEnabledUsers();
    
    /**
     * 查找所有禁用的用户
     */
    List<User> findDisabledUsers();
    
    /**
     * 创建用户
     */
    User createUser(User user);
    
    /**
     * 更新用户
     */
    User updateUser(User user);
    
    /**
     * 删除用户（软删除）
     */
    boolean deleteUser(Long id);
    
    /**
     * 启用用户
     */
    boolean enableUser(Long id);
    
    /**
     * 禁用用户
     */
    boolean disableUser(Long id);
    
    /**
     * 验证用户登录
     */
    User validateLogin(String username, String password);
    
    /**
     * 检查用户名是否存在
     */
    boolean isUsernameExists(String username);
    
    /**
     * 检查邮箱是否存在
     */
    boolean isEmailExists(String email);
    
    /**
     * 检查手机号是否存在
     */
    boolean isPhoneExists(String phone);
    
    /**
     * 查找管理员用户
     */
    List<User> findAdminUsers();
    
    /**
     * 查找企业用户
     */
    List<User> findEnterpriseUsers();
    
    /**
     * 查找普通用户
     */
    List<User> findNormalUsers();
    
    /**
     * 查找系统用户
     */
    List<User> findSystemUsers();
    
    /**
     * 根据用户ID获取昵称
     */
    String getNicknameById(Long id);
    
    /**
     * 根据用户ID获取头像URL
     */
    String getAvatarById(Long id);
    
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