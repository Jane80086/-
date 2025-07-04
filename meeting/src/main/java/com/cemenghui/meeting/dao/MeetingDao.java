package com.cemenghui.meeting.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cemenghui.meeting.bean.Meeting;
import com.cemenghui.meeting.bean.MeetingReviewRecord;

import org.apache.ibatis.annotations.*;

import java.util.List;

public interface MeetingDao extends BaseMapper<Meeting> {
    
    @Insert("INSERT INTO meeting_info (meeting_name, start_time, end_time, creator, meeting_content, image_url, status, create_time, update_time) " +
            "VALUES (#{meetingName}, #{startTime}, #{endTime}, #{creator}, #{meetingContent}, #{imageUrl}, #{status}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Meeting meeting);
    
    @Select("SELECT * FROM meeting_info WHERE id = #{id} AND status != 3")
    Meeting findById(Long id);
    
    @Update("UPDATE meeting_info SET meeting_name = #{meetingName}, start_time = #{startTime}, end_time = #{endTime}, " +
            "meeting_content = #{meetingContent}, image_url = #{imageUrl}, update_time = NOW() " +
            "WHERE id = #{id} AND status != 3")
    int update(Meeting meeting);
    
    @Update("UPDATE meeting_info SET status = 3, update_time = NOW() WHERE id = #{id}")
    int deleteById(Long id);
    
    @Update("UPDATE meeting_info SET status = #{status}, reviewer = #{reviewer}, review_time = NOW(), " +
            "review_comment = #{reviewComment}, update_time = NOW() WHERE id = #{id}")
    int review(@Param("id") Long id, @Param("status") Integer status, 
               @Param("reviewer") String reviewer, @Param("reviewComment") String reviewComment);
    
    // 基础查询 - 所有非删除状态的会议
    @Select("SELECT * FROM meeting_info WHERE status != 3 ORDER BY create_time DESC LIMIT #{size} OFFSET #{offset}")
    List<Meeting> findAll(@Param("size") Integer size, @Param("offset") Integer offset);
    
    /**
     * 根据会议名称模糊查询
     */
    @Select("SELECT * FROM meeting_info WHERE status != 3 AND meeting_name LIKE CONCAT('%', #{meetingName}, '%') ORDER BY create_time DESC LIMIT #{size} OFFSET #{offset}")
    List<Meeting> findByMeetingName(@Param("meetingName") String meetingName, @Param("size") Integer size, @Param("offset") Integer offset);
    
    /**
     * 根据创建人模糊查询
     */
    @Select("SELECT * FROM meeting_info WHERE status != 3 AND creator LIKE CONCAT('%', #{creator}, '%') ORDER BY create_time DESC LIMIT #{size} OFFSET #{offset}")
    List<Meeting> findByCreator(@Param("creator") String creator, @Param("size") Integer size, @Param("offset") Integer offset);
    
    /**
     * 根据开始日期查询
     */
    @Select("SELECT * FROM meeting_info WHERE status != 3 AND DATE(start_time) >= #{startDate} ORDER BY create_time DESC LIMIT #{size} OFFSET #{offset}")
    List<Meeting> findByStartDate(@Param("startDate") String startDate, @Param("size") Integer size, @Param("offset") Integer offset);
    
    /**
     * 根据结束日期查询
     */
    @Select("SELECT * FROM meeting_info WHERE status != 3 AND DATE(end_time) <= #{endDate} ORDER BY create_time DESC LIMIT #{size} OFFSET #{offset}")
    List<Meeting> findByEndDate(@Param("endDate") String endDate, @Param("size") Integer size, @Param("offset") Integer offset);
    
    /**
     * 根据状态查询
     */
    @Select("SELECT * FROM meeting_info WHERE status = #{status} ORDER BY create_time DESC LIMIT #{size} OFFSET #{offset}")
    List<Meeting> findByStatus(@Param("status") Integer status, @Param("size") Integer size, @Param("offset") Integer offset);
    
    /**
     * 根据会议名称和创建人查询
     */
    @Select("SELECT * FROM meeting_info WHERE status != 3 AND meeting_name LIKE CONCAT('%', #{meetingName}, '%') AND creator LIKE CONCAT('%', #{creator}, '%') ORDER BY create_time DESC LIMIT #{size} OFFSET #{offset}")
    List<Meeting> findByMeetingNameAndCreator(@Param("meetingName") String meetingName, @Param("creator") String creator, @Param("size") Integer size, @Param("offset") Integer offset);
    
    /**
     * 根据日期范围查询
     */
    @Select("SELECT * FROM meeting_info WHERE status != 3 AND DATE(start_time) >= #{startDate} AND DATE(end_time) <= #{endDate} ORDER BY create_time DESC LIMIT #{size} OFFSET #{offset}")
    List<Meeting> findByDateRange(@Param("startDate") String startDate, @Param("endDate") String endDate, @Param("size") Integer size, @Param("offset") Integer offset);
    
    /**
     * 查询待审核会议
     */
    @Select("SELECT * FROM meeting_info WHERE status = 0 ORDER BY create_time DESC")
    List<Meeting> findPendingMeetings();
    
    // 审核记录相关
    @Insert("INSERT INTO meeting_review_record (meeting_id, meeting_name, creator, reviewer, status, review_comment, review_time) VALUES (#{meetingId}, #{meetingName}, #{creator}, #{reviewer}, #{status}, #{reviewComment}, #{reviewTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertReviewRecord(MeetingReviewRecord record);

    @Select("SELECT * FROM meeting_review_record WHERE reviewer = #{reviewer} ORDER BY review_time DESC")
    List<MeetingReviewRecord> findReviewRecordsByReviewer(String reviewer);

    @Select("SELECT * FROM meeting_review_record WHERE creator = #{creator} ORDER BY review_time DESC")
    List<MeetingReviewRecord> findReviewRecordsByCreator(String creator);

    @Select("SELECT * FROM meeting_review_record WHERE meeting_id = #{meetingId} ORDER BY review_time DESC")
    List<MeetingReviewRecord> findReviewRecordsByMeetingId(Long meetingId);
    
    /**
     * 查询自己的会议和已通过的会议
     */
    @Select("SELECT * FROM meeting_info WHERE status != 3 AND (creator = #{creator} OR status = 1) ORDER BY create_time DESC LIMIT #{size} OFFSET #{offset}")
    List<Meeting> findByCreatorOrApproved(@Param("creator") String creator, @Param("size") Integer size, @Param("offset") Integer offset);
    
    /**
     * 统计总数 - 所有非删除状态的会议
     */
    @Select("SELECT COUNT(*) FROM meeting_info WHERE status != 3")
    int countAll();
    
    /**
     * 统计按会议名称
     */
    @Select("SELECT COUNT(*) FROM meeting_info WHERE status != 3 AND meeting_name LIKE CONCAT('%', #{meetingName}, '%')")
    int countByMeetingName(@Param("meetingName") String meetingName);
    
    /**
     * 统计按创建人
     */
    @Select("SELECT COUNT(*) FROM meeting_info WHERE status != 3 AND creator LIKE CONCAT('%', #{creator}, '%')")
    int countByCreator(@Param("creator") String creator);
    
    /**
     * 统计按状态
     */
    @Select("SELECT COUNT(*) FROM meeting_info WHERE status = #{status}")
    int countByStatus(@Param("status") Integer status);
    
    /**
     * 统计自己的会议和已通过的会议
     */
    @Select("SELECT COUNT(*) FROM meeting_info WHERE status != 3 AND (creator = #{creator} OR status = 1)")
    int countByCreatorOrApproved(@Param("creator") String creator);
    
    /**
     * 查询管理员相关的会议（自己创建的 + 自己审核的 + 未审核的）
     */
    @Select("SELECT * FROM meeting_info WHERE status != 3 AND (creator = #{adminUsername} OR reviewer = #{adminUsername} OR status = 0) ORDER BY create_time DESC LIMIT #{size} OFFSET #{offset}")
    List<Meeting> findByAdminRelated(@Param("adminUsername") String adminUsername, @Param("size") Integer size, @Param("offset") Integer offset);
    
    /**
     * 统计管理员相关的会议总数
     */
    @Select("SELECT COUNT(*) FROM meeting_info WHERE status != 3 AND (creator = #{adminUsername} OR reviewer = #{adminUsername} OR status = 0)")
    int countByAdminRelated(@Param("adminUsername") String adminUsername);

    // 查询指定审核人审核的已拒绝会议
    @Select("SELECT * FROM meeting_info WHERE status = 2 AND reviewer = #{reviewer} ORDER BY create_time DESC")
    List<Meeting> findRejectedMeetingsByReviewer(String reviewer);
    
    // 查询指定创建人创建的已删除会议
    @Select("SELECT * FROM meeting_info WHERE status = 3 AND creator = #{creator} ORDER BY create_time DESC")
    List<Meeting> findDeletedMeetingsByCreator(String creator);
} 