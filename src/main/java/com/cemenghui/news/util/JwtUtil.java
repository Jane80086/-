package com.cemenghui.news.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys; // 注意：jjwt 0.11.x 版本可能没有 Keys.hmacShaKeyFor，需要手动生成 SecretKey
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec; // 导入此包
import java.util.Base64; // 导入此包
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * JWT工具类，用于生成、解析和验证JWT令牌。
 */
@Component
public class JwtUtil {

    // 从application.yml中读取JWT密钥
    @Value("${jwt.secret}")
    private String secret;

    // 从application.yml中读取JWT过期时间（毫秒）
    @Value("${jwt.expiration}")
    private Long expiration;

    /**
     * 获取用于签名的SecretKey。
     * @return SecretKey对象
     */
    private SecretKey getSigningKey() {
        // 对于jjwt 0.11.x版本，Keys.hmacShaKeyFor可能不可用。
        // 可以手动通过Base64解码密钥字符串来创建SecretKey。
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        return new SecretKeySpec(keyBytes, SignatureAlgorithm.HS256.getJcaName());
    }

    /**
     * 从JWT令牌中提取用户名（Subject）。
     * @param token JWT令牌
     * @return 用户名
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * 从JWT令牌中提取过期日期。
     * @param token JWT令牌
     * @return 过期日期
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * 从JWT令牌中提取特定声明。
     * @param token JWT令牌
     * @param claimsResolver 用于解析声明的函数
     * @param <T> 声明的类型
     * @return 提取的声明
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * 从JWT令牌中提取所有声明。
     * @param token JWT令牌
     * @return 所有声明的Claims对象
     */
    private Claims extractAllClaims(String token) {
        // 使用解析器构建器解析JWT，并验证签名
        return Jwts.parser() // jjwt 0.11.x 使用 parser()
                .setSigningKey(getSigningKey())
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 判断JWT令牌是否过期。
     * @param token JWT令牌
     * @return 如果令牌已过期则返回true，否则返回false
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * 根据UserDetails生成JWT令牌。
     * @param userDetails 用户详情对象
     * @return 生成的JWT令牌字符串
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        // 将用户角色信息放入claims，以便后续验证
        claims.put("roles", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority) // 使用getAuthority()获取权限字符串
                .collect(Collectors.toList()));
        return createToken(claims, userDetails.getUsername());
    }

    /**
     * 创建JWT令牌。
     * @param claims 声明（Payload）
     * @param subject 主题（通常是用户名）
     * @return JWT令牌字符串
     */
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims) // 设置声明
                .setSubject(subject) // 设置主题
                .setIssuedAt(new Date(System.currentTimeMillis())) // 设置签发时间
                .setExpiration(new Date(System.currentTimeMillis() + expiration)) // 设置过期时间
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // 使用密钥和算法签名
                .compact(); // 压缩生成JWT字符串
    }

    /**
     * 验证JWT令牌是否有效。
     * @param token JWT令牌
     * @param userDetails 用户详情对象
     * @return 如果令牌有效且未过期则返回true，否则返回false
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
