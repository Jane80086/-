package com.cemenghui.meeting.bean;

import lombok.Data;

@Data
public class MeetingReviewRequest {
    private Long meetingId; // 对应 meeting_id
    private Integer status; // 1: 通过, 2: 拒绝
    private String reviewComment; // 对应 review_comment
} 