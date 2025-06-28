package com.cemenghui.meeting.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;

    @Bean
    @Primary
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        
        // 基本配置
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName(driverClassName);
        
        // 连接池配置
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(5);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        config.setLeakDetectionThreshold(60000);
        
        // 连接测试
        config.setConnectionTestQuery("SELECT 1");
        config.setValidationTimeout(5000);
        
        // 其他配置
        config.setAutoCommit(true);
        config.setPoolName("MeetingHikariCP");
        
        // 连接属性
        config.addDataSourceProperty("characterEncoding", "UTF-8");
        config.addDataSourceProperty("useUnicode", "true");
        config.addDataSourceProperty("serverTimezone", "Asia/Shanghai");
        config.addDataSourceProperty("useSSL", "false");
        config.addDataSourceProperty("allowPublicKeyRetrieval", "true");
        
        return new HikariDataSource(config);
    }
} 