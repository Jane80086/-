package com.cemenghui.system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import com.cemenghui.system.util.JWTUtil;

@SpringBootApplication
@MapperScan("com.cemenghui.system.repository")
public class SystemApplication implements CommandLineRunner {

    @Autowired
    private JWTUtil jwtUtil;

    public static void main(String[] args) {
        SpringApplication.run(SystemApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // 初始化JWT密钥缓存
        jwtUtil.initStaticKey();
    }
} 