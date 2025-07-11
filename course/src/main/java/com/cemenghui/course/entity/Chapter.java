package com.cemenghui.course.entity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;

@Data
@TableName("chapters")
public class Chapter implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("course_id")
    private Long courseId;
    @TableField("title")
    private String title;
    @TableField("duration")
    private Integer duration; // 单位：分钟
    @TableField("description")
    private String description;
    @TableField("video_url")
    private String videoUrl;
} 