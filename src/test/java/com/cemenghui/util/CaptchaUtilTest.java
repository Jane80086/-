package com.system.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CaptchaUtilTest {

    @Test
    void testGenerateCaptcha_length() {
        CaptchaUtil util = new CaptchaUtil();
        String captcha = util.generateCaptcha(6);
        assertNotNull(captcha);
        assertEquals(6, captcha.length());
    }

    @Test
    void testGenerateCaptcha_randomness() {
        CaptchaUtil util = new CaptchaUtil();
        String c1 = util.generateCaptcha(6);
        String c2 = util.generateCaptcha(6);
        // 理论上有极小概率相同，但几乎不会
        assertNotEquals(c1, c2);
    }

    @Test
    void testValidateCaptcha_caseInsensitive() {
        CaptchaUtil util = new CaptchaUtil();
        assertTrue(util.validateCaptcha("aBc123", "AbC123"));
        assertFalse(util.validateCaptcha("aBc123", "xyz789"));
    }
}