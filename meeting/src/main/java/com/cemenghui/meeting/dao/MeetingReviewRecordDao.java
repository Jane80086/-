package com.cemenghui.meeting.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cemenghui.meeting.bean.MeetingReviewRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface MeetingReviewRecordDao extends BaseMapper<MeetingReviewRecord> {
    
    /**
     * 根据会议ID查找审核记录
     */
    @Select("SELECT * FROM meeting_review_records WHERE meeting_id = #{meetingId} ORDER BY review_time DESC")
    List<MeetingReviewRecord> findByMeetingId(@Param("meetingId") Long meetingId);
    
    /**
     * 根据创建者ID查找审核记录
     */
    @Select("SELECT * FROM meeting_review_records WHERE creator_id = #{creatorId} ORDER BY review_time DESC")
    List<MeetingReviewRecord> findByCreatorId(@Param("creatorId") Long creatorId);
    
    /**
     * 根据审核者ID查找审核记录
     */
    @Select("SELECT * FROM meeting_review_records WHERE reviewer_id = #{reviewerId} ORDER BY review_time DESC")
    List<MeetingReviewRecord> findByReviewerId(@Param("reviewerId") Long reviewerId);
    
    /**
     * 根据审核状态查找记录
     */
    @Select("SELECT * FROM meeting_review_records WHERE status = #{status} ORDER BY review_time DESC")
    List<MeetingReviewRecord> findByStatus(@Param("status") Integer status);
    
    /**
     * 根据时间范围查找审核记录
     */
    @Select("SELECT * FROM meeting_review_records WHERE review_time BETWEEN #{startTime} AND #{endTime} ORDER BY review_time DESC")
    List<MeetingReviewRecord> findByTimeRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
    
    /**
     * 统计审核者的审核记录数量
     */
    @Select("SELECT COUNT(*) FROM meeting_review_records WHERE reviewer_id = #{reviewerId}")
    Long countByReviewerId(@Param("reviewerId") Long reviewerId);
    
    /**
     * 统计创建者的审核记录数量
     */
    @Select("SELECT COUNT(*) FROM meeting_review_records WHERE creator_id = #{creatorId}")
    Long countByCreatorId(@Param("creatorId") Long creatorId);
} 