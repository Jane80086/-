package com.cemenghui.common;

import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JWTUtil {
    private static final Logger log = LoggerFactory.getLogger(JWTUtil.class);
    private static final String BASE64_SECRET_KEY = "3lO/GEOWLL88qmv3i97FrRdrYltYOSFq016I7+Uecgc=";
    private static final Key SIGNING_KEY = new SecretKeySpec(
            Base64.getDecoder().decode(BASE64_SECRET_KEY),
            SignatureAlgorithm.HS256.getJcaName()
    );
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24; // 24小时
    private static final long REFRESH_EXPIRATION_TIME = 1000 * 60 * 60 * 24 * 7; // 7天

    // ==== 核心修改点1：在 generateToken 方法中添加 Long userId 参数，并将其放入 claims ====
    public String generateToken(Long userId, String account, List<String> roles) { // **添加 Long userId 参数**
        Map<String, Object> claims = new HashMap<>();
        claims.put("account", account);
        claims.put("userId", userId); // **将用户 ID 加入 Claims，键名为 "userId"**
        claims.put("scope", roles.stream().map(role -> "ROLE_" + role.toUpperCase()).collect(Collectors.joining(" ")));
        return doGenerateToken(claims, account, EXPIRATION_TIME);
    }

    public String generateToken(Map<String, Object> claims, String subject) {
        // 如果你的业务逻辑会直接调用这个方法，需要确保传入的 claims 中也包含 userId
        // 否则，建议只使用上面那个带 userId, account, roles 参数的 generateToken 方法
        return doGenerateToken(claims, subject, EXPIRATION_TIME);
    }

    public String generateRefreshToken(String account) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("account", account);
        // 刷新 token 通常不需要包含 userId 或角色，保持不变
        return doGenerateToken(claims, account, REFRESH_EXPIRATION_TIME);
    }

    private String doGenerateToken(Map<String, Object> claims, String subject, long expiration) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SIGNING_KEY)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(SIGNING_KEY).build().parseClaimsJws(token);
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

    // 可以在 JWTUtil 中添加一个获取 userId 的方法，方便使用
    public Long getUserIdFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        // 从 claims 中直接获取 "userId"
        return claims.get("userId", Long.class);
    }

    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(SIGNING_KEY).build().parseClaimsJws(token).getBody();
    }
}