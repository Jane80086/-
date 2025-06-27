package com.cemenghui.course.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import com.cemenghui.course.shiro.AccountProfile;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    private Key key;

    @Autowired
    private ObjectMapper objectMapper;

    @PostConstruct
    public void init() {
        key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(AccountProfile profile) {
        try {
            String profileJson = objectMapper.writeValueAsString(profile);
            return Jwts.builder()
                    .setSubject(profile.getUsername())
                    .claim("profile", profileJson)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + expiration))
                    .signWith(key)
                    .compact();
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize AccountProfile", e);
        }
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public AccountProfile getProfileFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        String profileJson = claims.get("profile", String.class);
        if (profileJson != null) {
            try {
                return objectMapper.readValue(profileJson, AccountProfile.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to deserialize AccountProfile", e);
            }
        }
        return null;
    }
} 