package com.cemenghui.course.config;

import io.jsonwebtoken.Claims;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 测试专用的JWTUtil Mock实现
 */
@Component
@Primary
public class TestJWTUtil {
    
    public String generateToken(Long userId, String account, List<String> roles) {
        return "test-token-" + userId + "-" + account;
    }
    
    public String generateToken(Map<String, Object> claims, String subject) {
        return "test-token-" + subject;
    }
    
    public String generateRefreshToken(String account) {
        return "test-refresh-token-" + account;
    }
    
    public boolean validateToken(String token) {
        return token != null && token.startsWith("test-token");
    }
    
    public String getAccountFromToken(String token) {
        return "test-account";
    }
    
    public String getUsernameFromToken(String token) {
        return "test-username";
    }
    
    public Long getUserIdFromToken(String token) {
        return 1L;
    }
    
    public Claims getAllClaimsFromToken(String token) {
        return new TestClaims();
    }
    
    /**
     * 测试专用的Claims实现
     */
    private static class TestClaims extends HashMap<String, Object> implements Claims {
        
        public TestClaims() {
            put("account", "test-account");
            put("userId", 1L);
        }
        
        @Override
        public String getIssuer() {
            return "test-issuer";
        }
        
        @Override
        public Claims setIssuer(String s) {
            return this;
        }
        
        @Override
        public String getSubject() {
            return "test-subject";
        }
        
        @Override
        public Claims setSubject(String s) {
            return this;
        }
        
        @Override
        public String getAudience() {
            return "test-audience";
        }
        
        @Override
        public Claims setAudience(String s) {
            return this;
        }
        
        @Override
        public java.util.Date getExpiration() {
            return new java.util.Date(System.currentTimeMillis() + 3600000);
        }
        
        @Override
        public Claims setExpiration(java.util.Date date) {
            return this;
        }
        
        @Override
        public java.util.Date getNotBefore() {
            return new java.util.Date();
        }
        
        @Override
        public Claims setNotBefore(java.util.Date date) {
            return this;
        }
        
        @Override
        public java.util.Date getIssuedAt() {
            return new java.util.Date();
        }
        
        @Override
        public Claims setIssuedAt(java.util.Date date) {
            return this;
        }
        
        @Override
        public String getId() {
            return "test-id";
        }
        
        @Override
        public Claims setId(String s) {
            return this;
        }
        
        @Override
        public <T> T get(String s, Class<T> aClass) {
            return (T) super.get(s);
        }
        
        @Override
        public Claims put(String s, Object o) {
            super.put(s, o);
            return this;
        }
    }
} 