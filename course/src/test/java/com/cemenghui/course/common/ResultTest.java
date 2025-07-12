package com.cemenghui.course.common;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ResultTest {
    @Test
    void testSuccessWithData() {
        Result r = Result.success("data");
        assertEquals(200, r.getCode());
        assertEquals("操作成功", r.getMessage());
        assertEquals("data", r.getData());
    }
    @Test
    void testSuccessWithMessageAndData() {
        Result r = Result.success("msg", "data");
        assertEquals(200, r.getCode());
        assertEquals("msg", r.getMessage());
        assertEquals("data", r.getData());
    }
    @Test
    void testFail() {
        Result r = Result.fail("failMsg");
        assertEquals(500, r.getCode());
        assertEquals("failMsg", r.getMessage());
        assertNull(r.getData());
    }
} 