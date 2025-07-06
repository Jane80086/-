package com.cemenghui.system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import com.cemenghui.common.JWTUtil;

//@SpringBootApplication
@MapperScan(basePackages = {
        "com.cemenghui.dao",             // UserDao 所在包
        "com.cemenghui.system.repository" // EnterpriseMapper 所在包
})
@SpringBootApplication(scanBasePackages = "com.cemenghui") // 扫描整个项目根包
public class SystemApplication implements CommandLineRunner {

    @Autowired
    private JWTUtil jwtUtil;

    public static void main(String[] args) {
        SpringApplication.run(SystemApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // 初始化JWT密钥缓存
        // jwtUtil.initStaticKey();
    }
} 