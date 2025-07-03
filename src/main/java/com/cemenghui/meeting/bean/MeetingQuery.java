package com.cemenghui.meeting.bean;

import lombok.Data;
import java.time.LocalDate;

@Data
public class MeetingQuery {
    private String meetingName;
    private String creator;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer status;
    private Integer page = 1;
    private Integer size = 10;
} 