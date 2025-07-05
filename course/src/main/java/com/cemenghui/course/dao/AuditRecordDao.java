package com.cemenghui.course.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cemenghui.course.entity.AuditRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

/**
 * 统一审核记录数据访问接口
 * 对应新的audit_records表结构
 */
@Mapper
public interface AuditRecordDao extends BaseMapper<AuditRecord> {
    
    /**
     * 根据资源类型和资源ID查找审核记录
     */
    @Select("SELECT * FROM audit_records WHERE resource_type = #{resourceType} AND resource_id = #{resourceId} ORDER BY audit_time DESC")
    List<AuditRecord> findByResource(@Param("resourceType") String resourceType, @Param("resourceId") Long resourceId);
    
    /**
     * 根据审核人ID查找审核记录
     */
    @Select("SELECT * FROM audit_records WHERE reviewer_id = #{reviewerId} ORDER BY audit_time DESC")
    List<AuditRecord> findByReviewerId(@Param("reviewerId") Long reviewerId);
    
    /**
     * 根据审核状态查找审核记录
     */
    @Select("SELECT * FROM audit_records WHERE status = #{status} ORDER BY audit_time DESC")
    List<AuditRecord> findByStatus(@Param("status") String status);
    
    /**
     * 根据资源类型查找审核记录
     */
    @Select("SELECT * FROM audit_records WHERE resource_type = #{resourceType} ORDER BY audit_time DESC")
    List<AuditRecord> findByResourceType(@Param("resourceType") String resourceType);
    
    /**
     * 查找课程相关的审核记录
     */
    @Select("SELECT * FROM audit_records WHERE resource_type = 'COURSE' ORDER BY audit_time DESC")
    List<AuditRecord> findCourseAuditRecords();
    
    /**
     * 查找待审核的记录
     */
    @Select("SELECT * FROM audit_records WHERE status = 'PENDING' ORDER BY create_time ASC")
    List<AuditRecord> findPendingRecords();
    
    /**
     * 查找已通过的记录
     */
    @Select("SELECT * FROM audit_records WHERE status = 'APPROVED' ORDER BY audit_time DESC")
    List<AuditRecord> findApprovedRecords();
    
    /**
     * 查找已拒绝的记录
     */
    @Select("SELECT * FROM audit_records WHERE status = 'REJECTED' ORDER BY audit_time DESC")
    List<AuditRecord> findRejectedRecords();
} 