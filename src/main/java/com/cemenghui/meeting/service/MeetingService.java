package com.cemenghui.meeting.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cemenghui.meeting.bean.Meeting;
import com.cemenghui.meeting.bean.MeetingQuery;
import com.cemenghui.meeting.bean.MeetingReviewRequest;
import com.cemenghui.meeting.bean.MeetingReviewRecord;
import com.cemenghui.meeting.dao.MeetingDao;
import com.cemenghui.meeting.util.PermissionUtil;
import com.cemenghui.meeting.util.ValidationUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MeetingService {
    
    private final MeetingDao meetingDao;
    private final PermissionUtil permissionUtil;
    private final ValidationUtil validationUtil;
    @Value("${minio.endpoint}")
    private String minioEndpoint;
    @Value("${minio.bucket}")
    private String minioBucket;

    /**
     * 创建会议
     */
    @Transactional(rollbackFor = Exception.class)
    public Meeting createMeeting(Meeting meeting, String username) {
        try {
            // 参数验证
            if (meeting == null) {
                throw new IllegalArgumentException("会议数据不能为空");
            }
            
            if (username == null || username.trim().isEmpty()) {
                throw new IllegalArgumentException("用户名不能为空");
            }
            
            // 权限验证
            if (!permissionUtil.canCreateMeeting(username)) {
                throw new SecurityException("您没有权限创建会议");
            }
            
            // 数据验证
            String validationError = validationUtil.validateMeetingCreate(meeting);
            if (validationError != null) {
                throw new IllegalArgumentException(validationError);
            }
            
            // 设置创建人
            meeting.setCreator(username);
            
            // 设置初始状态：管理员创建的会议直接通过，企业用户创建的会议需要审核
            if (permissionUtil.isAdmin(username)) {
                meeting.setStatus(1); // 已通过
            } else {
                meeting.setStatus(0); // 待审核
            }
            
            // 设置创建时间和更新时间
            meeting.setCreateTime(LocalDateTime.now());
            meeting.setUpdateTime(LocalDateTime.now());
            meeting.setDeleted(0); // 设置逻辑删除字段
            
            // 插入数据库
            int result = meetingDao.insert(meeting);
            if (result <= 0) {
                throw new RuntimeException("会议创建失败，数据库插入异常");
            }
            
            log.info("用户 {} 成功创建会议: {} (ID: {})", username, meeting.getMeetingName(), meeting.getId());
            return meeting;
            
        } catch (SecurityException | IllegalArgumentException e) {
            log.warn("用户 {} 创建会议失败: {}", username, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("用户 {} 创建会议时发生系统错误", username, e);
            throw new RuntimeException("会议创建失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID获取会议详情
     */
    public Meeting getMeetingById(Long id) {
        try {
            if (id == null || id <= 0) {
                throw new IllegalArgumentException("会议ID无效");
            }
            
            Meeting meeting = meetingDao.selectById(id);
            if (meeting == null || meeting.getDeleted() == 1) {
                throw new IllegalArgumentException("会议不存在");
            }
            
            // 处理图片URL
            processImageUrl(meeting);
            
            return meeting;
            
        } catch (IllegalArgumentException e) {
            log.warn("获取会议详情失败: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("获取会议详情时发生系统错误, ID: {}", id, e);
            throw new RuntimeException("获取会议详情失败: " + e.getMessage());
        }
    }

    /**
     * 更新会议
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateMeeting(Meeting meeting, String username) {
        try {
            // 参数验证
            if (meeting == null) {
                throw new IllegalArgumentException("会议数据不能为空");
            }
            
            if (username == null || username.trim().isEmpty()) {
                throw new IllegalArgumentException("用户名不能为空");
            }
            
            // 数据验证
            String validationError = validationUtil.validateMeetingUpdate(meeting);
            if (validationError != null) {
                throw new IllegalArgumentException(validationError);
            }
            
            // 获取原会议信息
            Meeting originalMeeting = meetingDao.selectById(meeting.getId());
            if (originalMeeting == null || originalMeeting.getDeleted() == 1) {
                throw new IllegalArgumentException("会议不存在");
            }
            
            // 权限验证
            if (!permissionUtil.canEditMeeting(username, originalMeeting.getCreator())) {
                throw new SecurityException("您没有权限编辑此会议");
            }
            
            // 检查会议状态
            if (originalMeeting.getStatus() == 3) {
                throw new IllegalArgumentException("已删除的会议不能编辑");
            }
            
            // 设置更新时间
            meeting.setUpdateTime(LocalDateTime.now());
            
            // 更新数据库
            int result = meetingDao.updateById(meeting);
            if (result <= 0) {
                throw new RuntimeException("会议更新失败，数据库更新异常");
            }
            
            log.info("用户 {} 成功更新会议: {} (ID: {})", username, meeting.getMeetingName(), meeting.getId());
            return true;
            
        } catch (SecurityException | IllegalArgumentException e) {
            log.warn("用户 {} 更新会议失败: {}", username, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("用户 {} 更新会议时发生系统错误", username, e);
            throw new RuntimeException("会议更新失败: " + e.getMessage());
        }
    }

    /**
     * 删除会议
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteMeeting(Long id, String username, boolean confirmDelete) {
        try {
            // 参数验证
            if (id == null || id <= 0) {
                throw new IllegalArgumentException("会议ID无效");
            }
            
            if (username == null || username.trim().isEmpty()) {
                throw new IllegalArgumentException("用户名不能为空");
            }
            
            if (!confirmDelete) {
                throw new IllegalArgumentException("请确认删除操作");
            }
            
            // 获取会议信息
            Meeting meeting = meetingDao.selectById(id);
            if (meeting == null || meeting.getDeleted() == 1) {
                throw new IllegalArgumentException("会议不存在");
            }
            
            // 权限验证
            if (!permissionUtil.canDeleteMeeting(username, meeting.getCreator())) {
                throw new SecurityException("您没有权限删除此会议");
            }
            
            // 逻辑删除
            int result = meetingDao.deleteById(id);
            if (result <= 0) {
                throw new RuntimeException("会议删除失败，数据库删除异常");
            }
            
            log.info("用户 {} 成功删除会议: {} (ID: {})", username, meeting.getMeetingName(), id);
            return true;
            
        } catch (SecurityException | IllegalArgumentException e) {
            log.warn("用户 {} 删除会议失败: {}", username, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("用户 {} 删除会议时发生系统错误", username, e);
            throw new RuntimeException("会议删除失败: " + e.getMessage());
        }
    }

    /**
     * 审核会议
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean reviewMeeting(MeetingReviewRequest request, String reviewer) {
        try {
            // 参数验证
            if (request == null) {
                throw new IllegalArgumentException("审核请求不能为空");
            }
            
            if (reviewer == null || reviewer.trim().isEmpty()) {
                throw new IllegalArgumentException("审核人不能为空");
            }
            
            // 权限验证
            if (!permissionUtil.canReviewMeeting(reviewer)) {
                throw new SecurityException("您没有权限审核会议");
            }
            
            // 获取会议信息
            Meeting meeting = meetingDao.selectById(request.getMeetingId());
            if (meeting == null || meeting.getDeleted() == 1) {
                throw new IllegalArgumentException("会议不存在");
            }
            
            // 检查会议状态
            if (meeting.getStatus() != 0) {
                throw new IllegalArgumentException("只能审核待审核状态的会议");
            }
            
            // 更新会议状态
            meeting.setStatus(request.getStatus());
            meeting.setReviewer(reviewer);
            meeting.setReviewTime(LocalDateTime.now());
            meeting.setReviewComment(request.getReviewComment());
            meeting.setUpdateTime(LocalDateTime.now());
            
            int result = meetingDao.updateById(meeting);
            if (result <= 0) {
                throw new RuntimeException("会议审核失败，数据库更新异常");
            }
            
            // 插入审核记录
            MeetingReviewRecord record = new MeetingReviewRecord();
            record.setMeetingId(request.getMeetingId());
            record.setMeetingName(meeting.getMeetingName());
            record.setCreator(meeting.getCreator());
            record.setReviewer(reviewer);
            record.setStatus(request.getStatus());
            record.setReviewComment(request.getReviewComment());
            record.setReviewTime(LocalDateTime.now());
            
            meetingDao.insertReviewRecord(record);
            
            log.info("审核人 {} 成功审核会议: {} (ID: {}), 状态: {}", 
                reviewer, meeting.getMeetingName(), request.getMeetingId(), request.getStatus());
            return true;
            
        } catch (SecurityException | IllegalArgumentException e) {
            log.warn("审核人 {} 审核会议失败: {}", reviewer, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("审核人 {} 审核会议时发生系统错误", reviewer, e);
            throw new RuntimeException("会议审核失败: " + e.getMessage());
        }
    }

    /**
     * 根据条件查询会议
     */
    public Map<String, Object> getMeetingsByCondition(MeetingQuery query, String username) {
        try {
            // 参数验证
            if (query == null) {
                throw new IllegalArgumentException("查询条件不能为空");
            }
            
            if (username == null || username.trim().isEmpty()) {
                throw new IllegalArgumentException("用户名不能为空");
            }
            
            // 权限验证
            if (!permissionUtil.canViewMeetings(username)) {
                throw new SecurityException("您没有权限查看会议");
            }
            
            // 构建查询条件
            LambdaQueryWrapper<Meeting> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Meeting::getDeleted, 0); // 只查询未删除的会议
            
            // 根据用户权限设置查询条件
            if (permissionUtil.isAdmin(username)) {
                // 管理员只能看到自己审核的会议和未审核的会议
                wrapper.and(w -> w.eq(Meeting::getReviewer, username).or().eq(Meeting::getStatus, 0));
                if (query.getStatus() != null) {
                    wrapper.eq(Meeting::getStatus, query.getStatus());
                }
            } else if (permissionUtil.isEnterpriseUser(username)) {
                // 企业用户可以查看自己的会议和已通过的会议
                wrapper.and(w -> w.eq(Meeting::getCreator, username).or().eq(Meeting::getStatus, 1));
            } else {
                // 普通用户只能查看已通过的会议
                wrapper.eq(Meeting::getStatus, 1);
            }
            
            // 添加其他查询条件
            if (query.getMeetingName() != null && !query.getMeetingName().trim().isEmpty()) {
                wrapper.like(Meeting::getMeetingName, query.getMeetingName());
            }
            
            if (query.getCreator() != null && !query.getCreator().trim().isEmpty()) {
                wrapper.like(Meeting::getCreator, query.getCreator());
            }
            
            if (query.getStartDate() != null) {
                wrapper.ge(Meeting::getStartTime, query.getStartDate().atStartOfDay());
            }
            
            if (query.getEndDate() != null) {
                wrapper.le(Meeting::getEndTime, query.getEndDate().atTime(23, 59, 59));
            }
            
            // 排序
            wrapper.orderByDesc(Meeting::getCreateTime);
            
            // 分页查询
            Page<Meeting> page = new Page<>(query.getPage(), query.getSize());
            Page<Meeting> result = meetingDao.selectPage(page, wrapper);
            
            // 处理图片URL
            processImageUrls(result.getRecords());
            
            // 构建返回结果
            Map<String, Object> response = new HashMap<>();
            response.put("meetings", result.getRecords());
            response.put("total", result.getTotal());
            response.put("pages", result.getPages());
            response.put("current", result.getCurrent());
            response.put("size", result.getSize());
            
            return response;
            
        } catch (SecurityException | IllegalArgumentException e) {
            log.warn("用户 {} 查询会议失败: {}", username, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("用户 {} 查询会议时发生系统错误", username, e);
            throw new RuntimeException("查询会议失败: " + e.getMessage());
        }
    }

    /**
     * 获取待审核会议列表
     */
    public List<Meeting> getPendingMeetings(String username) {
        try {
            // 权限验证
            if (!permissionUtil.canReviewMeeting(username)) {
                throw new SecurityException("您没有权限查看待审核会议");
            }
            
            List<Meeting> meetings = meetingDao.findPendingMeetings();
            processImageUrls(meetings);
            return meetings;
            
        } catch (SecurityException e) {
            log.warn("用户 {} 获取待审核会议失败: {}", username, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("用户 {} 获取待审核会议时发生系统错误", username, e);
            throw new RuntimeException("获取待审核会议失败: " + e.getMessage());
        }
    }

    /**
     * 获取审核记录
     */
    public List<MeetingReviewRecord> getReviewRecordsByReviewer(String reviewer) {
        try {
            return meetingDao.findReviewRecordsByReviewer(reviewer);
        } catch (Exception e) {
            log.error("获取审核记录失败: {}", e.getMessage());
            throw new RuntimeException("获取审核记录失败: " + e.getMessage());
        }
    }

    public List<MeetingReviewRecord> getReviewRecordsByCreator(String creator) {
        try {
            return meetingDao.findReviewRecordsByCreator(creator);
        } catch (Exception e) {
            log.error("获取审核记录失败: {}", e.getMessage());
            throw new RuntimeException("获取审核记录失败: " + e.getMessage());
        }
    }

    public List<MeetingReviewRecord> getReviewRecordsByMeetingId(Long meetingId) {
        try {
            return meetingDao.findReviewRecordsByMeetingId(meetingId);
        } catch (Exception e) {
            log.error("获取审核记录失败: {}", e.getMessage());
            throw new RuntimeException("获取审核记录失败: " + e.getMessage());
        }
    }

    /**
     * 处理会议列表的图片URL
     */
    private void processImageUrls(List<Meeting> meetings) {
        if (meetings != null) {
            for (Meeting meeting : meetings) {
                processImageUrl(meeting);
            }
        }
    }

    /**
     * 处理单个会议的图片URL
     * 对于私有bucket，保持对象名称，前端需要通过API获取预签名URL
     */
    private void processImageUrl(Meeting meeting) {
        if (meeting != null && meeting.getImageUrl() != null && !meeting.getImageUrl().trim().isEmpty()) {
            try {
                String url = meeting.getImageUrl();
                // 如果是完整的HTTP URL，说明是公开访问的
                if (url.startsWith("http://") || url.startsWith("https://")) {
                    // 保持原有URL不变
                    return;
                }
                
                // 对于私有bucket，保持对象名称格式
                // 前端需要通过 /api/file/presigned-url/{objectName} 获取预签名URL
                // 这里不做任何修改，保持对象名称
                
            } catch (Exception e) {
                log.warn("处理会议图片URL失败: {}", e.getMessage());
            }
        }
    }
} 