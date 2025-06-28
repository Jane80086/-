package com.cemenghui.meeting.entity;

import java.time.LocalDateTime;

public class MeetingReviewRecord {
    private Long id;
    private Long meetingId;
    private String meetingName;
    private String creator;
    private String reviewer;
    private Integer status; // 1: 通过, 2: 拒绝
    private String reviewComment;
    private LocalDateTime reviewTime;

    public MeetingReviewRecord() {}

    public MeetingReviewRecord(Long meetingId, String meetingName, String creator, String reviewer, Integer status, String reviewComment) {
        this.meetingId = meetingId;
        this.meetingName = meetingName;
        this.creator = creator;
        this.reviewer = reviewer;
        this.status = status;
        this.reviewComment = reviewComment;
        this.reviewTime = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(Long meetingId) {
        this.meetingId = meetingId;
    }

    public String getMeetingName() {
        return meetingName;
    }

    public void setMeetingName(String meetingName) {
        this.meetingName = meetingName;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getReviewer() {
        return reviewer;
    }

    public void setReviewer(String reviewer) {
        this.reviewer = reviewer;
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

    public LocalDateTime getReviewTime() {
        return reviewTime;
    }

    public void setReviewTime(LocalDateTime reviewTime) {
        this.reviewTime = reviewTime;
    }

    @Override
    public String toString() {
        return "MeetingReviewRecord{" +
                "id=" + id +
                ", meetingId=" + meetingId +
                ", meetingName='" + meetingName + '\'' +
                ", creator='" + creator + '\'' +
                ", reviewer='" + reviewer + '\'' +
                ", status=" + status +
                ", reviewComment='" + reviewComment + '\'' +
                ", reviewTime=" + reviewTime +
                '}';
    }
} 