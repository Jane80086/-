package com.cemenghui.system.util;

import com.cemenghui.system.config.JWTConfig;
import com.cemenghui.common.AdminUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.jsonwebtoken.MalformedJwtException;
import java.util.Base64;
import java.nio.charset.StandardCharsets;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.security.Key;

@Component
public class JWTUtil {

    private static final Logger log = LoggerFactory.getLogger(JWTUtil.class);

    @Resource
    private JWTConfig jwtConfig;

    // 静态密钥缓存，避免重复解码
    private static String cachedSecret;
    private static Key cachedKey;

    public String generateToken(String account, long expireMillis) {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + expireMillis);
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", account);
        claims.put("iat", now);
        claims.put("exp", expireDate);
        Key key = getSecretKey();
        return Jwts.builder()
                .setClaims(claims)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateToken(String account) {
        return generateToken(account, jwtConfig.getAccessExpire());
    }

    public String generateToken(AdminUser admin, long expireMillis) {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + expireMillis);
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", admin.getAccount());
        claims.put("role", admin.getRole());
        claims.put("iat", now);
        claims.put("exp", expireDate);
        Key key = getSecretKey();
        return Jwts.builder()
                .setClaims(claims)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateToken(AdminUser admin) {
        return generateToken(admin, jwtConfig.getAccessExpire());
    }

    public static Claims parseToken(String jwt) {
        try {
            log.debug("原始JWT: {}", jwt);
            jwt = cleanToken(jwt);
            log.debug("清理后JWT: {}", jwt);

            // 解析
            return Jwts.parserBuilder()
                .setSigningKey(getStaticSecretKey())
                .build()
                .parseClaimsJws(jwt)
                .getBody();
        } catch (MalformedJwtException e) {
            log.error("JWT格式错误: {}", e.getMessage());
            if (jwt != null && jwt.contains(".")) {
                String[] parts = jwt.split("\\.");
                if (parts.length >= 2) {
                    log.error("JWT Header (decoded): {}", decodeBase64Url(parts[0]));
                    log.error("JWT Payload (decoded): {}", decodeBase64Url(parts[1]));
                    if (parts.length == 3) {
                        log.error("JWT Signature: {}", parts[2]);
                    }
                }
            }
            throw e;
        } catch (Exception e) {
            log.error("JWT解析失败: {}", e.getMessage(), e);
            throw new RuntimeException("JWT解析失败", e);
        }
    }

    /**
     * 清理JWT令牌中的非法字符
     * @param token 原始令牌
     * @return 清理后的令牌
     */
    public static String cleanToken(String token) {
        if (token == null) return null;
        
        // 移除Bearer前缀
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        // 移除所有空白字符（空格、制表符、换行符等）
        token = token.replaceAll("\\s", "");
        
        // 过滤所有非Base64URL合法字符([A-Za-z0-9-_=])
        token = token.replaceAll("[^A-Za-z0-9\\-_=\\.]", "");
        
        log.debug("Token清理完成: 长度={}", token != null ? token.length() : 0);
        return token;
    }

    /**
     * 解码Base64URL字符串
     */
    private static String decodeBase64Url(String base64Url) {
        try {
            if (base64Url == null || base64Url.isEmpty()) {
                return "空字符串";
            }
            
            // Base64URL转Base64
            base64Url = base64Url.replace('-', '+').replace('_', '/');
            
            // 添加填充
            switch (base64Url.length() % 4) {
                case 2: base64Url += "=="; break;
                case 3: base64Url += "="; break;
            }
            
            return new String(Base64.getDecoder().decode(base64Url), StandardCharsets.UTF_8);
        } catch (Exception e) {
            return "解码失败: " + e.getMessage();
        }
    }

    /**
     * 获取密钥，使用缓存避免重复解码
     */
    private Key getSecretKey() {
        String secret = jwtConfig.getSecret();
        
        // 检查缓存
        if (cachedSecret != null && cachedSecret.equals(secret) && cachedKey != null) {
            return cachedKey;
        }
        
        try {
            // 清理密钥字符串
            secret = secret.trim().replaceAll("\\s", "");
            log.debug("使用密钥: {}", secret.substring(0, Math.min(10, secret.length())) + "...");
            
            Key key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
            
            // 更新缓存
            cachedSecret = secret;
            cachedKey = key;
            
            return key;
        } catch (Exception e) {
            log.error("密钥解码失败: {}", e.getMessage(), e);
            throw new RuntimeException("密钥配置错误", e);
        }
    }

    /**
     * 静态方法获取密钥（用于parseToken）
     * 注意：这个方法需要在Spring容器初始化后调用
     */
    private static Key getStaticSecretKey() {
        if (cachedKey != null) {
            return cachedKey;
        }
        
        // 如果缓存为空，尝试从配置中获取
        try {
            // 这里使用一个默认的密钥，实际项目中应该从配置中获取
            String defaultSecret = "ZmQ0ZGI5NjQ0MDQwY2I4MjMxY2Y3ZmI3MjdhN2ZmMjNhODViOTg1ZGE0NTBjMGM4NDA5NzYxMjdjOWMwYWRmZTBlZjlhNGY3ZTg4Y2U3YTE1ODVkZDU5Y2Y3OGYwZWE1NzUzNWQ2YjFjZDc0NGMxZWU2MmQ3MjY1NzJmNTE0MzI=";
            return Keys.hmacShaKeyFor(Decoders.BASE64.decode(defaultSecret));
        } catch (Exception e) {
            log.error("静态方法获取密钥失败: {}", e.getMessage(), e);
            throw new RuntimeException("密钥获取失败", e);
        }
    }

    /**
     * 初始化静态密钥缓存
     * 这个方法应该在Spring容器初始化后调用
     */
    public void initStaticKey() {
        try {
            String secret = jwtConfig.getSecret();
            secret = secret.trim().replaceAll("\\s", "");
            log.info("初始化JWT密钥缓存，密钥长度: {}", secret.length());
            
            Key key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
            cachedSecret = secret;
            cachedKey = key;
            
            log.info("JWT密钥缓存初始化成功");
        } catch (Exception e) {
            log.error("JWT密钥缓存初始化失败: {}", e.getMessage(), e);
            throw new RuntimeException("JWT密钥初始化失败", e);
        }
    }

    public String getAccountFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.getSubject(); // "sub" 字段
    }

    public String getRoleFromToken(String token) {
        Claims claims = parseToken(token);
        Object role = claims.get("role");
        return role != null ? role.toString() : null;
    }

    /**
     * 验证JWT令牌格式
     */
    public static boolean isValidJWTFormat(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }
        
        token = cleanToken(token);
        String[] parts = token.split("\\.");
        
        // JWT应该有三部分：header.payload.signature
        if (parts.length != 3) {
            log.debug("JWT格式无效：期望3部分，实际{}部分", parts.length);
            return false;
        }
        
        // 检查每部分是否只包含Base64URL字符
        for (String part : parts) {
            if (!part.matches("[A-Za-z0-9\\-_=]+")) {
                log.debug("JWT部分包含非法字符: {}", part);
                return false;
            }
        }
        
        return true;
    }

    /**
     * 从token中获取userId（优先userId字段，否则sub/account）
     */
    public String getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        Object userId = claims.get("userId");
        if (userId != null) {
            return userId.toString();
        }
        // 兼容sub/account
        Object sub = claims.getSubject();
        if (sub != null) {
            return sub.toString();
        }
        Object account = claims.get("account");
        if (account != null) {
            return account.toString();
        }
        return null;
    }
}