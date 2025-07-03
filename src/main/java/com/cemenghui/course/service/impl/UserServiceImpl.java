package com.cemenghui.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.cemenghui.course.dao.UserDao;
import com.cemenghui.course.common.User;
import com.cemenghui.course.entity.UserType;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import com.cemenghui.course.common.AdminUser;
import com.cemenghui.course.common.EnterpriseUser;
import com.cemenghui.course.common.NormalUser;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cemenghui.course.service.UserService;

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
    public Optional<User> findByUsername(String username) {
        try {
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getUsername, username);
            User user = userDao.selectOne(wrapper);
            return Optional.ofNullable(user);
        } catch (Exception e) {
            System.err.println("根据用户名查找用户失败: " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * 根据用户类型查找用户列表
     * @param userType 用户类型
     * @return 用户列表
     */
    public List<User> findByUserType(UserType userType) {
        try {
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            switch (userType) {
                case ADMIN:
                    wrapper.eq(User::getUserType, "ADMIN");
                    break;
                case ENTERPRISE:
                    wrapper.eq(User::getUserType, "ENTERPRISE");
                    break;
                case NORMAL:
                    wrapper.eq(User::getUserType, "NORMAL");
                    break;
                default:
                    return getAllUsers();
            }
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
    public User getById(Long id) {
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
        AdminUser admin1 = new AdminUser();
        admin1.setId(1L);
        admin1.setUsername("admin");
        admin1.setEmail("admin@example.com");
        mockUsers.add(admin1);
        
        // 添加企业用户
        EnterpriseUser enterprise1 = new EnterpriseUser();
        enterprise1.setId(2L);
        enterprise1.setUsername("enterprise1");
        enterprise1.setEmail("enterprise1@example.com");
        enterprise1.setCompanyName("示例企业");
        mockUsers.add(enterprise1);
        
        // 添加普通用户
        NormalUser normal1 = new NormalUser();
        normal1.setId(3L);
        normal1.setUsername("user1");
        normal1.setEmail("user1@example.com");
        mockUsers.add(normal1);
        
        NormalUser normal2 = new NormalUser();
        normal2.setId(4L);
        normal2.setUsername("user2");
        normal2.setEmail("user2@example.com");
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
} 