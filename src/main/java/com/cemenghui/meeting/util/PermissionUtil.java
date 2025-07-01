package com.cemenghui.meeting.util;

import com.cemenghui.meeting.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PermissionUtil {
    
    @Autowired
    private UserDao userDao;
    
    /**
     * 检查用户是否为管理员
     */
    public boolean isAdmin(String username) {
        if (username == null) {
            return false;
        }
        var user = userDao.findByUsername(username);
        return user != null && "ADMIN".equals(user.getUserType());
    }
    
    /**
     * 检查用户是否为企业用户
     */
    public boolean isEnterpriseUser(String username) {
        if (username == null) {
            return false;
        }
        var user = userDao.findByUsername(username);
        return user != null && "ENTERPRISE".equals(user.getUserType());
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
        return username != null && (username.equals(creator) || isAdmin(username));
    }
    
    /**
     * 检查用户是否有权限删除会议
     * 只有创建者或管理员可以删除
     */
    public boolean canDeleteMeeting(String username, String creator) {
        return username != null && (username.equals(creator) || isAdmin(username));
    }
    
    /**
     * 检查用户是否有权限审核会议
     * 只有管理员可以审核
     */
    public boolean canReviewMeeting(String username) {
        return isAdmin(username);
    }
    
    /**
     * 检查用户是否有权限创建会议
     * 管理员和企业用户可以创建会议
     */
    public boolean canCreateMeeting(String username) {
        return isAdmin(username) || isEnterpriseUser(username);
    }
    
    /**
     * 检查用户是否有权限查看所有会议
     * 所有用户都可以查看已通过的会议
     */
    public boolean canViewMeetings(String username) {
        return username != null;
    }
    
    /**
     * 检查用户是否有权限查看特定会议
     * 管理员可以查看所有会议
     * 企业用户可以查看自己创建的会议和已通过的会议
     * 普通用户只能查看已通过的会议
     */
    public boolean canViewMeeting(String username, com.cemenghui.meeting.bean.Meeting meeting) {
        if (username == null || meeting == null) {
            return false;
        }
        
        // 管理员可以查看所有会议
        if (isAdmin(username)) {
            return true;
        }
        
        // 企业用户可以查看自己创建的会议和已通过的会议
        if (isEnterpriseUser(username)) {
            return username.equals(meeting.getCreator()) || meeting.getStatus() == 1;
        }
        
        // 普通用户只能查看已通过的会议
        return meeting.getStatus() == 1;
    }
} 