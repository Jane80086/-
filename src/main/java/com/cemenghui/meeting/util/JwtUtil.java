package com.cemenghui.meeting.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class JwtUtil {
    
    @Value("${app.jwt.secret}")
    private String secretKey;
    
    @Value("${app.jwt.expiration}")
    private long expirationTime;
    
    @Value("${app.jwt.refresh-expiration}")
    private long refreshExpirationTime;
    
    // 声明常量
    private static final String ISSUER = "meeting-system";
    private static final String AUDIENCE = "meeting-users";
    private static final long REFRESH_THRESHOLD = TimeUnit.MINUTES.toMillis(30); // 30分钟内可刷新

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    /**
     * 生成JWT Token
     * @param username 用户名
     * @return JWT Token字符串
     */
    public String generateToken(String username) {
        try {
            if (username == null || username.trim().isEmpty()) {
                throw new IllegalArgumentException("用户名不能为空");
            }
            
            Date now = new Date();
            Date expiryDate = new Date(now.getTime() + expirationTime);

            return Jwts.builder()
                    .setSubject(username)
                    .setIssuer(ISSUER)
                    .setAudience(AUDIENCE)
                    .setIssuedAt(now)
                    .setExpiration(expiryDate)
                    .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                    .compact();
        } catch (Exception e) {
            log.error("生成Token失败: {}", username, e);
            throw new RuntimeException("Token生成失败", e);
        }
    }

    /**
     * 生成刷新Token
     * @param username 用户名
     * @return 刷新Token字符串
     */
    public String generateRefreshToken(String username) {
        try {
            if (username == null || username.trim().isEmpty()) {
                throw new IllegalArgumentException("用户名不能为空");
            }
            
            Date now = new Date();
            Date expiryDate = new Date(now.getTime() + refreshExpirationTime);

            return Jwts.builder()
                    .setSubject(username)
                    .setIssuer(ISSUER)
                    .setAudience(AUDIENCE)
                    .setIssuedAt(now)
                    .setExpiration(expiryDate)
                    .claim("type", "refresh")
                    .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                    .compact();
        } catch (Exception e) {
            log.error("生成刷新Token失败: {}", username, e);
            throw new RuntimeException("刷新Token生成失败", e);
        }
    }

    /**
     * 从Token中获取用户名
     * @param token JWT Token
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        try {
            if (token == null || token.trim().isEmpty()) {
                throw new IllegalArgumentException("Token不能为空");
            }
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            String username = claims.getSubject();
            if (username == null || username.trim().isEmpty()) {
                throw new IllegalArgumentException("Token中不包含有效的用户名");
            }
            return username;
        } catch (Exception e) {
            log.error("从Token获取用户名失败, token={}", token, e);
            throw new IllegalArgumentException("Token解析失败: " + e.getMessage());
        }
    }

    /**
     * 验证Token是否有效
     * @param token JWT Token
     * @return 是否有效
     */
    public boolean validateToken(String token) {
        try {
            if (token == null || token.trim().isEmpty()) {
                log.warn("Token为空");
                return false;
            }
            
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            
            // 检查Token是否过期
            if (claims.getExpiration().before(new Date())) {
                log.warn("Token已过期");
                return false;
            }
            
            // 检查发行者
            if (!ISSUER.equals(claims.getIssuer())) {
                log.warn("Token发行者不匹配: {}", claims.getIssuer());
                return false;
            }
            
            // 检查受众
            if (!AUDIENCE.equals(claims.getAudience())) {
                log.warn("Token受众不匹配: {}", claims.getAudience());
                return false;
            }
            
            // 检查用户名
            String username = claims.getSubject();
            if (username == null || username.trim().isEmpty()) {
                log.warn("Token中不包含有效的用户名");
                return false;
            }
            
            return true;
        } catch (Exception e) {
            log.warn("Token验证失败: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 检查Token是否需要刷新
     * @param token JWT Token
     * @return 是否需要刷新
     */
    public boolean needsRefresh(String token) {
        try {
            if (!validateToken(token)) {
                return false;
            }
            
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            
            Date expiration = claims.getExpiration();
            Date now = new Date();
            long timeUntilExpiry = expiration.getTime() - now.getTime();
            
            return timeUntilExpiry < REFRESH_THRESHOLD;
        } catch (Exception e) {
            log.warn("检查Token刷新状态失败: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 刷新Token
     * @param token 原Token
     * @return 新的Token
     */
    public String refreshToken(String token) {
        try {
            if (!validateToken(token)) {
                throw new IllegalArgumentException("原Token无效，无法刷新");
            }
            
            String username = getUsernameFromToken(token);
            return generateToken(username);
        } catch (Exception e) {
            log.error("刷新Token失败", e);
            throw new RuntimeException("Token刷新失败", e);
        }
    }
    
    /**
     * 获取Token的过期时间
     * @param token JWT Token
     * @return 过期时间
     */
    public Date getExpirationDate(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            
            return claims.getExpiration();
        } catch (Exception e) {
            log.error("获取Token过期时间失败", e);
            return null;
        }
    }
    
    /**
     * 清理Token字符串（移除Bearer前缀等）
     * @param token 原始Token字符串
     * @return 清理后的Token
     */
    public String cleanToken(String token) {
        if (token == null) {
            return null;
        }
        
        token = token.trim();
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        return token;
    }
} 