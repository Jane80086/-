package com.cemenghui.meeting.entity;

import java.time.LocalDate;

public class MeetingQuery {
    private Integer page;
    private Integer size;
    private String meetingName;
    private String creator;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer status;

    public MeetingQuery() {}

    public MeetingQuery(Integer page, Integer size) {
        this.page = page;
        this.size = size;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
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

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "MeetingQuery{" +
                "page=" + page +
                ", size=" + size +
                ", meetingName='" + meetingName + '\'' +
                ", creator='" + creator + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", status=" + status +
                '}';
    }
} 