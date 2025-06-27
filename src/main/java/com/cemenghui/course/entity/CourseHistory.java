package com.cemenghui.course.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 课程观看历史实体
 */
@Entity
@Table(name = "course_history")
public class CourseHistory implements Serializable {
   private static final long serialVersionUID = 1L;

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long courseHistoryId;
   private Long courseId;
   private Long userId;
   private LocalDateTime viewedAt=LocalDateTime.now();

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
