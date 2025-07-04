package com.system.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UUIDUtilTest {

    @Test
    void testGenerateUUID_lengthAndFormat() {
        String uuid = UUIDUtil.generateUUID();
        assertNotNull(uuid);
        assertEquals(32, uuid.length());
        assertFalse(uuid.contains("-"));
    }

    @Test
    void testGenerateUUID_uniqueness() {
        String uuid1 = UUIDUtil.generateUUID();
        String uuid2 = UUIDUtil.generateUUID();
        assertNotEquals(uuid1, uuid2);
    }

    @Test
    void testPrivateConstructor() throws Exception {
        java.lang.reflect.Constructor<UUIDUtil> constructor = UUIDUtil.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        constructor.newInstance();
    }
}