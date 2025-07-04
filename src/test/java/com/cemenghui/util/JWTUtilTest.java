package com.system.util;

import io.jsonwebtoken.Claims;
import com.system.config.JWTConfig;
import com.system.entity.AdminUser;
import java.lang.reflect.Field;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class JWTUtilTest {
    private JWTUtil jwtUtil;

    @BeforeEach
    void setUp() throws Exception {
        jwtUtil = new JWTUtil();
        JWTConfig jwtConfig = new JWTConfig();
        Field secretField = JWTConfig.class.getDeclaredField("secret");
        secretField.setAccessible(true);
        secretField.set(jwtConfig, "testSecretKeyForJwtUnitTest1234567890testSecretKeyForJwtUnitTest1234567890");
        Field field = JWTUtil.class.getDeclaredField("jwtConfig");
        field.setAccessible(true);
        field.set(jwtUtil, jwtConfig);
    }

    @Test
    void testGenerateAndParseToken() {
        String account = "testuser";
        long expireMillis = 1000 * 60 * 10; // 10分钟
        String token = jwtUtil.generateToken(account, expireMillis);
        assertNotNull(token);
        Claims claims = JWTUtil.parseToken(token);
        assertEquals(account, claims.getSubject());
        assertNotNull(claims.get("iat"));
        assertNotNull(claims.get("exp"));
    }

    @Test
    void testCleanToken() {
        String raw = "Bearer abc.def.ghi ";
        String cleaned = JWTUtil.cleanToken(raw);
        assertEquals("abc.def.ghi", cleaned);
    }

    @Test
    void testParseToken_invalidToken() {
        String invalidToken = "invalid.token";
        assertThrows(RuntimeException.class, () -> JWTUtil.parseToken(invalidToken));
    }

    @Test
    void testIsValidJWTFormat_valid() {
        String token = jwtUtil.generateToken("user", 10000);
        assertTrue(JWTUtil.isValidJWTFormat(token));
    }

    @Test
    void testIsValidJWTFormat_invalid() {
        assertFalse(JWTUtil.isValidJWTFormat("not.a")); // 只有两段
        assertFalse(JWTUtil.isValidJWTFormat("..")); // 三段但全为空
        assertFalse(JWTUtil.isValidJWTFormat("a..")); // 有空段
        assertFalse(JWTUtil.isValidJWTFormat(null));
        assertFalse(JWTUtil.isValidJWTFormat(""));
    }

    @Test
    void testGetAccountFromToken() {
        String token = jwtUtil.generateToken("account1", 10000);
        String account = jwtUtil.getAccountFromToken(token);
        assertEquals("account1", account);
    }

    @Test
    void testGetRoleFromToken() {
        AdminUser admin = new AdminUser();
        admin.setAccount("admin1");
        admin.setRole("ADMIN");
        String token = jwtUtil.generateToken(admin, 10000);
        String role = jwtUtil.getRoleFromToken(token);
        assertEquals("ADMIN", role);
    }

    @Test
    void testGetUserIdFromToken() {
        AdminUser admin = new AdminUser();
        admin.setAccount("admin2");
        admin.setRole("ADMIN");
        String token = jwtUtil.generateToken(admin, 10000);
        String userId = jwtUtil.getUserIdFromToken(token);
        assertEquals("admin2", userId);
    }

    @Test
    void testInitStaticKey() {
        assertDoesNotThrow(() -> jwtUtil.initStaticKey());
    }

    @Test
    void testPrivateConstructor() throws Exception {
        java.lang.reflect.Constructor<JWTUtil> constructor = JWTUtil.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        constructor.newInstance();
    }

    @Test
    void testDecodeBase64Url_normalAndError() throws Exception {
        // 1. 正常解码
        java.lang.reflect.Method method = JWTUtil.class.getDeclaredMethod("decodeBase64Url", String.class);
        method.setAccessible(true);
        String encoded = java.util.Base64.getUrlEncoder().encodeToString("hello".getBytes(java.nio.charset.StandardCharsets.UTF_8));
        assertEquals("hello", method.invoke(null, encoded));
        // 2. 空字符串
        assertEquals("空字符串", method.invoke(null, ""));
        // 3. 非法base64
        assertTrue(((String)method.invoke(null, "!!!")).startsWith("解码失败"));
    }

    @Test
    void testGetSecretKey_error() throws Exception {
        JWTConfig badConfig = new JWTConfig();
        Field secretField = JWTConfig.class.getDeclaredField("secret");
        secretField.setAccessible(true);
        secretField.set(badConfig, "not_base64");
        Field field = JWTUtil.class.getDeclaredField("jwtConfig");
        field.setAccessible(true);
        field.set(jwtUtil, badConfig);
        // 清空缓存
        Field cachedSecretField = JWTUtil.class.getDeclaredField("cachedSecret");
        cachedSecretField.setAccessible(true);
        cachedSecretField.set(null, null);
        Field cachedKeyField = JWTUtil.class.getDeclaredField("cachedKey");
        cachedKeyField.setAccessible(true);
        cachedKeyField.set(null, null);
        java.lang.reflect.Method m = JWTUtil.class.getDeclaredMethod("getSecretKey");
        m.setAccessible(true);
        try {
            m.invoke(jwtUtil);
            fail("Should throw RuntimeException");
        } catch (java.lang.reflect.InvocationTargetException e) {
            assertTrue(e.getCause() instanceof RuntimeException);
        }
    }

    @Test
    void testParseToken_exceptionBranch() {
        // 传入 null，触发 Exception 分支
        assertThrows(RuntimeException.class, () -> JWTUtil.parseToken(null));
    }

    @Test
    void testParseToken_malformedJwtWith3Parts() {
        // 构造三段的非法 token，触发 parts.length == 3 分支
        String badToken = "a.b.c";
        try {
            JWTUtil.parseToken(badToken);
        } catch (Exception e) {
            // ignore
        }
    }

    @Test
    void testGetUserIdFromToken_userIdField() throws Exception {
        jwtUtil.initStaticKey();
        java.util.Map<String, Object> claims = new java.util.HashMap<>();
        claims.put("userId", "u123");
        String token = io.jsonwebtoken.Jwts.builder()
            .setClaims(claims)
            .signWith(io.jsonwebtoken.security.Keys.hmacShaKeyFor(
                io.jsonwebtoken.io.Decoders.BASE64.decode("testSecretKeyForJwtUnitTest1234567890testSecretKeyForJwtUnitTest1234567890")))
            .compact();
        assertEquals("u123", jwtUtil.getUserIdFromToken(token));
    }

    @Test
    void testGetUserIdFromToken_accountField() throws Exception {
        jwtUtil.initStaticKey();
        java.util.Map<String, Object> claims = new java.util.HashMap<>();
        claims.put("account", "acc123");
        String token = io.jsonwebtoken.Jwts.builder()
            .setClaims(claims)
            .signWith(io.jsonwebtoken.security.Keys.hmacShaKeyFor(
                io.jsonwebtoken.io.Decoders.BASE64.decode("testSecretKeyForJwtUnitTest1234567890testSecretKeyForJwtUnitTest1234567890")))
            .compact();
        assertEquals("acc123", jwtUtil.getUserIdFromToken(token));
    }

    @Test
    void testGetUserIdFromToken_noUserIdNoSubNoAccount() throws Exception {
        jwtUtil.initStaticKey();
        java.util.Map<String, Object> claims = new java.util.HashMap<>();
        claims.put("foo", "bar");
        String token = io.jsonwebtoken.Jwts.builder()
            .setClaims(claims)
            .signWith(io.jsonwebtoken.security.Keys.hmacShaKeyFor(
                io.jsonwebtoken.io.Decoders.BASE64.decode("testSecretKeyForJwtUnitTest1234567890testSecretKeyForJwtUnitTest1234567890")))
            .compact();
        assertNull(jwtUtil.getUserIdFromToken(token));
    }

    @Test
    void testCleanToken_variousCases() {
        assertNull(JWTUtil.cleanToken(null));
        assertEquals("abc.def.ghi", JWTUtil.cleanToken("Bearer abc.def.ghi "));
        assertEquals("abc.def.ghi", JWTUtil.cleanToken("abc .def .ghi "));
        assertEquals("abc.def.ghi", JWTUtil.cleanToken("abc.def.ghi!"));
    }

    @Test
    void testGenerateToken_account_noExpire() {
        String token = jwtUtil.generateToken("testuser");
        assertNotNull(token);
    }

    @Test
    void testGenerateToken_admin_noExpire() {
        AdminUser admin = new AdminUser();
        admin.setAccount("admin3");
        admin.setRole("ADMIN");
        String token = jwtUtil.generateToken(admin);
        assertNotNull(token);
    }

    @Test
    void testParseToken_emptyString_exceptionBranch() {
        assertThrows(RuntimeException.class, () -> JWTUtil.parseToken(""));
    }

    @Test
    void testDecodeBase64Url_paddingCases() throws Exception {
        java.lang.reflect.Method method = JWTUtil.class.getDeclaredMethod("decodeBase64Url", String.class);
        method.setAccessible(true);
        // 长度为2
        String s2 = "YW"; // "a"
        assertEquals("a", method.invoke(null, s2));
        // 长度为3
        String s3 = "YWJ"; // "ab"
        assertEquals("ab", method.invoke(null, s3));
    }

    @Test
    void testGetRoleFromToken_noRole() {
        String token = jwtUtil.generateToken("user", 10000);
        assertNull(jwtUtil.getRoleFromToken(token));
    }

    @Test
    void testGetSecretKey_cacheHit() throws Exception {
        jwtUtil.initStaticKey();
        java.lang.reflect.Method m = JWTUtil.class.getDeclaredMethod("getSecretKey");
        m.setAccessible(true);
        Object key1 = m.invoke(jwtUtil);
        Object key2 = m.invoke(jwtUtil);
        assertSame(key1, key2);
    }
} 