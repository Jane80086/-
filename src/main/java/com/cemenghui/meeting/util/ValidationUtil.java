package com.cemenghui.meeting.util;

import com.cemenghui.meeting.entity.Meeting;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Component
public class ValidationUtil {
    
    /**
     * 验证会议创建数据
     */
    public String validateMeetingCreate(Meeting meeting) {
        if (meeting == null) {
            return "会议数据不能为空";
        }
        
        if (meeting.getMeetingName() == null || meeting.getMeetingName().trim().isEmpty()) {
            return "会议名称不能为空";
        }
        
        if (meeting.getMeetingName().length() > 100) {
            return "会议名称长度不能超过100个字符";
        }
        
        if (meeting.getStartTime() == null) {
            return "会议开始时间不能为空";
        }
        
        if (meeting.getEndTime() == null) {
            return "会议结束时间不能为空";
        }
        
        if (meeting.getStartTime().isAfter(meeting.getEndTime())) {
            return "会议开始时间不能晚于结束时间";
        }
        
        if (meeting.getStartTime().isBefore(LocalDateTime.now())) {
            return "会议开始时间不能早于当前时间";
        }
        
        if (meeting.getMeetingContent() != null && meeting.getMeetingContent().length() > 1000) {
            return "会议内容长度不能超过1000个字符";
        }
        
        return null; // 验证通过
    }
    
    /**
     * 验证会议更新数据
     */
    public String validateMeetingUpdate(Meeting meeting) {
        if (meeting == null) {
            return "会议数据不能为空";
        }
        
        if (meeting.getId() == null || meeting.getId() <= 0) {
            return "会议ID无效";
        }
        
        if (meeting.getMeetingName() == null || meeting.getMeetingName().trim().isEmpty()) {
            return "会议名称不能为空";
        }
        
        if (meeting.getMeetingName().length() > 100) {
            return "会议名称长度不能超过100个字符";
        }
        
        if (meeting.getStartTime() == null) {
            return "会议开始时间不能为空";
        }
        
        if (meeting.getEndTime() == null) {
            return "会议结束时间不能为空";
        }
        
        if (meeting.getStartTime().isAfter(meeting.getEndTime())) {
            return "会议开始时间不能晚于结束时间";
        }
        
        if (meeting.getMeetingContent() != null && meeting.getMeetingContent().length() > 1000) {
            return "会议内容长度不能超过1000个字符";
        }
        
        return null; // 验证通过
    }
    
    /**
     * 验证审核请求
     */
    public String validateReviewRequest(Long meetingId, Integer status, String reviewComment) {
        if (meetingId == null || meetingId <= 0) {
            return "会议ID无效";
        }
        
        if (status == null || (status != 1 && status != 2)) {
            return "审核状态无效，只能是1(通过)或2(拒绝)";
        }
        
        if (reviewComment != null && reviewComment.length() > 500) {
            return "审核意见长度不能超过500个字符";
        }
        
        return null; // 验证通过
    }
    
    /**
     * 验证会议查询参数
     */
    public String validateMeetingQuery(Integer page, Integer size, String meetingName, 
                                     String creator, String startDate, String endDate) {
        if (page != null && page < 1) {
            return "页码必须大于0";
        }
        
        if (size != null && (size < 1 || size > 100)) {
            return "每页大小必须在1-100之间";
        }
        
        if (meetingName != null && meetingName.length() > 50) {
            return "会议名称查询条件长度不能超过50个字符";
        }
        
        if (creator != null && creator.length() > 50) {
            return "创建人查询条件长度不能超过50个字符";
        }
        
        if (startDate != null && !isValidDate(startDate)) {
            return "开始日期格式无效，应为yyyy-MM-dd格式";
        }
        
        if (endDate != null && !isValidDate(endDate)) {
            return "结束日期格式无效，应为yyyy-MM-dd格式";
        }
        
        if (startDate != null && endDate != null) {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            if (start.isAfter(end)) {
                return "开始日期不能晚于结束日期";
            }
        }
        
        return null; // 验证通过
    }
    
    /**
     * 验证日期格式
     */
    private boolean isValidDate(String dateStr) {
        try {
            LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
    
    /**
     * 验证用户名
     */
    public String validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return "用户名不能为空";
        }
        
        if (username.length() < 3 || username.length() > 20) {
            return "用户名长度必须在3-20个字符之间";
        }
        
        if (!username.matches("^[a-zA-Z0-9_]+$")) {
            return "用户名只能包含字母、数字和下划线";
        }
        
        return null; // 验证通过
    }
    
    /**
     * 验证密码
     */
    public String validatePassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            return "密码不能为空";
        }
        
        if (password.length() < 6 || password.length() > 20) {
            return "密码长度必须在6-20个字符之间";
        }
        
        return null; // 验证通过
    }
    
    /**
     * 验证邮箱
     */
    public String validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return "邮箱不能为空";
        }
        
        if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
            return "邮箱格式无效";
        }
        
        return null; // 验证通过
    }
    
    /**
     * 验证手机号
     */
    public String validatePhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return "手机号不能为空";
        }
        
        if (!phone.matches("^1[3-9]\\d{9}$")) {
            return "手机号格式无效";
        }
        
        return null; // 验证通过
    }
}    