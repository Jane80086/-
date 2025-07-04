package com.cemenghui.course.entity;

import java.io.Serializable;

/**
 * 课程数据传输对象
 */
public class CourseDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long courseId;
    private String courseTitle;
    private String description;
    private String coverUrl;
    private String videpUrl;
    private CourseStatus status;
    private String createdByName;

    // 构造方法、getter/setter 可根据需要生成
}
