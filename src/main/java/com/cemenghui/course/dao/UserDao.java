package com.cemenghui.course.dao;

import com.cemenghui.course.entity.User;
import com.cemenghui.course.entity.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * 用户数据访问接口
 */
@Repository
public interface UserDao extends JpaRepository<User, Long> {
    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return 用户对象（可选）
     */
    Optional<User> findByUsername(String username);

    /**
     * 根据用户类型查找用户列表
     * @param usertype 用户类型
     * @return 用户列表
     */
    List<User> findByUserType(UserType usertype);
} 