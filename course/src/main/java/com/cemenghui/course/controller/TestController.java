package com.cemenghui.course.controller;

import com.cemenghui.course.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "*")
public class TestController {

    @GetMapping("/hello")
    public Result hello() {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("message", "Hello from backend!");
            data.put("timestamp", System.currentTimeMillis());
            return Result.success("测试成功", data);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("测试失败: " + e.getMessage());
        }
    }

    @GetMapping("/courses")
    public Result testCourses() {
        try {
            // 返回简化的课程数据
            Map<String, Object> course1 = new HashMap<>();
            course1.put("id", 1);
            course1.put("title", "Java基础教程");
            course1.put("description", "Java编程基础入门课程");
            course1.put("duration", 120);
            course1.put("price", 0.0);
            course1.put("category", "编程开发");

            Map<String, Object> course2 = new HashMap<>();
            course2.put("id", 2);
            course2.put("title", "Spring Boot实战");
            course2.put("description", "Spring Boot框架开发实战课程");
            course2.put("duration", 180);
            course2.put("price", 99.0);
            course2.put("category", "框架开发");

            return Result.success("获取课程列表成功", new Object[]{course1, course2});
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("获取课程列表失败: " + e.getMessage());
        }
    }
} 