package com.cemenghui.course.controller;

import com.cemenghui.course.entity.UserCourse;
import com.cemenghui.course.service.UserCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/user-course")
public class UserCourseController {
    @Autowired
    private UserCourseService userCourseService;

    @PostMapping("/purchase")
    public ResponseEntity<?> purchase(@RequestParam Long userId, @RequestParam Long courseId) {
        boolean result = userCourseService.purchaseCourse(userId, courseId);
        return ResponseEntity.ok(result ? "购买成功" : "购买失败");
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(@RequestParam Long userId, @RequestParam Long courseId) {
        boolean result = userCourseService.deletePurchasedCourse(userId, courseId);
        return ResponseEntity.ok(result ? "删除成功" : "删除失败");
    }

    @GetMapping("/list")
    public ResponseEntity<List<UserCourse>> list(@RequestParam Long userId) {
        return ResponseEntity.ok(userCourseService.getPurchasedCourses(userId));
    }
} 