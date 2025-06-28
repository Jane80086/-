package com.cemenghui.meeting.entity;

public class MeetingReviewRequest {
    private Long meetingId;
    private Integer status; // 1: 通过, 2: 拒绝
    private String reviewComment;

    public MeetingReviewRequest() {}

    public MeetingReviewRequest(Long meetingId, Integer status, String reviewComment) {
        this.meetingId = meetingId;
        this.status = status;
        this.reviewComment = reviewComment;
    }

    public Long getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(Long meetingId) {
        this.meetingId = meetingId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getReviewComment() {
        return reviewComment;
    }

    public void setReviewComment(String reviewComment) {
        this.reviewComment = reviewComment;
    }

    @Override
    public String toString() {
        return "MeetingReviewRequest{" +
                "meetingId=" + meetingId +
                ", status=" + status +
                ", reviewComment='" + reviewComment + '\'' +
                '}';
    }
} 