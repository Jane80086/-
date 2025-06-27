package com.cemenghui.course.dao;

import com.cemenghui.course.common.User;
import com.cemenghui.course.common.AdminUser;
import com.cemenghui.course.common.EnterpriseUser;
import com.cemenghui.course.common.NormalUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

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

    // 查找所有管理员用户
    @Query("SELECT u FROM AdminUser u")
    List<AdminUser> findAllAdmins();

    // 查找所有企业用户
    @Query("SELECT u FROM EnterpriseUser u")
    List<EnterpriseUser> findAllEnterprises();

    // 查找所有普通用户
    @Query("SELECT u FROM NormalUser u")
    List<NormalUser> findAllNormals();
} 