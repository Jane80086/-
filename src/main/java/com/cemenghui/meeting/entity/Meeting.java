package com.cemenghui.meeting.entity;

import java.time.LocalDateTime;

public class Meeting {
    private Long id;
    private String meetingName; // 对应 meeting_name
    private LocalDateTime startTime; // 对应 start_time
    private LocalDateTime endTime; // 对应 end_time
    private String creator; // 对应 creator
    private String meetingContent; // 对应 meeting_content
    private Integer status; // 0: 待审核, 1: 已通过, 2: 已拒绝, 3: 已删除
    private String reviewer; // 审核人
    private LocalDateTime reviewTime; // 审核时间，对应 review_time
    private String reviewComment; // 审核意见，对应 review_comment
    private LocalDateTime createTime; // 对应 create_time
    private LocalDateTime updateTime; // 对应 update_time
    private String imageUrl; // 会议图片URL

    // 构造函数
    public Meeting() {}

    public Meeting(String meetingName, LocalDateTime startTime, LocalDateTime endTime, String creator, String meetingContent) {
        this.meetingName = meetingName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.creator = creator;
        this.meetingContent = meetingContent;
        this.status = 0; // 默认待审核
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }

    // Getter和Setter方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMeetingName() {
        return meetingName;
    }

    public void setMeetingName(String meetingName) {
        this.meetingName = meetingName;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getMeetingContent() {
        return meetingContent;
    }

    public void setMeetingContent(String meetingContent) {
        this.meetingContent = meetingContent;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getReviewer() {
        return reviewer;
    }

    public void setReviewer(String reviewer) {
        this.reviewer = reviewer;
    }

    public LocalDateTime getReviewTime() {
        return reviewTime;
    }

    public void setReviewTime(LocalDateTime reviewTime) {
        this.reviewTime = reviewTime;
    }

    public String getReviewComment() {
        return reviewComment;
    }

    public void setReviewComment(String reviewComment) {
        this.reviewComment = reviewComment;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public String toString() {
        return "Meeting{" +
                "id=" + id +
                ", meetingName='" + meetingName + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", creator='" + creator + '\'' +
                ", meetingContent='" + meetingContent + '\'' +
                ", status=" + status +
                ", reviewer='" + reviewer + '\'' +
                ", reviewTime=" + reviewTime +
                ", reviewComment='" + reviewComment + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
} 