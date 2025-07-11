package com.cemenghui.meeting.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cemenghui.meeting.bean.MeetingParticipant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MeetingParticipantDao extends BaseMapper<MeetingParticipant> {
    
    /**
     * 根据会议ID查找参与者
     */
    @Select("SELECT * FROM meeting_participants WHERE meeting_id = #{meetingId} ORDER BY join_time DESC")
    List<MeetingParticipant> findByMeetingId(@Param("meetingId") Long meetingId);
    
    /**
     * 根据用户ID查找参与的会议
     */
    @Select("SELECT * FROM meeting_participants WHERE user_id = #{userId} ORDER BY join_time DESC")
    List<MeetingParticipant> findByUserId(@Param("userId") Long userId);
    
    /**
     * 查找会议参与者数量
     */
    @Select("SELECT COUNT(*) FROM meeting_participants WHERE meeting_id = #{meetingId} AND status = 1")
    Long countParticipantsByMeetingId(@Param("meetingId") Long meetingId);
    
    /**
     * 查找用户参与的会议数量
     */
    @Select("SELECT COUNT(*) FROM meeting_participants WHERE user_id = #{userId} AND status = 1")
    Long countMeetingsByUserId(@Param("userId") Long userId);
} 