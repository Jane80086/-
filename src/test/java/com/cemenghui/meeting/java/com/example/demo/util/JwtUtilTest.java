package com.cemenghui.meeting.java.com.example.demo.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.cemenghui.meeting.util.JwtUtil;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JwtUtil 单元测试类
 * 测试JWT token的生成和验证功能
 */
@DisplayName("JWT工具测试")
public class JwtUtilTest {

    private JwtUtil jwtUtil;
    private static final String TEST_SECRET = "test-secret-key-for-testing-only-not-for-production-use";
    private static final String TEST_USERNAME = "testuser";

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        // 设置测试用的密钥
        ReflectionTestUtils.setField(jwtUtil, "secretKey", TEST_SECRET);
        ReflectionTestUtils.setField(jwtUtil, "expirationTime", 3600000L); // 1小时
    }

    @Test
    @DisplayName("测试生成Token - 正常情况")
    public void testGenerateToken_Success() {
        // 执行测试
        String token = jwtUtil.generateToken(TEST_USERNAME);

        // 验证结果
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.length() > 50); // JWT token通常很长
    }

    @Test
    @DisplayName("测试生成Token - 用户名为空")
    public void testGenerateToken_EmptyUsername() {
        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            jwtUtil.generateToken("");
        });
        assertTrue(exception.getMessage().contains("Token生成失败"));
    }

    @Test
    @DisplayName("测试生成Token - 用户名为null")
    public void testGenerateToken_NullUsername() {
        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            jwtUtil.generateToken(null);
        });
        assertTrue(exception.getMessage().contains("Token生成失败"));
    }

    @Test
    @DisplayName("测试验证Token - 有效Token")
    public void testValidateToken_ValidToken() {
        // 生成token
        String token = jwtUtil.generateToken(TEST_USERNAME);

        // 执行测试
        boolean result = jwtUtil.validateToken(token);

        // 验证结果
        assertTrue(result);
    }

    @Test
    @DisplayName("测试验证Token - 无效Token")
    public void testValidateToken_InvalidToken() {
        // 准备无效token
        String invalidToken = "invalid.token.here";

        // 执行测试
        boolean result = jwtUtil.validateToken(invalidToken);

        // 验证结果
        assertFalse(result);
    }

    @Test
    @DisplayName("测试验证Token - 空Token")
    public void testValidateToken_EmptyToken() {
        // 执行测试
        boolean result = jwtUtil.validateToken("");

        // 验证结果
        assertFalse(result);
    }

    @Test
    @DisplayName("测试验证Token - null Token")
    public void testValidateToken_NullToken() {
        // 执行测试
        boolean result = jwtUtil.validateToken(null);

        // 验证结果
        assertFalse(result);
    }

    @Test
    @DisplayName("测试从Token获取用户名 - 有效Token")
    public void testGetUsernameFromToken_ValidToken() {
        // 生成token
        String token = jwtUtil.generateToken(TEST_USERNAME);

        // 执行测试
        String username = jwtUtil.getUsernameFromToken(token);

        // 验证结果
        assertNotNull(username);
        assertEquals(TEST_USERNAME, username);
    }

    @Test
    @DisplayName("测试从Token获取用户名 - 无效Token")
    public void testGetUsernameFromToken_InvalidToken() {
        // 准备无效token
        String invalidToken = "invalid.token.here";

        // 执行测试并验证异常
        assertThrows(Exception.class, () -> {
            jwtUtil.getUsernameFromToken(invalidToken);
        });
    }

    @Test
    @DisplayName("测试从Token获取用户名 - 空Token")
    public void testGetUsernameFromToken_EmptyToken() {
        // 执行测试并验证异常
        assertThrows(Exception.class, () -> {
            jwtUtil.getUsernameFromToken("");
        });
    }

    @Test
    @DisplayName("测试从Token获取用户名 - null Token")
    public void testGetUsernameFromToken_NullToken() {
        // 执行测试并验证异常
        assertThrows(Exception.class, () -> {
            jwtUtil.getUsernameFromToken(null);
        });
    }

    @Test
    @DisplayName("测试Token过期时间")
    public void testTokenExpiration() {
        // 生成token
        String token = jwtUtil.generateToken(TEST_USERNAME);

        // 验证token有效
        assertTrue(jwtUtil.validateToken(token));

        // 获取用户名
        String username = jwtUtil.getUsernameFromToken(token);
        assertEquals(TEST_USERNAME, username);
    }

    @Test
    @DisplayName("测试不同用户名的Token")
    public void testDifferentUsernames() {
        // 生成不同用户的token
        String token1 = jwtUtil.generateToken("user1");
        String token2 = jwtUtil.generateToken("user2");

        // 验证token
        assertTrue(jwtUtil.validateToken(token1));
        assertTrue(jwtUtil.validateToken(token2));

        // 验证用户名
        assertEquals("user1", jwtUtil.getUsernameFromToken(token1));
        assertEquals("user2", jwtUtil.getUsernameFromToken(token2));
    }

    @Test
    @DisplayName("测试Token格式")
    public void testTokenFormat() {
        // 生成token
        String token = jwtUtil.generateToken(TEST_USERNAME);

        // 验证token格式（JWT通常由三部分组成，用点分隔）
        String[] parts = token.split("\\.");
        assertEquals(3, parts.length, "JWT token应该有三部分");

        // 验证每部分都不为空
        for (String part : parts) {
            assertFalse(part.isEmpty());
        }
    }

    @Test
    @DisplayName("测试Token唯一性")
    public void testTokenUniqueness() {
        // 生成多个相同用户的token
        String token1 = jwtUtil.generateToken(TEST_USERNAME);
        
        // 等待更长时间确保时间戳不同
        try {
            Thread.sleep(1000); // 等待1秒确保时间戳不同
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        String token2 = jwtUtil.generateToken(TEST_USERNAME);

        // 验证token不同（因为时间戳不同）
        assertNotEquals(token1, token2);

        // 验证两个token都有效
        assertTrue(jwtUtil.validateToken(token1));
        assertTrue(jwtUtil.validateToken(token2));

        // 验证两个token都返回相同的用户名
        assertEquals(TEST_USERNAME, jwtUtil.getUsernameFromToken(token1));
        assertEquals(TEST_USERNAME, jwtUtil.getUsernameFromToken(token2));
        
        // 额外验证：检查两个token的过期时间是否不同
        Date expiration1 = jwtUtil.getExpirationDate(token1);
        Date expiration2 = jwtUtil.getExpirationDate(token2);
        assertNotEquals(expiration1, expiration2);
    }

    @Test
    @DisplayName("测试特殊字符用户名")
    public void testSpecialCharacterUsername() {
        // 测试包含特殊字符的用户名
        String specialUsername = "user@123_456";
        String token = jwtUtil.generateToken(specialUsername);

        // 验证token
        assertTrue(jwtUtil.validateToken(token));

        // 验证用户名
        assertEquals(specialUsername, jwtUtil.getUsernameFromToken(token));
    }

    @Test
    @DisplayName("测试中文字符用户名")
    public void testChineseUsername() {
        // 测试中文字符用户名
        String chineseUsername = "测试用户";
        String token = jwtUtil.generateToken(chineseUsername);

        // 验证token
        assertTrue(jwtUtil.validateToken(token));

        // 验证用户名
        assertEquals(chineseUsername, jwtUtil.getUsernameFromToken(token));
    }

    @Test
    @DisplayName("测试长用户名")
    public void testLongUsername() {
        // 测试长用户名
        StringBuilder longUsername = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            longUsername.append("a");
        }
        String token = jwtUtil.generateToken(longUsername.toString());

        // 验证token
        assertTrue(jwtUtil.validateToken(token));

        // 验证用户名
        assertEquals(longUsername.toString(), jwtUtil.getUsernameFromToken(token));
    }

    @Test
    @DisplayName("测试Token处理 - 带Bearer前缀")
    public void testTokenWithBearerPrefix() {
        // 生成token
        String originalToken = jwtUtil.generateToken(TEST_USERNAME);
        String tokenWithBearer = "Bearer " + originalToken;

        // 验证原始token有效
        assertTrue(jwtUtil.validateToken(originalToken));
        assertEquals(TEST_USERNAME, jwtUtil.getUsernameFromToken(originalToken));
        
        // 验证带Bearer前缀的token需要先清理
        String cleanedToken = jwtUtil.cleanToken(tokenWithBearer);
        assertTrue(jwtUtil.validateToken(cleanedToken));
        assertEquals(TEST_USERNAME, jwtUtil.getUsernameFromToken(cleanedToken));
    }

    @Test
    @DisplayName("测试Token处理 - 带多余空格")
    public void testTokenWithExtraSpaces() {
        // 生成token
        String originalToken = jwtUtil.generateToken(TEST_USERNAME);
        String tokenWithSpaces = "  " + originalToken + "  ";

        // 验证原始token有效
        assertTrue(jwtUtil.validateToken(originalToken));
        assertEquals(TEST_USERNAME, jwtUtil.getUsernameFromToken(originalToken));
        
        // 验证带空格的token需要先清理
        String cleanedToken = jwtUtil.cleanToken(tokenWithSpaces);
        assertTrue(jwtUtil.validateToken(cleanedToken));
        assertEquals(TEST_USERNAME, jwtUtil.getUsernameFromToken(cleanedToken));
    }
    
    @Test
    @DisplayName("测试cleanToken方法")
    public void testCleanToken() {
        String originalToken = jwtUtil.generateToken(TEST_USERNAME);
        
        // 测试清理带Bearer前缀的token
        String tokenWithBearer = "Bearer " + originalToken;
        String cleanedToken = jwtUtil.cleanToken(tokenWithBearer);
        assertEquals(originalToken, cleanedToken);
        
        // 测试清理带空格的token
        String tokenWithSpaces = "  " + originalToken + "  ";
        cleanedToken = jwtUtil.cleanToken(tokenWithSpaces);
        assertEquals(originalToken, cleanedToken);
        
        // 测试清理null token
        assertNull(jwtUtil.cleanToken(null));
        
        // 测试清理空字符串
        assertEquals("", jwtUtil.cleanToken(""));
        
        // 测试清理只有空格的字符串
        assertEquals("", jwtUtil.cleanToken("   "));
    }
} 