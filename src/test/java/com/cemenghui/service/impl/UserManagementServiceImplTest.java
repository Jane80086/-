package com.system.service.impl;

import com.system.entity.EnterpriseUser;
import com.system.entity.UserTemplate;
import com.system.repository.UserManagementMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.system.dto.UserListDTO;
import com.system.dto.UserQueryDTO;
import com.system.dto.UserHistoryListDTO;
import com.system.dto.UserHistoryQueryDTO;
import com.system.entity.UserModifyHistory;
import com.system.entity.Enterprise;
import com.system.repository.EnterpriseMapper;
import com.system.repository.EnterpriseUserMapper;

@ExtendWith(MockitoExtension.class)
public class UserManagementServiceImplTest {

    @InjectMocks
    private UserManagementServiceImpl userManagementService;

    @Mock
    private UserManagementMapper userManagementMapper;

    @Mock
    private EnterpriseMapper enterpriseMapper;

    @Mock
    private EnterpriseUserMapper enterpriseUserMapper;

    @Test
    void testCreateUserByTemplate_withTemplate() {
        EnterpriseUser user = new EnterpriseUser();
        String templateId = "tpl123";
        UserTemplate template = new UserTemplate();
        template.setDefaultValues(new HashMap<>());
        template.setRole("admin");
        template.setPermissions(new HashSet<>(Arrays.asList("perm1", "perm2")));

        when(userManagementMapper.getUserTemplate(templateId)).thenReturn(template);

        String userId = userManagementService.createUserByTemplate(user, templateId);

        assertNotNull(userId);
        verify(userManagementMapper).createUserByTemplate(any(EnterpriseUser.class));
        verify(userManagementMapper).inheritPermissions(eq(userId), eq("admin"));
        verify(userManagementMapper, times(2)).addPermission(eq(userId), anyString());
    }

    @Test
    void testCreateUserByTemplate_templateNotFound() {
        when(userManagementMapper.getUserTemplate(anyString())).thenReturn(null);
        EnterpriseUser user = new EnterpriseUser();
        String userId = userManagementService.createUserByTemplate(user, "notfound");
        assertNull(userId);
    }

    @Test
    void testCreateUserByTemplate_emptyPermissions() {
        EnterpriseUser user = new EnterpriseUser();
        String templateId = "tpl123";
        UserTemplate template = new UserTemplate();
        template.setDefaultValues(new java.util.HashMap<>());
        template.setRole("admin");
        template.setPermissions(new java.util.HashSet<>()); // 空权限
        when(userManagementMapper.getUserTemplate(templateId)).thenReturn(template);
        String userId = userManagementService.createUserByTemplate(user, templateId);
        assertNotNull(userId);
        verify(userManagementMapper).createUserByTemplate(any(EnterpriseUser.class));
        verify(userManagementMapper).inheritPermissions(anyString(), eq("admin"));
        // 不会调用 addPermission
    }

    @Test
    void testCreateUserByTemplate_mapperThrows() {
        EnterpriseUser user = new EnterpriseUser();
        String templateId = "tpl123";
        UserTemplate template = new UserTemplate();
        template.setDefaultValues(new java.util.HashMap<>());
        template.setRole("admin");
        template.setPermissions(new java.util.HashSet<>());
        when(userManagementMapper.getUserTemplate(templateId)).thenReturn(template);
        doThrow(new RuntimeException("db error")).when(userManagementMapper).createUserByTemplate(any(EnterpriseUser.class));
        assertThrows(RuntimeException.class, () -> userManagementService.createUserByTemplate(user, templateId));
    }

    @Test
    public void testDummy() {
        assert true;
    }

    @Test
    void testGetUserList_withData() {
        UserQueryDTO query = new UserQueryDTO();
        EnterpriseUser user = new EnterpriseUser();
        when(userManagementMapper.getUserList(query)).thenReturn(Collections.singletonList(user));
        when(userManagementMapper.getUserCount(query)).thenReturn(1);
        UserListDTO dto = userManagementService.getUserList(query);
        assertNotNull(dto);
        assertNotNull(dto.getRecords());
        assertEquals(1, dto.getTotal());
    }

    @Test
    void testGetUserList_empty() {
        UserQueryDTO query = new UserQueryDTO();
        when(userManagementMapper.getUserList(query)).thenReturn(Collections.emptyList());
        when(userManagementMapper.getUserCount(query)).thenReturn(0);
        UserListDTO dto = userManagementService.getUserList(query);
        assertNotNull(dto);
        assertEquals(0, dto.getTotal());
    }

    @Test
    void testGetUserById_found() {
        EnterpriseUser user = new EnterpriseUser();
        when(userManagementMapper.getUserById("uid")).thenReturn(user);
        assertEquals(user, userManagementService.getUserById("uid"));
    }

    @Test
    void testGetUserById_notFound() {
        when(userManagementMapper.getUserById("uid")).thenReturn(null);
        assertNull(userManagementService.getUserById("uid"));
    }

    @Test
    void testGetUserByAccount_found() {
        EnterpriseUser user = new EnterpriseUser();
        when(userManagementMapper.getUserByAccount("acc")).thenReturn(user);
        assertEquals(user, userManagementService.getUserByAccount("acc"));
    }

    @Test
    void testGetUserByAccount_notFound() {
        when(userManagementMapper.getUserByAccount("acc")).thenReturn(null);
        assertNull(userManagementService.getUserByAccount("acc"));
    }

    @Test
    void testGetAllTemplates() {
        List<UserTemplate> list = Collections.singletonList(new UserTemplate());
        when(userManagementMapper.getAllTemplates()).thenReturn(list);
        assertEquals(list, userManagementService.getAllTemplates());
    }

    @Test
    void testGetUserModifyHistory() {
        List<UserModifyHistory> list = userManagementService.getUserModifyHistory("uid");
        assertNotNull(list);
    }

    @Test
    void testGetUserModifyHistoryPaged() {
        UserHistoryQueryDTO query = new UserHistoryQueryDTO();
        List<UserModifyHistory> list = Collections.singletonList(new UserModifyHistory());
        when(userManagementMapper.getUserModifyHistoryPaged(query)).thenReturn(list);
        when(userManagementMapper.countUserModifyHistory(query)).thenReturn(1);
        UserHistoryListDTO dto = userManagementService.getUserModifyHistoryPaged(query);
        assertNotNull(dto);
        assertEquals(1, dto.getTotal());
        assertEquals(list, dto.getRecords());
    }

    @Test
    void testGetEnterpriseUsersByEnterpriseId_withData() {
        String eid = "eid";
        EnterpriseUser user = new EnterpriseUser();
        List<EnterpriseUser> users = Collections.singletonList(user);
        Enterprise ent = new Enterprise();
        when(enterpriseUserMapper.findByEnterpriseId(eid)).thenReturn(users);
        when(enterpriseMapper.findByEnterpriseId(eid)).thenReturn(ent);
        List<EnterpriseUser> result = userManagementService.getEnterpriseUsersByEnterpriseId(eid);
        assertEquals(users, result);
        assertEquals(ent, result.get(0).getEnterprise());
    }

    @Test
    void testGetEnterpriseUsersByEnterpriseId_empty() {
        String eid = "eid";
        when(enterpriseUserMapper.findByEnterpriseId(eid)).thenReturn(Collections.emptyList());
        List<EnterpriseUser> result = userManagementService.getEnterpriseUsersByEnterpriseId(eid);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetEnterpriseUsersByEnterpriseId_nullParam() {
        List<EnterpriseUser> result = userManagementService.getEnterpriseUsersByEnterpriseId(null);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetEnterpriseUsersByEnterpriseId_mapperThrows() {
        String eid = "eid";
        when(enterpriseUserMapper.findByEnterpriseId(eid)).thenThrow(new RuntimeException("db error"));
        List<EnterpriseUser> result = userManagementService.getEnterpriseUsersByEnterpriseId(eid);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testSyncEnterpriseInfo_found() {
        String name = "ent";
        Enterprise ent = new Enterprise();
        ent.setEnterpriseId("eid");
        ent.setEnterpriseName(name);
        when(enterpriseMapper.findByEnterpriseName(name)).thenReturn(ent);
        EnterpriseUser user = userManagementService.syncEnterpriseInfo(name);
        assertNotNull(user);
        assertEquals("eid", user.getEnterpriseId());
        assertEquals(name, user.getEnterpriseName());
    }

    @Test
    void testSyncEnterpriseInfo_notFound() {
        String name = "ent";
        when(enterpriseMapper.findByEnterpriseName(name)).thenReturn(null);
        EnterpriseUser user = userManagementService.syncEnterpriseInfo(name);
        assertNull(user);
    }

    @Test
    void testSyncEnterpriseInfo_nullParam() {
        EnterpriseUser user = userManagementService.syncEnterpriseInfo(null);
        assertNull(user);
    }

    @Test
    void testSyncEnterpriseInfoById_found() {
        String eid = "eid";
        Enterprise ent = new Enterprise();
        ent.setEnterpriseId(eid);
        ent.setEnterpriseName("ent");
        when(enterpriseMapper.findByEnterpriseId(eid)).thenReturn(ent);
        EnterpriseUser user = userManagementService.syncEnterpriseInfoById(eid);
        assertNotNull(user);
        assertEquals(eid, user.getEnterpriseId());
        assertEquals("ent", user.getEnterpriseName());
    }

    @Test
    void testSyncEnterpriseInfoById_notFound() {
        String eid = "eid";
        when(enterpriseMapper.findByEnterpriseId(eid)).thenReturn(null);
        EnterpriseUser user = userManagementService.syncEnterpriseInfoById(eid);
        assertNull(user);
    }

    @Test
    void testSyncEnterpriseInfoById_nullParam() {
        EnterpriseUser user = userManagementService.syncEnterpriseInfoById(null);
        assertNull(user);
    }
} 