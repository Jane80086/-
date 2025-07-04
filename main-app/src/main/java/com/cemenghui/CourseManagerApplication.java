package com.cemenghui;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * 课程管理系统主启动类
 * 聚合启动所有子模块：course、meeting、news、system
 */
@SpringBootApplication
@ComponentScan(basePackages = {
        "com.cemenghui.course",
        "com.cemenghui.meeting",
        "com.cemenghui.news",
        "com.cemenghui.system"
})
public class CourseManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(CourseManagerApplication.class, args);
        System.out.println("=================================");
        System.out.println("课程管理系统启动成功！");
        System.out.println("访问地址: http://localhost:8080");
        System.out.println("=================================");
    }
}
