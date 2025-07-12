package com.cemenghui.news.config;



import org.springframework.beans.factory.annotation.Value;

import org.springframework.context.annotation.Bean;

import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity; // 引入这个注解

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.oauth2.jwt.JwtDecoder;

import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter; // 引入

import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter; // 引入

import org.springframework.security.web.SecurityFilterChain;



import javax.crypto.spec.SecretKeySpec;

import java.nio.charset.StandardCharsets;

import java.util.Base64;



@Configuration

@EnableWebSecurity

@EnableGlobalMethodSecurity(prePostEnabled = true) // **启用 @PreAuthorize 注解**

public class SecurityConfig {



    @Value("${jwt.secret-key}")

    private String jwtSecretKeyBase64;



    @Bean

    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http

                .csrf(AbstractHttpConfigurer::disable)

                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(authorize -> authorize

// 允许公共接口访问，例如如果 NewsController 的 /api/news/* 是公共的

// .requestMatchers("/api/news/**").permitAll()

                                .anyRequest().authenticated() // 所有其他请求都需要认证

                )

                .oauth2ResourceServer(oauth2 -> oauth2

                        .jwt(jwt -> jwt

                                .decoder(jwtDecoder())

                                .jwtAuthenticationConverter(jwtAuthenticationConverter()) // **添加此行**

                        )

                );



        return http.build();

    }



    @Bean

    public JwtDecoder jwtDecoder() {

        byte[] decodedKey = Base64.getDecoder().decode(jwtSecretKeyBase64.getBytes(StandardCharsets.UTF_8));

        SecretKeySpec secretKeySpec = new SecretKeySpec(decodedKey, "HS256");

        return NimbusJwtDecoder.withSecretKey(secretKeySpec).build();

    }



// **添加此 Bean 来配置如何从 JWT claims 中提取权限**

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        // 设置从 JWT 的 "scope" claim 中提取权限
        grantedAuthoritiesConverter.setAuthoritiesClaimName("scope");

        // 最终方案：设置为 ""，让它直接匹配你的 "ADMIN"
        // 因为 JWT 中就是 "ADMIN"，而你的 isAdmin() 方法也检查 "ADMIN"
        grantedAuthoritiesConverter.setAuthorityPrefix("");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }

}