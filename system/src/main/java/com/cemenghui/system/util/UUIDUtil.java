package com.cemenghui.system.util;

import java.util.UUID;

public class UUIDUtil {
    /**
     * 生成32位无短横线的UUID字符串
     */
    public static String generateUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }
} 