package com.cemenghui.meeting.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cemenghui.meeting.bean.MeetingViewLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface MeetingViewLogDao extends BaseMapper<MeetingViewLog> {
    
    /**
     * 根据用户ID查找浏览记录
     */
    @Select("SELECT * FROM meeting_view_logs WHERE user_id = #{userId} ORDER BY view_time DESC")
    List<MeetingViewLog> findByUserId(@Param("userId") Long userId);
    
    /**
     * 根据会议ID查找浏览记录
     */
    @Select("SELECT * FROM meeting_view_logs WHERE meeting_id = #{meetingId} ORDER BY view_time DESC")
    List<MeetingViewLog> findByMeetingId(@Param("meetingId") Long meetingId);
    
    /**
     * 根据时间范围查找浏览记录
     */
    @Select("SELECT * FROM meeting_view_logs WHERE view_time BETWEEN #{startTime} AND #{endTime} ORDER BY view_time DESC")
    List<MeetingViewLog> findByTimeRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
    
    /**
     * 查找会议的浏览次数
     */
    @Select("SELECT COUNT(*) FROM meeting_view_logs WHERE meeting_id = #{meetingId}")
    Long countViewsByMeetingId(@Param("meetingId") Long meetingId);
    
    /**
     * 查找用户的浏览记录数量
     */
    @Select("SELECT COUNT(*) FROM meeting_view_logs WHERE user_id = #{userId}")
    Long countViewsByUserId(@Param("userId") Long userId);
    
    /**
     * 查找热门会议（按浏览次数排序）
     */
    @Select("SELECT meeting_id, meeting_name, COUNT(*) as view_count FROM meeting_view_logs " +
            "GROUP BY meeting_id, meeting_name ORDER BY view_count DESC LIMIT #{limit}")
    List<Object> findPopularMeetings(@Param("limit") Integer limit);
} 