package com.cemenghui.course.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoggingConfig {

    @Bean
    public Logger courseLogger() {
        return LoggerFactory.getLogger("com.cemenghui.course");
    }
} 