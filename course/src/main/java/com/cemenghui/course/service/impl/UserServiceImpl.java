package com.cemenghui.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.cemenghui.course.dao.UserDao;
import com.cemenghui.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cemenghui.course.service.UserService;
import java.time.LocalDateTime;

/**
 * 用户服务
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;

    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return 用户对象（可选）
     */
    @Override
    public Optional<User> findByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        User user = userDao.selectOne(wrapper);
        return Optional.ofNullable(user);
    }

    /**
     * 根据用户类型查找用户列表
     * @param userType 用户类型
     * @return 用户列表
     */
    @Override
    public List<User> findByUserType(String userType) {
        try {
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getUserType, userType);
            return userDao.selectList(wrapper);
        } catch (Exception e) {
            System.err.println("根据用户类型查找用户失败: " + e.getMessage());
            return getMockUsers(); // 返回模拟数据
        }
    }

    /**
     * 获取所有用户
     * @return 用户列表
     */
    @Override
    public List<User> getAllUsers() {
        try {
            return userDao.selectList(null);
        } catch (Exception e) {
            System.err.println("获取所有用户失败: " + e.getMessage());
            return getMockUsers(); // 返回模拟数据
        }
    }

    /**
     * 根据ID查找用户
     * @param id 用户ID
     * @return 用户对象或null
     */
    @Override
    public User findById(Long id) {
        try {
        return userDao.selectById(id);
        } catch (Exception e) {
            System.err.println("根据ID查找用户失败: " + e.getMessage());
            return null;
        }
    }



    /**
     * 保存用户
     * @param user 用户对象
     * @return 保存后的用户对象
     */
    @Override
    public User saveUser(User user) {
        try {
            userDao.insert(user);
            return user;
        } catch (Exception e) {
            System.err.println("保存用户失败: " + e.getMessage());
            return null;
        }
    }

    /**
     * 删除用户
     * @param id 用户ID
     * @return 是否删除成功
     */
    @Override
    public boolean deleteUser(Long id) {
        try {
            userDao.deleteById(id);
            return true;
        } catch (Exception e) {
            System.err.println("删除用户失败: " + e.getMessage());
            return false;
        }
    }

    /**
     * 更新用户
     * @param user 用户对象
     * @return 更新后的用户对象
     */
    @Override
    public User updateUser(User user) {
        try {
            if (user.getId() != null && userDao.selectById(user.getId()) != null) {
                userDao.updateById(user);
                return user;
            }
            return null;
        } catch (Exception e) {
            System.err.println("更新用户失败: " + e.getMessage());
            return null;
        }
    }

    /**
     * 检查用户是否存在
     * @param id 用户ID
     * @return 是否存在
     */
    @Override
    public boolean existsById(Long id) {
        try {
            return userDao.selectById(id) != null;
        } catch (Exception e) {
            System.err.println("检查用户是否存在失败: " + e.getMessage());
            return false;
        }
    }

    /**
     * 获取用户总数
     * @return 用户总数
     */
    @Override
    public long count() {
        try {
            return userDao.selectCount(null);
        } catch (Exception e) {
            System.err.println("获取用户总数失败: " + e.getMessage());
            return 0;
        }
    }

    /**
     * 分页获取用户
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @return 用户列表
     */
    @Override
    public List<User> getUsersByPage(int page, int size) {
        try {
            // 这里应该使用Spring Data的分页功能
            // 简化实现，直接返回所有用户
            List<User> allUsers = getAllUsers();
            int start = page * size;
            int end = Math.min(start + size, allUsers.size());
            
            if (start >= allUsers.size()) {
                return new ArrayList<>();
            }
            
            return allUsers.subList(start, end);
        } catch (Exception e) {
            System.err.println("分页获取用户失败: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * 获取模拟用户数据（当数据库不可用时）
     * @return 模拟用户列表
     */
    private List<User> getMockUsers() {
        List<User> mockUsers = new ArrayList<>();
        
        // 添加管理员用户
        User admin1 = new User();
        admin1.setId(1L);
        admin1.setUsername("admin");
        admin1.setEmail("admin@example.com");
        admin1.setUserType("ADMIN");
        mockUsers.add(admin1);
        
        // 添加企业用户
        User enterprise1 = new User();
        enterprise1.setId(2L);
        enterprise1.setUsername("enterprise1");
        enterprise1.setEmail("enterprise1@example.com");
        enterprise1.setUserType("ENTERPRISE");
        mockUsers.add(enterprise1);
        
        // 添加普通用户
        User normal1 = new User();
        normal1.setId(3L);
        normal1.setUsername("user1");
        normal1.setEmail("user1@example.com");
        normal1.setUserType("NORMAL");
        mockUsers.add(normal1);
        
        User normal2 = new User();
        normal2.setId(4L);
        normal2.setUsername("user2");
        normal2.setEmail("user2@example.com");
        normal2.setUserType("NORMAL");
        mockUsers.add(normal2);
        
        return mockUsers;
    }

    // 事件钩子示例
    protected void onUserLogin(Long id) {
        // 记录用户登录行为
        System.out.println("用户 " + id + " 登录了系统");
    }

    @Override
    public boolean existsByUsername(String username) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        return userDao.selectCount(wrapper) > 0;
    }

    @Override
    public void deleteByUsername(String username) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        userDao.delete(wrapper);
    }

    @Override
    public User findByEmail(String email) {
        try {
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getEmail, email);
            return userDao.selectOne(wrapper);
        } catch (Exception e) {
            System.err.println("根据邮箱查找用户失败: " + e.getMessage());
            return null;
        }
    }

    @Override
    public User findByPhone(String phone) {
        try {
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getPhone, phone);
            return userDao.selectOne(wrapper);
        } catch (Exception e) {
            System.err.println("根据手机号查找用户失败: " + e.getMessage());
            return null;
        }
    }

    @Override
    public List<User> findByEnterpriseId(String enterpriseId) {
        try {
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getEnterpriseId, enterpriseId);
            return userDao.selectList(wrapper);
        } catch (Exception e) {
            System.err.println("根据企业ID查找用户失败: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public List<User> findEnabledUsers() {
        try {
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getStatus, 1);
            return userDao.selectList(wrapper);
        } catch (Exception e) {
            System.err.println("查找启用用户失败: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public List<User> findDisabledUsers() {
        try {
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getStatus, 0);
            return userDao.selectList(wrapper);
        } catch (Exception e) {
            System.err.println("查找禁用用户失败: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public User createUser(User user) {
        return saveUser(user);
    }

    @Override
    public boolean enableUser(Long id) {
        try {
            User user = userDao.selectById(id);
            if (user != null) {
                user.setStatus(1);
                user.setUpdateTime(LocalDateTime.now());
                userDao.updateById(user);
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("启用用户失败: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean disableUser(Long id) {
        try {
            User user = userDao.selectById(id);
            if (user != null) {
                user.setStatus(0);
                user.setUpdateTime(LocalDateTime.now());
                userDao.updateById(user);
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("禁用用户失败: " + e.getMessage());
            return false;
        }
    }

    @Override
    public User validateLogin(String username, String password) {
        try {
            User user = findByUsername(username).orElse(null);
            if (user != null && password.equals(user.getPassword())) {
                return user;
            }
            return null;
        } catch (Exception e) {
            System.err.println("验证登录失败: " + e.getMessage());
            return null;
        }
    }

    @Override
    public boolean isUsernameExists(String username) {
        return existsByUsername(username);
    }

    @Override
    public boolean isEmailExists(String email) {
        try {
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getEmail, email);
            return userDao.selectCount(wrapper) > 0;
        } catch (Exception e) {
            System.err.println("检查邮箱是否存在失败: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean isPhoneExists(String phone) {
        try {
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getPhone, phone);
            return userDao.selectCount(wrapper) > 0;
        } catch (Exception e) {
            System.err.println("检查手机号是否存在失败: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<User> findAdminUsers() {
        return findByUserType("ADMIN");
    }

    @Override
    public List<User> findEnterpriseUsers() {
        return findByUserType("ENTERPRISE");
    }

    @Override
    public List<User> findNormalUsers() {
        return findByUserType("NORMAL");
    }

    @Override
    public List<User> findSystemUsers() {
        return findByUserType("SYSTEM");
    }

    @Override
    public String getAvatarById(Long id) {
        User user = userDao.selectById(id);
        return user != null ? user.getAvatar() : null;
    }

    /**
     * 根据用户ID获取昵称
     * @param id 用户ID
     * @return 用户昵称
     */
    @Override
    public String getNicknameById(Long id) {
        try {
            User user = userDao.selectById(id);
            return user != null ? user.getNickname() : null;
        } catch (Exception e) {
            System.err.println("根据ID获取用户昵称失败: " + e.getMessage());
            return null;
        }
    }
} 