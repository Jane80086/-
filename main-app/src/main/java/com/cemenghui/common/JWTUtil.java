package com.cemenghui.common;

import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JWTUtil {
    private static final Logger log = LoggerFactory.getLogger(JWTUtil.class);
    private static final String SECRET_KEY = "cemenghui-secret-key";
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24; // 24小时
    private static final long REFRESH_EXPIRATION_TIME = 1000 * 60 * 60 * 24 * 7; // 7天

    public String generateToken(String account) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("account", account);
        return doGenerateToken(claims, account, EXPIRATION_TIME);
    }

    public String generateToken(Map<String, Object> claims, String subject) {
        return doGenerateToken(claims, subject, EXPIRATION_TIME);
    }

    public String generateRefreshToken(String account) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("account", account);
        return doGenerateToken(claims, account, REFRESH_EXPIRATION_TIME);
    }

    private String doGenerateToken(Map<String, Object> claims, String subject, long expiration) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            log.warn("JWT validation failed: {}", e.getMessage());
            return false;
        }
    }

    public String getAccountFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return claims.get("account", String.class);
    }

    public String getUsernameFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return claims.getSubject();
    }

    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }
} 