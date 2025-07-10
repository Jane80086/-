package com.cemenghui.system.util;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class CaptchaUtil {

    public String generateCaptcha(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder captcha = new StringBuilder();
        for (int i = 0; i < length; i++) {
            captcha.append(chars.charAt(random.nextInt(chars.length())));
        }
        return captcha.toString();
    }

    public boolean validateCaptcha(String inputCaptcha, String storedCaptcha) {
        return inputCaptcha.equalsIgnoreCase(storedCaptcha);
    }
}
