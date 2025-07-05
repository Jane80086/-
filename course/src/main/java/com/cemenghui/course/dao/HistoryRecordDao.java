package com.cemenghui.course.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cemenghui.course.entity.HistoryRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

/**
 * 统一历史记录数据访问接口
 * 对应新的history_records表结构
 */
@Mapper
public interface HistoryRecordDao extends BaseMapper<HistoryRecord> {
    
    /**
     * 根据资源类型和资源ID查找历史记录
     */
    @Select("SELECT * FROM history_records WHERE resource_type = #{resourceType} AND resource_id = #{resourceId} ORDER BY record_time DESC")
    List<HistoryRecord> findByResource(@Param("resourceType") String resourceType, @Param("resourceId") Long resourceId);
    
    /**
     * 根据用户ID查找历史记录
     */
    @Select("SELECT * FROM history_records WHERE user_id = #{userId} ORDER BY record_time DESC")
    List<HistoryRecord> findByUserId(@Param("userId") Long userId);
    
    /**
     * 根据操作类型查找历史记录
     */
    @Select("SELECT * FROM history_records WHERE action = #{action} ORDER BY record_time DESC")
    List<HistoryRecord> findByAction(@Param("action") String action);
    
    /**
     * 根据资源类型查找历史记录
     */
    @Select("SELECT * FROM history_records WHERE resource_type = #{resourceType} ORDER BY record_time DESC")
    List<HistoryRecord> findByResourceType(@Param("resourceType") String resourceType);
    
    /**
     * 查找课程相关的历史记录
     */
    @Select("SELECT * FROM history_records WHERE resource_type = 'COURSE' ORDER BY record_time DESC")
    List<HistoryRecord> findCourseHistoryRecords();
    
    /**
     * 查找用户相关的历史记录
     */
    @Select("SELECT * FROM history_records WHERE resource_type = 'USER' ORDER BY record_time DESC")
    List<HistoryRecord> findUserHistoryRecords();
    
    /**
     * 查找企业相关的历史记录
     */
    @Select("SELECT * FROM history_records WHERE resource_type = 'ENTERPRISE' ORDER BY record_time DESC")
    List<HistoryRecord> findEnterpriseHistoryRecords();
    
    /**
     * 查找查看操作的历史记录
     */
    @Select("SELECT * FROM history_records WHERE action = 'VIEW' ORDER BY record_time DESC")
    List<HistoryRecord> findViewHistoryRecords();
    
    /**
     * 查找修改操作的历史记录
     */
    @Select("SELECT * FROM history_records WHERE action = 'MODIFY' ORDER BY record_time DESC")
    List<HistoryRecord> findModifyHistoryRecords();
    
    /**
     * 查找删除操作的历史记录
     */
    @Select("SELECT * FROM history_records WHERE action = 'DELETE' ORDER BY record_time DESC")
    List<HistoryRecord> findDeleteHistoryRecords();
} 