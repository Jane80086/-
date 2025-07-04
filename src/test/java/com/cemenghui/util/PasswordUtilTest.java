package com.system.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PasswordUtilTest {
    private final PasswordUtil passwordUtil = new PasswordUtil();

    @Test
    void testEncodeAndMatches() {
        String rawPassword = "Test1234";
        String encoded = passwordUtil.encode(rawPassword);
        assertNotNull(encoded);
        assertTrue(passwordUtil.matches(rawPassword, encoded));
        assertFalse(passwordUtil.matches("WrongPass", encoded));
    }

    @Test
    public void testDummy() {
        assert true;
    }
} 