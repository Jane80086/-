package com.cemenghui.course.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 课程观看历史实体
 */
@TableName("course_history")
@Data
public class CourseHistory implements Serializable {
   private static final long serialVersionUID = 1L;

   @TableId(value = "course_history_id", type = IdType.AUTO)
   private Long courseHistoryId;
   
   @TableField("course_id")
   private Long courseId;
   
   @TableField("user_id")
   private Long userId;
   
   @TableField(value = "viewed_at", fill = FieldFill.INSERT)
   private LocalDateTime viewedAt;

   @TableLogic
   @TableField("deleted")
   private Integer deleted = 0;

   public Long getCourseHistoryId(){
       return courseHistoryId;
   }

   public void setCourseHistoryId(Long courseHistoryId){
       this.courseHistoryId = courseHistoryId;
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
@param userId
 @param courseId
 */
protected void recordCourseHistory(Long userId,Long courseId){

}

/**
 *@param userId 用户ID
 **/
protected void cleanHistory(Long userId,Long courseId){}
}
