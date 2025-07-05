package com.cemenghui.news.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cemenghui.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户数据访问接口
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return 用户对象
     */
    @Select("SELECT * FROM users WHERE username = #{username}")
    User findByUsername(@Param("username") String username);

    /**
     * 根据用户名查找用户（兼容MyBatis Plus）
     * @param username 用户名
     * @return 用户对象
     */
    @Select("SELECT * FROM users WHERE username = #{username}")
    User selectByUsername(@Param("username") String username);

    /**
     * 查找所有管理员用户
     * @return 管理员用户列表
     */
    @Select("SELECT * FROM users WHERE user_type = 'ADMIN'")
    List<User> findAllAdmins();

    /**
     * 查找所有企业用户
     * @return 企业用户列表
     */
    @Select("SELECT * FROM users WHERE user_type = 'ENTERPRISE'")
    List<User> findAllEnterprises();

    /**
     * 查找所有普通用户
     * @return 普通用户列表
     */
    @Select("SELECT * FROM users WHERE user_type = 'NORMAL'")
    List<User> findAllNormals();

    /**
     * 获取用户类型
     * @param userId 用户ID
     * @return 用户类型
     */
    @Select("SELECT user_type FROM users WHERE id = #{userId}")
    String getUserType(@Param("userId") Long userId);

    /**
     * 分页查询用户（需要在XML中实现复杂查询）
     * @param page 分页参数
     * @param username 用户名（可选）
     * @param phone 手机号（可选）
     * @param status 状态（可选）
     * @return 分页结果
     */
    Page<User> findUsersByCondition(Page<User> page,
                                    @Param("username") String username,
                                    @Param("phone") String phone,
                                    @Param("status") Integer status);
}