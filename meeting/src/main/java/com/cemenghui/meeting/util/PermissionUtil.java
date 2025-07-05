package com.cemenghui.meeting.util;

import com.cemenghui.meeting.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PermissionUtil {
    
    @Autowired
    private UserDao userDao;
    
    /**
     * 检查用户是否为管理员
     */
    public boolean isAdmin(String username) {
        if (username == null) {
            log.warn("isAdmin: 用户名为空");
            return false;
        }
        var user = userDao.findByUsername(username);
        boolean result = user != null && "ADMIN".equals(user.getUserType());
        if (!result) {
            log.info("isAdmin: 用户 {} 不是管理员", username);
        }
        return result;
    }
    
    /**
     * 检查用户是否为企业用户
     */
    public boolean isEnterpriseUser(String username) {
        if (username == null) {
            log.warn("isEnterpriseUser: 用户名为空");
            return false;
        }
        var user = userDao.findByUsername(username);
        boolean result = user != null && "ENTERPRISE".equals(user.getUserType());
        if (!result) {
            log.info("isEnterpriseUser: 用户 {} 不是企业用户", username);
        }
        return result;
    }
    
    /**
     * 检查用户是否为普通用户
     */
    public boolean isNormalUser(String username) {
        return !isAdmin(username) && !isEnterpriseUser(username);
    }
    
    /**
     * 检查用户是否有权限编辑会议
     * 只有创建者或管理员可以编辑
     */
    public boolean canEditMeeting(String username, String creator) {
        boolean result = username != null && (username.equals(creator) || isAdmin(username));
        if (!result) {
            log.info("canEditMeeting: 用户 {} 无权编辑会议，创建者为 {}", username, creator);
        }
        return result;
    }
    
    /**
     * 检查用户是否有权限删除会议
     * 只有创建者或管理员可以删除
     */
    public boolean canDeleteMeeting(String username, String creator) {
        boolean result = username != null && (username.equals(creator) || isAdmin(username));
        if (!result) {
            log.info("canDeleteMeeting: 用户 {} 无权删除会议，创建者为 {}", username, creator);
        }
        return result;
    }
    
    /**
     * 检查用户是否有权限审核会议
     * 只有管理员可以审核
     */
    public boolean canReviewMeeting(String username) {
        boolean result = isAdmin(username);
        if (!result) {
            log.info("canReviewMeeting: 用户 {} 无权审核会议", username);
        }
        return result;
    }
    
    /**
     * 检查用户是否有权限创建会议
     * 管理员和企业用户可以创建会议
     */
    public boolean canCreateMeeting(String username) {
        boolean result = isAdmin(username) || isEnterpriseUser(username);
        if (!result) {
            log.info("canCreateMeeting: 用户 {} 无权创建会议", username);
        }
        return result;
    }
    
    /**
     * 检查用户是否有权限查看所有会议
     * 所有用户都可以查看已通过的会议
     */
    public boolean canViewMeetings(String username) {
        boolean result = username != null;
        if (!result) {
            log.info("canViewMeetings: 用户名为空");
        }
        return result;
    }
    
    /**
     * 检查用户是否有权限查看特定会议
     * 管理员可以查看所有会议
     * 企业用户可以查看自己创建的会议和已通过的会议
     * 普通用户只能查看已通过的会议
     */
    public boolean canViewMeeting(String username, com.cemenghui.meeting.bean.Meeting meeting) {
        if (username == null || meeting == null) {
            log.warn("canViewMeeting: 用户名或会议为空");
            return false;
        }
        
        // 管理员可以查看所有会议
        if (isAdmin(username)) {
            return true;
        }
        
        // 企业用户可以查看自己创建的会议和已通过的会议
        if (isEnterpriseUser(username)) {
            boolean result = username.equals(meeting.getCreatorName()) || meeting.getStatus() == 1;
            if (!result) {
                log.info("canViewMeeting: 企业用户 {} 无权查看会议 {}", username, meeting.getId());
            }
            return result;
        }
        
        // 普通用户只能查看已通过的会议
        boolean result = meeting.getStatus() == 1;
        if (!result) {
            log.info("canViewMeeting: 普通用户 {} 无权查看未通过会议 {}", username, meeting.getId());
        }
        return result;
    }
} 