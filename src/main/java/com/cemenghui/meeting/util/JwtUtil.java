package com.cemenghui.meeting.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
public class JwtUtil {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);
    
    // 配置常量
    private static final String SECRET_KEY = "your-secret-key-must-be-at-least-256-bits-long-for-security";
    private static final long EXPIRATION_TIME = TimeUnit.HOURS.toMillis(24); // 24小时
    private static final long REFRESH_THRESHOLD = TimeUnit.MINUTES.toMillis(30); // 30分钟内可刷新
    
    // 声明常量
    private static final String ISSUER = "meeting-system";
    private static final String AUDIENCE = "meeting-users";

    private final SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

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
            Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME);

            return Jwts.builder()
                    .setSubject(username)
                    .setIssuer(ISSUER)
                    .setAudience(AUDIENCE)
                    .setIssuedAt(now)
                    .setExpiration(expiryDate)
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();
        } catch (Exception e) {
            logger.error("生成Token失败: {}", username, e);
            throw new RuntimeException("Token生成失败", e);
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
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String username = claims.getSubject();
            if (username == null || username.trim().isEmpty()) {
                throw new IllegalArgumentException("Token中不包含有效的用户名");
            }
            
            return username;
        } catch (Exception e) {
            logger.error("从Token获取用户名失败", e);
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
                logger.warn("Token为空");
                return false;
            }
            
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            
            // 检查Token是否过期
            if (claims.getExpiration().before(new Date())) {
                logger.warn("Token已过期");
                return false;
            }
            
            // 检查发行者
            if (!ISSUER.equals(claims.getIssuer())) {
                logger.warn("Token发行者不匹配: {}", claims.getIssuer());
                return false;
            }
            
            // 检查受众
            if (!AUDIENCE.equals(claims.getAudience())) {
                logger.warn("Token受众不匹配: {}", claims.getAudience());
                return false;
            }
            
            // 检查用户名
            String username = claims.getSubject();
            if (username == null || username.trim().isEmpty()) {
                logger.warn("Token中不包含有效的用户名");
                return false;
            }
            
            return true;
        } catch (Exception e) {
            logger.warn("Token验证失败: {}", e.getMessage());
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
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            
            Date expiration = claims.getExpiration();
            Date now = new Date();
            long timeUntilExpiry = expiration.getTime() - now.getTime();
            
            return timeUntilExpiry < REFRESH_THRESHOLD;
        } catch (Exception e) {
            logger.warn("检查Token刷新状态失败: {}", e.getMessage());
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
            logger.error("刷新Token失败", e);
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
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            
            return claims.getExpiration();
        } catch (Exception e) {
            logger.error("获取Token过期时间失败", e);
            return null;
        }
    }
    
    /**
     * 清理Token（移除Bearer前缀）
     * @param token 原始Token
     * @return 清理后的Token
     */
    public String cleanToken(String token) {
        if (token == null) {
            return null;
        }
        return token.replace("Bearer ", "").trim();
    }
} 