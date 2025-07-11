package com.cemenghui.meeting.util;

import com.cemenghui.meeting.bean.Meeting;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.regex.Pattern;
import java.time.format.DateTimeFormatter;

@Component
public class ValidationUtil {
    private static final Logger logger = LoggerFactory.getLogger(ValidationUtil.class);
    
    // 正则表达式模式
    private static final Pattern MEETING_NAME_PATTERN = Pattern.compile("^[\\u4e00-\\u9fa5a-zA-Z0-9\\s\\-_()（）]{2,100}$");
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,20}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^.{6,20}$");
    
    // 常量
    private static final int MAX_MEETING_CONTENT_LENGTH = 2000;
    private static final int MAX_REVIEW_COMMENT_LENGTH = 500;
    private static final int MIN_MEETING_DURATION_MINUTES = 15;
    private static final int MAX_MEETING_DURATION_HOURS = 24;

    /**
     * 验证会议创建数据
     */
    public String validateMeetingCreate(Meeting meeting) {
        try {
            if (meeting == null) {
                return "会议数据不能为空";
            }
            
            // 验证会议名称
            String nameError = validateMeetingName(meeting.getMeetingName());
            if (nameError != null) {
                return nameError;
            }
            
            // 验证时间
            String timeError = validateMeetingTime(meeting.getStartTime(), meeting.getEndTime());
            if (timeError != null) {
                return timeError;
            }
            
            // 验证会议内容
            String contentError = validateMeetingContent(meeting.getMeetingContent());
            if (contentError != null) {
                return contentError;
            }
            
            // 验证会议时长
            String durationError = validateMeetingDuration(meeting.getStartTime(), meeting.getEndTime());
            if (durationError != null) {
                return durationError;
            }
            
            return null; // 验证通过
        } catch (Exception e) {
            logger.error("会议创建验证异常", e);
            return "数据验证失败，请检查输入格式";
        }
    }
    
    /**
     * 验证会议更新数据
     */
    public String validateMeetingUpdate(Meeting meeting) {
        try {
            if (meeting == null) {
                return "会议数据不能为空";
            }
            
            if (meeting.getId() == null) {
                return "会议ID不能为空";
            }
            
            // 验证会议名称（如果提供）
            if (meeting.getMeetingName() != null) {
                String nameError = validateMeetingName(meeting.getMeetingName());
                if (nameError != null) {
                    return nameError;
                }
            }
            
            // 验证时间（如果提供）
            if (meeting.getStartTime() != null && meeting.getEndTime() != null) {
                String timeError = validateMeetingTime(meeting.getStartTime(), meeting.getEndTime());
                if (timeError != null) {
                    return timeError;
                }
                
                String durationError = validateMeetingDuration(meeting.getStartTime(), meeting.getEndTime());
                if (durationError != null) {
                    return durationError;
                }
            }
            
            return null; // 验证通过
        } catch (Exception e) {
            logger.error("会议更新验证异常", e);
            return "数据验证失败，请检查输入格式";
        }
    }
    
    /**
     * 验证用户名
     */
    public String validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            logger.warn("validateUsername: 用户名为空");
            return "用户名不能为空";
        }
        if (!USERNAME_PATTERN.matcher(username.trim()).matches()) {
            logger.warn("validateUsername: 用户名格式不正确: {}", username);
            return "用户名格式不正确，仅允许字母、数字、下划线，长度3-20";
        }
        return null;
    }
    
    /**
     * 验证密码
     */
    public String validatePassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            return "密码不能为空";
        }
        
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            return "密码长度必须在6-20位之间";
        }
        
        return null;
    }
    
    /**
     * 验证审核意见
     */
    public String validateReviewComment(String comment) {
        if (comment != null && comment.length() > MAX_REVIEW_COMMENT_LENGTH) {
            return "审核意见不能超过" + MAX_REVIEW_COMMENT_LENGTH + "个字符";
        }
        
        return null;
    }
    
    /**
     * 验证分页参数
     */
    public String validatePagination(Integer page, Integer size) {
        if (page != null && (page < 1 || page > 1000)) {
            return "页码必须在1-1000之间";
        }
        
        if (size != null && (size < 1 || size > 100)) {
            return "页面大小必须在1-100之间";
        }
        
        return null;
    }
    
    /**
     * 验证用户注册数据
     */
    public String validateUserRegistration(String username, String password, String realName, String email, String phone, String userType) {
        // 验证用户名
        String usernameError = validateUsername(username);
        if (usernameError != null) {
            return usernameError;
        }
        
        // 验证密码
        String passwordError = validatePassword(password);
        if (passwordError != null) {
            return passwordError;
        }
        
        // 验证真实姓名
        if (realName == null || realName.trim().isEmpty()) {
            return "真实姓名不能为空";
        }
        if (realName.trim().length() > 50) {
            return "真实姓名不能超过50个字符";
        }
        
        // 验证邮箱（如果提供）
        if (email != null && !email.trim().isEmpty()) {
            if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
                return "邮箱格式不正确";
            }
            if (email.length() > 200) {
                return "邮箱长度不能超过200个字符";
            }
        }
        
        // 验证手机号（如果提供）
        if (phone != null && !phone.trim().isEmpty()) {
            if (!phone.matches("^1[3-9]\\d{9}$")) {
                return "手机号格式不正确";
            }
        }
        
        // 验证用户类型
        if (userType == null || userType.trim().isEmpty()) {
            return "用户类型不能为空";
        }
        String type = userType.trim().toUpperCase();
        if (!"ADMIN".equals(type) && !"ENTERPRISE".equals(type) && !"NORMAL".equals(type)) {
            return "用户类型只能是ADMIN、ENTERPRISE或NORMAL";
        }
        
        return null;
    }
    
    /**
     * 验证会议查询参数
     */
    public String validateMeetingQuery(Integer page, Integer size, String meetingName, String creator, String startDate, String endDate) {
        // 验证分页参数
        String paginationError = validatePagination(page, size);
        if (paginationError != null) {
            return paginationError;
        }
        
        // 验证会议名称（如果提供）
        if (meetingName != null && !meetingName.trim().isEmpty()) {
            if (meetingName.trim().length() > 100) {
                return "会议名称不能超过100个字符";
            }
        }
        
        // 验证创建人（如果提供）
        if (creator != null && !creator.trim().isEmpty()) {
            if (creator.trim().length() > 100) {
                return "创建人名称不能超过100个字符";
            }
        }
        
        // 验证日期范围（如果提供）
        if (startDate != null && endDate != null && !startDate.trim().isEmpty() && !endDate.trim().isEmpty()) {
            try {
                LocalDateTime start = LocalDateTime.parse(startDate.trim());
                LocalDateTime end = LocalDateTime.parse(endDate.trim());
                if (start.isAfter(end)) {
                    return "开始日期不能晚于结束日期";
                }
            } catch (Exception e) {
                return "日期格式不正确，请使用ISO格式(YYYY-MM-DDTHH:mm:ss)";
            }
        }
        
        return null;
    }
    
    /**
     * 验证审核请求
     */
    public String validateReviewRequest(Long meetingId, Integer status, String reviewComment) {
        if (meetingId == null || meetingId <= 0) {
            return "会议ID必须大于0";
        }
        
        if (status == null) {
            return "审核状态不能为空";
        }
        
        if (status != 1 && status != 2) {
            return "审核状态只能是1(通过)或2(拒绝)";
        }
        
        // 验证审核意见（如果提供）
        if (reviewComment != null) {
            String commentError = validateReviewComment(reviewComment);
            if (commentError != null) {
                return commentError;
            }
        }
        
        return null;
    }
    
    /**
     * 验证URL格式
     */
    public String validateUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return null; // URL可以为空
        }
        
        if (url.trim().length() > 500) {
            return "URL长度不能超过500个字符";
        }
        
        // 简单的URL格式验证
        if (!url.trim().startsWith("http://") && !url.trim().startsWith("https://")) {
            return "URL必须以http://或https://开头";
        }
        
        return null;
    }
    
    /**
     * 验证日期时间字符串格式
     */
    public LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.trim().isEmpty()) {
            return null;
        }
        
        try {
            return LocalDateTime.parse(dateTimeStr.trim(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (Exception e) {
            logger.warn("日期时间格式解析失败: {}", dateTimeStr);
            throw new IllegalArgumentException("日期时间格式不正确，请使用ISO格式(YYYY-MM-DDTHH:mm:ss)");
        }
    }
    
    /**
     * 验证会议内容长度
     */
    public String validateMeetingContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            return "会议内容不能为空";
        }
        
        if (content.trim().length() < 10) {
            return "会议内容至少需要10个字符";
        }
        
        if (content.length() > MAX_MEETING_CONTENT_LENGTH) {
            return "会议内容不能超过" + MAX_MEETING_CONTENT_LENGTH + "个字符";
        }
        
        return null;
    }
    
    // 私有辅助方法
    
    private String validateMeetingName(String meetingName) {
        if (meetingName == null || meetingName.trim().isEmpty()) {
            return "会议名称不能为空";
        }
        
        if (!MEETING_NAME_PATTERN.matcher(meetingName.trim()).matches()) {
            return "会议名称只能包含中文、英文、数字、空格、连字符、下划线和括号，长度2-100位";
        }
        
        return null;
    }
    
    private String validateMeetingTime(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime == null) {
            return "开始时间不能为空";
        }
        
        if (endTime == null) {
            return "结束时间不能为空";
        }
        
        LocalDateTime now = LocalDateTime.now();
        if (startTime.isBefore(now)) {
            return "开始时间不能早于当前时间";
        }
        
        if (startTime.isAfter(endTime)) {
            return "开始时间不能晚于结束时间";
        }
        
        return null;
    }
    
    private String validateMeetingDuration(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime == null || endTime == null) {
            return null; // 时间验证已在其他地方处理
        }
        
        long durationMinutes = java.time.Duration.between(startTime, endTime).toMinutes();
        
        if (durationMinutes < MIN_MEETING_DURATION_MINUTES) {
            return "会议时长不能少于" + MIN_MEETING_DURATION_MINUTES + "分钟";
        }
        
        if (durationMinutes > MAX_MEETING_DURATION_HOURS * 60) {
            return "会议时长不能超过" + MAX_MEETING_DURATION_HOURS + "小时";
        }
        
        return null;
    }
} 