package com.cemenghui.meeting.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cemenghui.meeting.bean.MeetingOperationLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface MeetingOperationLogDao extends BaseMapper<MeetingOperationLog> {
    
    /**
     * 根据用户ID查找操作日志
     */
    @Select("SELECT * FROM meeting_operation_logs WHERE user_id = #{userId} ORDER BY operation_time DESC")
    List<MeetingOperationLog> findByUserId(@Param("userId") Long userId);
    
    /**
     * 根据会议ID查找操作日志
     */
    @Select("SELECT * FROM meeting_operation_logs WHERE meeting_id = #{meetingId} ORDER BY operation_time DESC")
    List<MeetingOperationLog> findByMeetingId(@Param("meetingId") Long meetingId);
    
    /**
     * 根据操作类型查找日志
     */
    @Select("SELECT * FROM meeting_operation_logs WHERE operation_type = #{operationType} ORDER BY operation_time DESC")
    List<MeetingOperationLog> findByOperationType(@Param("operationType") String operationType);
    
    /**
     * 根据时间范围查找日志
     */
    @Select("SELECT * FROM meeting_operation_logs WHERE operation_time BETWEEN #{startTime} AND #{endTime} ORDER BY operation_time DESC")
    List<MeetingOperationLog> findByTimeRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
    
    /**
     * 查找用户的操作日志数量
     */
    @Select("SELECT COUNT(*) FROM meeting_operation_logs WHERE user_id = #{userId}")
    Long countByUserId(@Param("userId") Long userId);
    
    /**
     * 查找会议的操作日志数量
     */
    @Select("SELECT COUNT(*) FROM meeting_operation_logs WHERE meeting_id = #{meetingId}")
    Long countByMeetingId(@Param("meetingId") Long meetingId);
} 