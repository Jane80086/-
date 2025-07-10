package com.cemenghui.system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import com.cemenghui.common.JWTUtil;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.cemenghui.system", "com.cemenghui.common"})
@MapperScan("com.cemenghui.system.repository")
public class SystemApplication implements CommandLineRunner {

    @Autowired
    private JWTUtil jwtUtil;

    public static void main(String[] args) {
        SpringApplication.run(SystemApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // jwtUtil.initStaticKey(); // 已废弃，无需初始化
    }
} 
