package com.cemenghui.meeting;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(exclude = {
    SecurityAutoConfiguration.class,
    UserDetailsServiceAutoConfiguration.class,
    RedisAutoConfiguration.class,
    JpaRepositoriesAutoConfiguration.class,
    DataSourceAutoConfiguration.class
})
@MapperScan("com.cemenghui.meeting.dao")
@EnableTransactionManagement
@ComponentScan(basePackages = "com.cemenghui.meeting", 
               excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com\\.cemenghui\\.course\\..*"))
public class MeetingApplication {

    public static void main(String[] args) {
        SpringApplication.run(MeetingApplication.class, args);
        System.out.println("会议管理系统启动成功！");
        System.out.println("访问地址: http://localhost:8081");
        System.out.println("API文档: http://localhost:8081/swagger-ui.html");
    }

} 