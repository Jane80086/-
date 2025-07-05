package com.cemenghui.news.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cemenghui.news.entity.UserOperationLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface UserOperationLogMapper extends BaseMapper<UserOperationLog> {
    
    /**
     * 根据新闻ID查找操作记录
     */
    @Select("SELECT * FROM user_operation_logs WHERE resource_type = 'NEWS' AND resource_id = #{newsId} ORDER BY operation_time DESC")
    List<UserOperationLog> findByNewsId(@Param("newsId") Long newsId);
    
    /**
     * 根据用户ID查找操作记录
     */
    @Select("SELECT * FROM user_operation_logs WHERE resource_type = 'NEWS' AND user_id = #{userId} ORDER BY operation_time DESC")
    List<UserOperationLog> findByUserId(@Param("userId") Long userId);
    
    /**
     * 根据操作类型查找记录
     */
    @Select("SELECT * FROM user_operation_logs WHERE resource_type = 'NEWS' AND operation_type = #{operationType} ORDER BY operation_time DESC")
    List<UserOperationLog> findByOperationType(@Param("operationType") String operationType);
    
    /**
     * 查找用户的新闻操作记录
     */
    @Select("SELECT * FROM user_operation_logs WHERE resource_type = 'NEWS' AND user_id = #{userId} AND operation_type = #{operationType} ORDER BY operation_time DESC")
    List<UserOperationLog> findByUserAndOperationType(@Param("userId") Long userId, @Param("operationType") String operationType);
}

