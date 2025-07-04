package com.system.service.impl;

import com.system.service.EnterpriseService;
import com.system.entity.Enterprise;
import com.system.repository.EnterpriseMapper;
import org.junit.jupiter.api.Test;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EnterpriseServiceImplTest {
    @Test
    void testGetEnterpriseListOffset() {
        EnterpriseMapper mapper = mock(EnterpriseMapper.class);
        when(mapper.selectEnterpriseListPaged(anyString(), anyString(), anyString(), eq(0), eq(10))).thenReturn(Collections.emptyList());
        when(mapper.countEnterpriseListPaged(anyString(), anyString(), anyString())).thenReturn(0);
        EnterpriseServiceImpl service = new EnterpriseServiceImpl();
        // 反射注入mock依赖
        try {
            java.lang.reflect.Field field = EnterpriseServiceImpl.class.getDeclaredField("enterpriseMapper");
            field.setAccessible(true);
            field.set(service, mapper);
        } catch (Exception e) {
            fail("依赖注入失败: " + e.getMessage());
        }
        Map<String, Object> result = service.getEnterpriseList(1, 10, "", "", "");
        assertEquals(0, result.get("total"));
        assertTrue(result.containsKey("records"));
        // page=2, size=10, offset应为10
        when(mapper.selectEnterpriseListPaged(anyString(), anyString(), anyString(), eq(10), eq(10))).thenReturn(Collections.emptyList());
        service.getEnterpriseList(2, 10, "", "", "");
        verify(mapper).selectEnterpriseListPaged(anyString(), anyString(), anyString(), eq(10), eq(10));
    }

    @Test
    void testGetEnterpriseListWithData() {
        EnterpriseMapper mapper = mock(EnterpriseMapper.class);
        EnterpriseServiceImpl service = new EnterpriseServiceImpl();
        try {
            java.lang.reflect.Field field = EnterpriseServiceImpl.class.getDeclaredField("enterpriseMapper");
            field.setAccessible(true);
            field.set(service, mapper);
        } catch (Exception e) {
            fail("依赖注入失败: " + e.getMessage());
        }
        List<Enterprise> list = Collections.singletonList(new Enterprise());
        when(mapper.selectEnterpriseListPaged(anyString(), anyString(), anyString(), eq(0), eq(10))).thenReturn(list);
        when(mapper.countEnterpriseListPaged(anyString(), anyString(), anyString())).thenReturn(1);
        Map<String, Object> result = service.getEnterpriseList(1, 10, "", "", "");
        assertEquals(1, result.get("total"));
        assertEquals(list, result.get("records"));
    }

    @Test
    void testGetEnterpriseListWithNullParams() {
        EnterpriseMapper mapper = mock(EnterpriseMapper.class);
        EnterpriseServiceImpl service = new EnterpriseServiceImpl();
        try {
            java.lang.reflect.Field field = EnterpriseServiceImpl.class.getDeclaredField("enterpriseMapper");
            field.setAccessible(true);
            field.set(service, mapper);
        } catch (Exception e) {
            fail("依赖注入失败: " + e.getMessage());
        }
        when(mapper.selectEnterpriseListPaged(any(), any(), any(), anyInt(), anyInt())).thenReturn(Collections.emptyList());
        when(mapper.countEnterpriseListPaged(any(), any(), any())).thenReturn(0);
        Map<String, Object> result = service.getEnterpriseList(1, 10, null, null, null);
        assertEquals(0, result.get("total"));
        assertTrue(result.containsKey("records"));
    }

    @Test
    void testGetEnterpriseList_mapperThrows() {
        EnterpriseMapper mapper = mock(EnterpriseMapper.class);
        EnterpriseServiceImpl service = new EnterpriseServiceImpl();
        try {
            java.lang.reflect.Field field = EnterpriseServiceImpl.class.getDeclaredField("enterpriseMapper");
            field.setAccessible(true);
            field.set(service, mapper);
        } catch (Exception e) {
            fail("依赖注入失败: " + e.getMessage());
        }
        when(mapper.selectEnterpriseListPaged(any(), any(), any(), anyInt(), anyInt())).thenThrow(new RuntimeException("db error"));
        when(mapper.countEnterpriseListPaged(any(), any(), any())).thenReturn(0);
        assertThrows(RuntimeException.class, () -> service.getEnterpriseList(1, 10, "", "", ""));
    }
} 