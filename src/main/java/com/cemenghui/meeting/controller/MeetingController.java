package com.cemenghui.meeting.controller;

import com.cemenghui.meeting.bean.*;
import com.cemenghui.meeting.service.MeetingService;
import com.cemenghui.meeting.util.JwtUtil;
import com.cemenghui.meeting.util.PermissionUtil;
import com.cemenghui.meeting.service.MinioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/meeting")
public class MeetingController {
    private static final Logger logger = LoggerFactory.getLogger(MeetingController.class);
    
    @Autowired
    private MeetingService meetingService;
    
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PermissionUtil permissionUtil;

    @Autowired
    private MinioService minioService;

    /**
     * 验证token并返回用户名
     */
    private String validateTokenAndGetUsername(String token) {
        if (token == null || token.trim().isEmpty()) {
            logger.warn("Token为空");
            throw new IllegalArgumentException("Token不能为空");
        }
        
        String cleanToken = token.replace("Bearer ", "");
        if (!jwtUtil.validateToken(cleanToken)) {
            logger.warn("Token验证失败: {}", cleanToken.substring(0, Math.min(10, cleanToken.length())));
            throw new IllegalArgumentException("无效的token");
        }
        
        try {
            return jwtUtil.getUsernameFromToken(cleanToken);
        } catch (Exception e) {
            logger.error("从Token获取用户名失败", e);
            throw new IllegalArgumentException("Token解析失败");
        }
    }

    @PostMapping("/create")
    public ApiResponse<Meeting> createMeeting(@RequestBody Meeting meeting, 
                                             @RequestHeader("Authorization") String token) {
        String username = null;
        try {
            username = validateTokenAndGetUsername(token);
            logger.info("用户 {} 开始创建会议: {}", username, meeting.getMeetingName());
            
            Meeting createdMeeting = meetingService.createMeeting(meeting, username);
            logger.info("用户 {} 成功创建会议: {}", username, createdMeeting.getId());
            return ApiResponse.success("会议创建成功", createdMeeting);
        } catch (IllegalArgumentException e) {
            logger.warn("用户 {} 创建会议失败 - 参数错误: {}", username, e.getMessage());
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            logger.error("用户 {} 创建会议失败 - 系统错误", username, e);
            return ApiResponse.error("会议创建失败: " + e.getMessage());
        }
    }

    @PostMapping("/update")
    public ApiResponse<Boolean> updateMeeting(@RequestBody Meeting meeting,
                                             @RequestHeader("Authorization") String token) {
        String username = null;
        try {
            username = validateTokenAndGetUsername(token);
            logger.info("用户 {} 开始更新会议: {}", username, meeting.getId());
            
            boolean success = meetingService.updateMeeting(meeting, username);
            if (success) {
                logger.info("用户 {} 成功更新会议: {}", username, meeting.getId());
                return ApiResponse.success("会议更新成功", true);
            } else {
                logger.warn("用户 {} 更新会议失败: {}", username, meeting.getId());
                return ApiResponse.error("会议更新失败");
            }
        } catch (IllegalArgumentException e) {
            logger.warn("用户 {} 更新会议失败 - 参数错误: {}", username, e.getMessage());
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            logger.error("用户 {} 更新会议失败 - 系统错误", username, e);
            return ApiResponse.error("会议更新失败: " + e.getMessage());
        }
    }

    @PostMapping("/delete")
    public ApiResponse<Boolean> deleteMeeting(@RequestBody Map<String, Object> request,
                                             @RequestHeader("Authorization") String token) {
        String username = null;
        try {
            username = validateTokenAndGetUsername(token);
            
            if (request == null || !request.containsKey("meeting_id")) {
                logger.warn("用户 {} 删除会议失败 - 缺少meeting_id参数", username);
                return ApiResponse.error(400, "缺少会议ID参数");
            }
            
            Long meetingId = Long.valueOf(request.get("meeting_id").toString());
            Boolean confirmDelete = Boolean.valueOf(request.get("confirm_delete").toString());
            
            logger.info("用户 {} 开始删除会议: {}, 确认删除: {}", username, meetingId, confirmDelete);
            
            boolean success = meetingService.deleteMeeting(meetingId, username, confirmDelete);
            if (success) {
                logger.info("用户 {} 成功删除会议: {}", username, meetingId);
                return ApiResponse.success("会议删除成功", true);
            } else {
                logger.warn("用户 {} 删除会议失败: {}", username, meetingId);
                return ApiResponse.error("会议删除失败");
            }
        } catch (NumberFormatException e) {
            logger.warn("用户 {} 删除会议失败 - meeting_id格式错误", username);
            return ApiResponse.error(400, "会议ID格式错误");
        } catch (IllegalArgumentException e) {
            logger.warn("用户 {} 删除会议失败 - 参数错误: {}", username, e.getMessage());
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            logger.error("用户 {} 删除会议失败 - 系统错误", username, e);
            return ApiResponse.error("会议删除失败: " + e.getMessage());
        }
    }

    @PostMapping("/review")
    public ApiResponse<Boolean> reviewMeeting(@RequestBody MeetingReviewRequest request,
                                             @RequestHeader("Authorization") String token) {
        String reviewer = null;
        try {
            reviewer = validateTokenAndGetUsername(token);
            logger.info("审核人 {} 开始审核会议: {}", reviewer, request.getMeetingId());
            
            boolean success = meetingService.reviewMeeting(request, reviewer);
            if (success) {
                logger.info("审核人 {} 成功审核会议: {}", reviewer, request.getMeetingId());
                return ApiResponse.success("会议审核成功", true);
            } else {
                logger.warn("审核人 {} 审核会议失败: {}", reviewer, request.getMeetingId());
                return ApiResponse.error("会议审核失败");
            }
        } catch (IllegalArgumentException e) {
            logger.warn("审核人 {} 审核会议失败 - 参数错误: {}", reviewer, e.getMessage());
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            logger.error("审核人 {} 审核会议失败 - 系统错误", reviewer, e);
            return ApiResponse.error("会议审核失败: " + e.getMessage());
        }
    }

    @PostMapping("/list")
    public ApiResponse<Map<String, Object>> getMeetings(@RequestBody MeetingQuery query,
                                                        @RequestHeader("Authorization") String token) {
        String username = null;
        try {
            username = validateTokenAndGetUsername(token);
            logger.info("用户 {} 开始查询会议列表", username);
            
            Map<String, Object> result = meetingService.getMeetingsByCondition(query, username);
            logger.info("用户 {} 成功查询会议列表，共 {} 条记录", username, result.get("total"));
            return ApiResponse.success("查询成功", result);
        } catch (IllegalArgumentException e) {
            logger.warn("用户 {} 查询会议列表失败 - 参数错误: {}", username, e.getMessage());
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            logger.error("用户 {} 查询会议列表失败 - 系统错误", username, e);
            return ApiResponse.error("查询失败: " + e.getMessage());
        }
    }

    @PostMapping("/detail")
    public ApiResponse<Meeting> getMeetingDetail(@RequestBody Map<String, Long> request,
                                                @RequestHeader("Authorization") String token) {
        String username = null;
        try {
            username = validateTokenAndGetUsername(token);
            
            if (request == null || !request.containsKey("meeting_id")) {
                logger.warn("用户 {} 查询会议详情失败 - 缺少meeting_id参数", username);
                return ApiResponse.error(400, "缺少会议ID参数");
            }
            
            Long meetingId = request.get("meeting_id");
            logger.info("用户 {} 开始查询会议详情: {}", username, meetingId);
            
            Meeting meeting = meetingService.getMeetingById(meetingId);
            if (meeting == null) {
                logger.warn("用户 {} 查询会议详情失败 - 会议不存在: {}", username, meetingId);
                return ApiResponse.error("会议不存在");
            }
            
            // 权限验证：检查用户是否有权限查看此会议
            if (!permissionUtil.canViewMeeting(username, meeting)) {
                logger.warn("用户 {} 查询会议详情失败 - 权限不足: {}", username, meetingId);
                return ApiResponse.error(403, "您没有权限查看此会议");
            }
            
            logger.info("用户 {} 成功查询会议详情: {}", username, meetingId);
            return ApiResponse.success("查询成功", meeting);
        } catch (Exception e) {
            logger.error("用户 {} 查询会议详情失败 - 系统错误", username, e);
            return ApiResponse.error("查询失败: " + e.getMessage());
        }
    }

    @PostMapping("/pending")
    public ApiResponse<java.util.List<Meeting>> getPendingMeetings(@RequestHeader("Authorization") String token) {
        String username = null;
        try {
            username = validateTokenAndGetUsername(token);
            logger.info("用户 {} 开始查询待审核会议", username);
            
            java.util.List<Meeting> meetings = meetingService.getPendingMeetings(username);
            logger.info("用户 {} 成功查询待审核会议，共 {} 条记录", username, meetings.size());
            return ApiResponse.success("查询成功", meetings);
        } catch (IllegalArgumentException e) {
            logger.warn("用户 {} 查询待审核会议失败 - 参数错误: {}", username, e.getMessage());
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            logger.error("用户 {} 查询待审核会议失败 - 系统错误", username, e);
            return ApiResponse.error("查询失败: " + e.getMessage());
        }
    }

    @PostMapping("/review/records/by-reviewer")
    public ApiResponse<java.util.List<MeetingReviewRecord>> getReviewRecordsByReviewer(@RequestHeader("Authorization") String token) {
        String reviewer = null;
        try {
            reviewer = validateTokenAndGetUsername(token);
            logger.info("审核人 {} 开始查询审核记录", reviewer);
            
            java.util.List<MeetingReviewRecord> records = meetingService.getReviewRecordsByReviewer(reviewer);
            logger.info("审核人 {} 成功查询审核记录，共 {} 条记录", reviewer, records.size());
            return ApiResponse.success("查询成功", records);
        } catch (Exception e) {
            logger.error("审核人 {} 查询审核记录失败 - 系统错误", reviewer, e);
            return ApiResponse.error("查询失败: " + e.getMessage());
        }
    }

    @PostMapping("/review/records/by-creator")
    public ApiResponse<java.util.List<MeetingReviewRecord>> getReviewRecordsByCreator(@RequestHeader("Authorization") String token) {
        String creator = null;
        try {
            creator = validateTokenAndGetUsername(token);
            logger.info("创建人 {} 开始查询审核记录", creator);
            
            java.util.List<MeetingReviewRecord> records = meetingService.getReviewRecordsByCreator(creator);
            logger.info("创建人 {} 成功查询审核记录，共 {} 条记录", creator, records.size());
            return ApiResponse.success("查询成功", records);
        } catch (Exception e) {
            logger.error("创建人 {} 查询审核记录失败 - 系统错误", creator, e);
            return ApiResponse.error("查询失败: " + e.getMessage());
        }
    }

    @PostMapping("/review/records/by-meeting")
    public ApiResponse<java.util.List<MeetingReviewRecord>> getReviewRecordsByMeeting(@RequestBody Map<String, Long> request, @RequestHeader("Authorization") String token) {
        try {
            validateTokenAndGetUsername(token); // 验证token但不使用用户名
            
            if (request == null || !request.containsKey("meeting_id")) {
                logger.warn("查询会议审核记录失败 - 缺少meeting_id参数");
                return ApiResponse.error(400, "缺少会议ID参数");
            }
            
            Long meetingId = request.get("meeting_id");
            logger.info("开始查询会议审核记录: {}", meetingId);
            
            java.util.List<MeetingReviewRecord> records = meetingService.getReviewRecordsByMeetingId(meetingId);
            logger.info("成功查询会议审核记录: {}, 共 {} 条记录", meetingId, records.size());
            return ApiResponse.success("查询成功", records);
        } catch (Exception e) {
            logger.error("查询会议审核记录失败 - 系统错误", e);
            return ApiResponse.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 上传会议图片
     */
    @PostMapping("/uploadImage")
    public ApiResponse<String> uploadMeetingImage(@RequestParam("file") MultipartFile file) {
        try {
            ApiResponse<String> uploadResult = minioService.uploadFile(file);
            if (uploadResult.getCode() == 200) {
                return ApiResponse.success("上传成功", uploadResult.getData());
            } else {
                return ApiResponse.error(uploadResult.getCode(), uploadResult.getMessage());
            }
        } catch (Exception e) {
            logger.error("会议图片上传失败", e);
            return ApiResponse.error("图片上传失败: " + e.getMessage());
        }
    }
} 