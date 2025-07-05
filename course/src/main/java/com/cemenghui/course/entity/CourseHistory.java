package com.cemenghui.course.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 课程观看历史实体
 * 保留原有功能，但映射到新的history_records表
 */
@TableName("history_records")
@Data
public class CourseHistory implements Serializable {
   private static final long serialVersionUID = 1L;

   @TableId(type = IdType.AUTO)
   private Long id;
   
   @TableField("resource_type")
   private String resourceType = "COURSE";
   
   @TableField("resource_id")
   private Long courseId;
   
   @TableField("action")
   private String action = "VIEW";
   
   @TableField("user_id")
   private Long userId;
   
   @TableField("record_time")
   private LocalDateTime viewedAt;

   public Long getCourseHistoryId(){
       return id;
   }

   public void setCourseHistoryId(Long courseHistoryId){
       this.id = courseHistoryId;
   }

   public Long getUserId(){
       return userId;
   }

   public void setUserId(Long userId){
       this.userId = userId;
   }

   public LocalDateTime getViewedAt(){
       return viewedAt;
   }

   public void setViewedAt(LocalDateTime time){
       this.viewedAt = time;
   }

   /**
    * @param userId
    * @param courseId
    */
   protected void recordCourseHistory(Long userId, Long courseId){
       this.userId = userId;
       this.courseId = courseId;
       this.resourceType = "COURSE";
       this.action = "VIEW";
       this.viewedAt = LocalDateTime.now();
   }

   /**
    * @param userId 用户ID
    */
   protected void cleanHistory(Long userId, Long courseId){}
}
