package com.cemenghui.meeting.service;

import com.cemenghui.meeting.entity.Meeting;
import com.cemenghui.meeting.entity.MeetingQuery;
import com.cemenghui.meeting.entity.MeetingReviewRequest;
import com.cemenghui.meeting.entity.MeetingReviewRecord;
import com.cemenghui.meeting.dao.MeetingDao;
import com.cemenghui.meeting.util.PermissionUtil;
import com.cemenghui.meeting.util.ValidationUtil;
import com.cemenghui.meeting.controller.GlobalExceptionHandler;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class MeetingService {
    private static final Logger logger = LoggerFactory.getLogger(MeetingService.class);
    
    @Autowired
    private MeetingDao meetingDao;
    
    @Autowired
    private PermissionUtil permissionUtil;
    
    @Autowired
    private ValidationUtil validationUtil;
    
    @Autowired
    private MinioService minioService;

    /**
     * 创建会议
     */
    @Transactional(rollbackFor = Exception.class)
    public Meeting createMeeting(Meeting meeting, String username) {
        try {
            // 参数验证
            if (meeting == null) {
                throw new GlobalExceptionHandler.BusinessException("会议数据不能为空");
            }
            
            if (username == null || username.trim().isEmpty()) {
                throw new GlobalExceptionHandler.BusinessException("用户名不能为空");
            }
            
            // 权限验证
            if (!permissionUtil.canCreateMeeting(username)) {
                throw new SecurityException("您没有权限创建会议");
            }
            
            // 数据验证
            String validationError = validationUtil.validateMeetingCreate(meeting);
            if (validationError != null) {
                throw new GlobalExceptionHandler.BusinessException(validationError);
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
            meeting.setCreateTime(java.time.LocalDateTime.now());
            meeting.setUpdateTime(java.time.LocalDateTime.now());
            
            // 插入数据库
            int result = meetingDao.insert(meeting);
            if (result <= 0) {
                throw new RuntimeException("会议创建失败，数据库插入异常");
            }
            
            logger.info("用户 {} 成功创建会议: {} (ID: {})", username, meeting.getMeetingName(), meeting.getId());
            return meeting;
            
        } catch (SecurityException | GlobalExceptionHandler.BusinessException e) {
            logger.warn("用户 {} 创建会议失败: {}", username, e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("用户 {} 创建会议时发生系统错误", username, e);
            throw new RuntimeException("会议创建失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID获取会议详情
     */
    public Meeting getMeetingById(Long id) {
        try {
            if (id == null || id <= 0) {
                throw new GlobalExceptionHandler.BusinessException("会议ID无效");
            }
            
            Meeting meeting = meetingDao.findById(id);
            if (meeting == null) {
                throw new GlobalExceptionHandler.ResourceNotFoundException("会议不存在");
            }
            
            // 处理图片URL
            processImageUrl(meeting);
            
            return meeting;
            
        } catch (GlobalExceptionHandler.ResourceNotFoundException | GlobalExceptionHandler.BusinessException e) {
            logger.warn("获取会议详情失败: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("获取会议详情时发生系统错误, ID: {}", id, e);
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
                throw new GlobalExceptionHandler.BusinessException("会议数据不能为空");
            }
            
            if (username == null || username.trim().isEmpty()) {
                throw new GlobalExceptionHandler.BusinessException("用户名不能为空");
            }
            
            // 数据验证
            String validationError = validationUtil.validateMeetingUpdate(meeting);
            if (validationError != null) {
                throw new GlobalExceptionHandler.BusinessException(validationError);
            }
            
            // 获取原会议信息
            Meeting originalMeeting = meetingDao.findById(meeting.getId());
            if (originalMeeting == null) {
                throw new GlobalExceptionHandler.ResourceNotFoundException("会议不存在");
            }
            
            // 权限验证
            if (!permissionUtil.canEditMeeting(username, originalMeeting.getCreator())) {
                throw new SecurityException("您没有权限编辑此会议");
            }
            
            // 检查会议状态
            if (originalMeeting.getStatus() == 3) {
                throw new GlobalExceptionHandler.BusinessException("已删除的会议不能编辑");
            }
            
            // 设置更新时间
            meeting.setUpdateTime(java.time.LocalDateTime.now());
            
            // 更新数据库
            int result = meetingDao.update(meeting);
            if (result <= 0) {
                throw new RuntimeException("会议更新失败，数据库更新异常");
            }
            
            logger.info("用户 {} 成功更新会议: {} (ID: {})", username, meeting.getMeetingName(), meeting.getId());
            return true;
            
        } catch (SecurityException | GlobalExceptionHandler.BusinessException | GlobalExceptionHandler.ResourceNotFoundException e) {
            logger.warn("用户 {} 更新会议失败: {}", username, e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("用户 {} 更新会议时发生系统错误", username, e);
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
                throw new GlobalExceptionHandler.BusinessException("会议ID无效");
            }
            
            if (username == null || username.trim().isEmpty()) {
                throw new GlobalExceptionHandler.BusinessException("用户名不能为空");
            }
            
            if (!confirmDelete) {
                throw new GlobalExceptionHandler.BusinessException("请确认删除操作");
            }
            
            // 获取会议信息
            Meeting meeting = meetingDao.findById(id);
            if (meeting == null) {
                throw new GlobalExceptionHandler.ResourceNotFoundException("会议不存在");
            }
            
            // 权限验证
            if (!permissionUtil.canDeleteMeeting(username, meeting.getCreator())) {
                throw new SecurityException("您没有权限删除此会议");
            }
            
            // 检查会议状态
            if (meeting.getStatus() == 3) {
                throw new GlobalExceptionHandler.BusinessException("会议已经被删除");
            }
            
            // 删除数据库记录
            int result = meetingDao.deleteById(id);
            if (result <= 0) {
                throw new RuntimeException("会议删除失败，数据库删除异常");
            }
            
            logger.info("用户 {} 成功删除会议: {} (ID: {})", username, meeting.getMeetingName(), id);
            return true;
            
        } catch (SecurityException | GlobalExceptionHandler.BusinessException | GlobalExceptionHandler.ResourceNotFoundException e) {
            logger.warn("用户 {} 删除会议失败: {}", username, e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("用户 {} 删除会议时发生系统错误", username, e);
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
                throw new GlobalExceptionHandler.BusinessException("审核请求不能为空");
            }
            
            if (reviewer == null || reviewer.trim().isEmpty()) {
                throw new GlobalExceptionHandler.BusinessException("审核人不能为空");
            }
            
            // 验证审核请求
            String validationError = validationUtil.validateReviewRequest(
                request.getMeetingId(), request.getStatus(), request.getReviewComment());
            if (validationError != null) {
                throw new GlobalExceptionHandler.BusinessException(validationError);
            }
            
            // 权限验证
            if (!permissionUtil.canReviewMeeting(reviewer)) {
                throw new SecurityException("您没有权限审核会议");
            }
            
            // 获取会议信息
            Meeting meeting = meetingDao.findById(request.getMeetingId());
            if (meeting == null) {
                throw new GlobalExceptionHandler.ResourceNotFoundException("会议不存在");
            }
            
            // 检查会议状态
            if (meeting.getStatus() != 0) {
                throw new GlobalExceptionHandler.BusinessException("只能审核待审核状态的会议");
            }
            
            // 检查是否审核自己的会议
            if (meeting.getCreator().equals(reviewer)) {
                throw new GlobalExceptionHandler.BusinessException("不能审核自己创建的会议");
            }
            
            // 更新会议状态
            int result = meetingDao.review(request.getMeetingId(), request.getStatus(), 
                                    reviewer, request.getReviewComment());
            if (result <= 0) {
                throw new RuntimeException("会议审核失败，数据库更新异常");
            }
            
            // 插入审核记录
            MeetingReviewRecord record = new MeetingReviewRecord();
            record.setMeetingId(meeting.getId());
            record.setMeetingName(meeting.getMeetingName());
            record.setCreator(meeting.getCreator());
            record.setReviewer(reviewer);
            record.setStatus(request.getStatus());
            record.setReviewComment(request.getReviewComment());
            record.setReviewTime(java.time.LocalDateTime.now());
            
            int recordResult = meetingDao.insertReviewRecord(record);
            if (recordResult <= 0) {
                throw new RuntimeException("审核记录创建失败");
            }
            
            logger.info("审核人 {} 成功审核会议: {} (ID: {}), 状态: {}", 
                reviewer, meeting.getMeetingName(), meeting.getId(), 
                request.getStatus() == 1 ? "通过" : "拒绝");
            
            return true;
            
        } catch (SecurityException | GlobalExceptionHandler.BusinessException | GlobalExceptionHandler.ResourceNotFoundException e) {
            logger.warn("审核人 {} 审核会议失败: {}", reviewer, e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("审核人 {} 审核会议时发生系统错误", reviewer, e);
            throw new RuntimeException("会议审核失败: " + e.getMessage());
        }
    }

    /**
     * 根据条件查询会议列表
     */
    public Map<String, Object> getMeetingsByCondition(MeetingQuery query, String username) {
        try {
            // 参数验证
            if (query == null) {
                throw new GlobalExceptionHandler.BusinessException("查询参数不能为空");
            }
            
            if (username == null || username.trim().isEmpty()) {
                throw new GlobalExceptionHandler.BusinessException("用户名不能为空");
            }
            
            // 权限验证
            if (!permissionUtil.canViewMeetings(username)) {
                throw new SecurityException("您没有权限查看会议");
            }
            
            // 验证查询参数
            String validationError = validationUtil.validateMeetingQuery(
                query.getPage(), query.getSize(), query.getMeetingName(), 
                query.getCreator(), query.getStartDate() != null ? query.getStartDate().toString() : null,
                query.getEndDate() != null ? query.getEndDate().toString() : null);
            if (validationError != null) {
                throw new GlobalExceptionHandler.BusinessException(validationError);
            }
            
            // 设置默认分页参数
            if (query.getPage() == null || query.getPage() < 1) {
                query.setPage(1);
            }
            if (query.getSize() == null || query.getSize() < 1 || query.getSize() > 100) {
                query.setSize(20);
            }
            
            int offset = (query.getPage() - 1) * query.getSize();
            
            String startDate = query.getStartDate() != null ? 
                query.getStartDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : null;
            String endDate = query.getEndDate() != null ? 
                query.getEndDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : null;
            
            List<Meeting> meetings;
            int total;
            
            // 根据用户权限和查询条件选择合适的方法
            if (permissionUtil.isAdmin(username)) {
                // 管理员可以查看所有会议
                if (query.getMeetingName() != null && !query.getMeetingName().isEmpty() && 
                    query.getCreator() != null && !query.getCreator().isEmpty()) {
                    // 按会议名称和创建人查询
                    meetings = meetingDao.findByMeetingNameAndCreatorAndAdminRelated(
                        query.getMeetingName(), query.getCreator(), username, query.getSize(), offset
                    );
                    total = meetingDao.countByMeetingNameAndCreatorAndAdminRelated(query.getMeetingName(), query.getCreator(), username);
                } else if (query.getMeetingName() != null && !query.getMeetingName().isEmpty()) {
                    // 按会议名称查询
                    meetings = meetingDao.findByMeetingNameAndAdminRelated(
                        query.getMeetingName(), username, query.getSize(), offset
                    );
                    total = meetingDao.countByMeetingNameAndAdminRelated(query.getMeetingName(), username);
                } else if (query.getCreator() != null && !query.getCreator().isEmpty()) {
                    // 按创建人查询
                    meetings = meetingDao.findByCreatorAndAdminRelated(
                        query.getCreator(), username, query.getSize(), offset
                    );
                    total = meetingDao.countByCreatorAndAdminRelated(query.getCreator(), username);
                } else if (startDate != null && endDate != null) {
                    // 按日期范围查询
                    meetings = meetingDao.findByDateRangeAndAdminRelated(
                        startDate, endDate, username, query.getSize(), offset
                    );
                    total = meetingDao.countByDateRangeAndAdminRelated(startDate, endDate, username);
                } else if (startDate != null) {
                    // 按开始日期查询
                    meetings = meetingDao.findByStartDateAndAdminRelated(
                        startDate, username, query.getSize(), offset
                    );
                    total = meetingDao.countByStartDateAndAdminRelated(startDate, username);
                } else {
                    // 查询所有会议
                    meetings = meetingDao.findByAdminRelated(username, query.getSize(), offset);
                    total = meetingDao.countByAdminRelated(username);
                }
            } else if (permissionUtil.isEnterpriseUser(username)) {
                // 企业用户可以看到自己创建的会议和已通过的会议
                if (query.getMeetingName() != null && !query.getMeetingName().isEmpty() && 
                    query.getCreator() != null && !query.getCreator().isEmpty()) {
                    // 按会议名称和创建人查询
                    meetings = meetingDao.findByMeetingNameAndCreator(
                        query.getMeetingName(), query.getCreator(), query.getSize(), offset
                    );
                    total = meetingDao.countByMeetingNameAndCreator(query.getMeetingName(), query.getCreator());
                } else if (query.getMeetingName() != null && !query.getMeetingName().isEmpty()) {
                    // 按会议名称查询
                    meetings = meetingDao.findByMeetingNameAndCreatorOrApproved(
                        query.getMeetingName(), username, query.getSize(), offset
                    );
                    total = meetingDao.countByMeetingNameAndCreatorOrApproved(query.getMeetingName(), username);
                } else if (query.getCreator() != null && !query.getCreator().isEmpty()) {
                    // 按创建人查询
                    meetings = meetingDao.findByCreator(
                        query.getCreator(), query.getSize(), offset
                    );
                    total = meetingDao.countByCreator(query.getCreator());
                } else if (startDate != null && endDate != null) {
                    // 按日期范围查询
                    meetings = meetingDao.findByDateRangeAndCreatorOrApproved(
                        startDate, endDate, username, query.getSize(), offset
                    );
                    total = meetingDao.countByDateRangeAndCreatorOrApproved(startDate, endDate, username);
                } else if (startDate != null) {
                    // 按开始日期查询
                    meetings = meetingDao.findByStartDateAndCreatorOrApproved(
                        startDate, username, query.getSize(), offset
                    );
                    total = meetingDao.countByStartDateAndCreatorOrApproved(startDate, username);
                } else {
                    // 查询自己的会议和已通过的会议
                    meetings = meetingDao.findByCreatorOrApproved(username, query.getSize(), offset);
                    total = meetingDao.countByCreatorOrApproved(username);
                }
            } else {
                // 普通用户只能查看已通过的会议
                if (query.getMeetingName() != null && !query.getMeetingName().isEmpty() && 
                    query.getCreator() != null && !query.getCreator().isEmpty()) {
                    // 按会议名称和创建人查询
                    meetings = meetingDao.findByMeetingNameAndCreator(
                        query.getMeetingName(), query.getCreator(), query.getSize(), offset
                    );
                    total = meetingDao.countByMeetingNameAndCreator(query.getMeetingName(), query.getCreator());
                } else if (query.getMeetingName() != null && !query.getMeetingName().isEmpty()) {
                    // 按会议名称查询
                    meetings = meetingDao.findByMeetingNameAndStatus(
                        query.getMeetingName(), 1, query.getSize(), offset
                    );
                    total = meetingDao.countByMeetingNameAndStatus(query.getMeetingName(), 1);
                } else if (query.getCreator() != null && !query.getCreator().isEmpty()) {
                    // 按创建人查询
                    meetings = meetingDao.findByCreator(
                        query.getCreator(), query.getSize(), offset
                    );
                    total = meetingDao.countByCreator(query.getCreator());
                } else if (startDate != null && endDate != null) {
                    // 按日期范围查询
                    meetings = meetingDao.findByDateRangeAndStatus(
                        startDate, endDate, 1, query.getSize(), offset
                    );
                    total = meetingDao.countByDateRangeAndStatus(startDate, endDate, 1);
                } else if (startDate != null) {
                    // 按开始日期查询
                    meetings = meetingDao.findByStartDateAndStatus(
                        startDate, 1, query.getSize(), offset
                    );
                    total = meetingDao.countByStartDateAndStatus(startDate, 1);
                } else {
                    // 查询所有已通过的会议
                    meetings = meetingDao.findByStatus(1, query.getSize(), offset);
                    total = meetingDao.countByStatus(1);
                }
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("meetings", meetings);
            result.put("total", total);
            result.put("page", query.getPage());
            result.put("size", query.getSize());
            result.put("pages", (int) Math.ceil((double) total / query.getSize()));
            
            // 处理会议列表中的图片URL
            processImageUrls(meetings);
            
            logger.info("用户 {} 成功查询会议列表，共 {} 条记录", username, total);
            return result;
            
        } catch (SecurityException | GlobalExceptionHandler.BusinessException e) {
            logger.warn("用户 {} 查询会议列表失败: {}", username, e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("用户 {} 查询会议列表时发生系统错误", username, e);
            throw new RuntimeException("查询会议列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取待审核会议列表（无参数版本，用于管理员查看所有待审核会议）
     */
    public List<Meeting> getPendingMeetings() {
        try {
            List<Meeting> meetings = meetingDao.findPendingMeetings();
            
            // 处理会议列表中的图片URL
            processImageUrls(meetings);
            
            logger.info("成功查询待审核会议列表，共 {} 条记录", meetings.size());
            return meetings;
            
        } catch (Exception e) {
            logger.error("查询待审核会议时发生系统错误", e);
            throw new RuntimeException("查询待审核会议失败: " + e.getMessage());
        }
    }

    /**
     * 获取待审核会议列表（带用户名参数版本，用于权限验证）
     */
    public List<Meeting> getPendingMeetings(String username) {
        try {
            if (username == null || username.trim().isEmpty()) {
                throw new GlobalExceptionHandler.BusinessException("用户名不能为空");
            }
            
            // 权限验证
            if (!permissionUtil.canReviewMeeting(username)) {
                throw new SecurityException("您没有权限查看待审核会议");
            }
            
            List<Meeting> meetings = meetingDao.findPendingMeetings();
            
            // 处理会议列表中的图片URL
            processImageUrls(meetings);
            
            logger.info("用户 {} 成功查询待审核会议列表，共 {} 条记录", username, meetings.size());
            return meetings;
            
        } catch (SecurityException | GlobalExceptionHandler.BusinessException e) {
            logger.warn("用户 {} 查询待审核会议失败: {}", username, e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("用户 {} 查询待审核会议时发生系统错误", username, e);
            throw new RuntimeException("查询待审核会议失败: " + e.getMessage());
        }
    }

    /**
     * 根据审核人获取审核记录
     */
    public List<MeetingReviewRecord> getReviewRecordsByReviewer(String reviewer) {
        try {
            if (reviewer == null || reviewer.trim().isEmpty()) {
                throw new GlobalExceptionHandler.BusinessException("审核人不能为空");
            }
            
            List<MeetingReviewRecord> records = meetingDao.findReviewRecordsByReviewer(reviewer);
            logger.info("成功查询审核人 {} 的审核记录，共 {} 条", reviewer, records.size());
            return records;
            
        } catch (GlobalExceptionHandler.BusinessException e) {
            logger.warn("查询审核记录失败: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("查询审核记录时发生系统错误", e);
            throw new RuntimeException("查询审核记录失败: " + e.getMessage());
        }
    }

    /**
     * 根据创建人获取审核记录
     */
    public List<MeetingReviewRecord> getReviewRecordsByCreator(String creator) {
        try {
            if (creator == null || creator.trim().isEmpty()) {
                throw new GlobalExceptionHandler.BusinessException("创建人不能为空");
            }
            
            List<MeetingReviewRecord> records = meetingDao.findReviewRecordsByCreator(creator);
            logger.info("成功查询创建人 {} 的审核记录，共 {} 条", creator, records.size());
            return records;
            
        } catch (GlobalExceptionHandler.BusinessException e) {
            logger.warn("查询审核记录失败: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("查询审核记录时发生系统错误", e);
            throw new RuntimeException("查询审核记录失败: " + e.getMessage());
        }
    }

    /**
     * 根据会议ID获取审核记录
     */
    public List<MeetingReviewRecord> getReviewRecordsByMeeting(Long meetingId) {
        try {
            if (meetingId == null || meetingId <= 0) {
                throw new GlobalExceptionHandler.BusinessException("会议ID无效");
            }
            
            List<MeetingReviewRecord> records = meetingDao.findReviewRecordsByMeetingId(meetingId);
            logger.info("成功查询会议 {} 的审核记录，共 {} 条", meetingId, records.size());
            return records;
            
        } catch (GlobalExceptionHandler.BusinessException e) {
            logger.warn("查询审核记录失败: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("查询审核记录时发生系统错误", e);
            throw new RuntimeException("查询审核记录失败: " + e.getMessage());
        }
    }

    /**
     * 根据会议ID获取审核记录（保持向后兼容）
     */
    public List<MeetingReviewRecord> getReviewRecordsByMeetingId(Long meetingId) {
        return getReviewRecordsByMeeting(meetingId);
    }

    /**
     * 处理会议列表中的图片URL
     */
    private void processImageUrls(List<Meeting> meetings) {
        if (meetings == null || meetings.isEmpty()) {
            return;
        }
        
        for (Meeting meeting : meetings) {
            if (meeting.getImageUrl() != null && !meeting.getImageUrl().startsWith("http")) {
                // 如果存储的是文件名而不是完整URL，则生成预签名URL
                try {
                    String presignedUrl = minioService.getPresignedUrl(meeting.getImageUrl());
                    meeting.setImageUrl(presignedUrl);
                } catch (Exception e) {
                    logger.warn("生成会议图片预签名URL失败: {}, 会议ID: {}", meeting.getImageUrl(), meeting.getId());
                    // 如果生成失败，保持原URL不变
                }
            }
        }
    }

    /**
     * 处理单个会议的图片URL
     */
    private void processImageUrl(Meeting meeting) {
        if (meeting != null && meeting.getImageUrl() != null && !meeting.getImageUrl().startsWith("http")) {
            try {
                String presignedUrl = minioService.getPresignedUrl(meeting.getImageUrl());
                meeting.setImageUrl(presignedUrl);
            } catch (Exception e) {
                logger.warn("生成会议图片预签名URL失败: {}, 会议ID: {}", meeting.getImageUrl(), meeting.getId());
                // 如果生成失败，保持原URL不变
            }
        }
    }
} 