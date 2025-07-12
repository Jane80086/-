package com.cemenghui.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cemenghui.course.dao.UserDao;
import com.cemenghui.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {
    @InjectMocks
    private UserServiceImpl userServiceImpl;
    @Mock
    private UserDao userDao;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindByUsername() {
        User user = new User();
        user.setUsername("test");
        when(userDao.selectOne(any(LambdaQueryWrapper.class))).thenReturn(user);
        Optional<User> result = userServiceImpl.findByUsername("test");
        assertTrue(result.isPresent());
    }

    @Test
    void testFindByUserType() {
        when(userDao.selectList(any(LambdaQueryWrapper.class))).thenReturn(Collections.singletonList(new User()));
        List<User> result = userServiceImpl.findByUserType("ADMIN");
        assertEquals(1, result.size());
    }

    @Test
    void testFindByUserType_Exception() {
        when(userDao.selectList(any(LambdaQueryWrapper.class))).thenThrow(new RuntimeException("fail"));
        List<User> result = userServiceImpl.findByUserType("ADMIN");
        assertFalse(result.isEmpty()); // 返回mock用户
    }

    @Test
    void testGetAllUsers() {
        when(userDao.selectList(null)).thenReturn(Collections.singletonList(new User()));
        List<User> result = userServiceImpl.getAllUsers();
        assertEquals(1, result.size());
    }

    @Test
    void testGetAllUsers_Exception() {
        when(userDao.selectList(null)).thenThrow(new RuntimeException("fail"));
        List<User> result = userServiceImpl.getAllUsers();
        assertFalse(result.isEmpty());
    }

    @Test
    void testFindById() {
        User user = new User();
        when(userDao.selectById(1L)).thenReturn(user);
        assertEquals(user, userServiceImpl.findById(1L));
    }

    @Test
    void testFindById_Exception() {
        when(userDao.selectById(1L)).thenThrow(new RuntimeException("fail"));
        assertNull(userServiceImpl.findById(1L));
    }

    @Test
    void testSaveUser() {
        User user = new User();
        when(userDao.insert(user)).thenReturn(1);
        assertEquals(user, userServiceImpl.saveUser(user));
    }

    @Test
    void testSaveUser_Exception() {
        User user = new User();
        when(userDao.insert(user)).thenThrow(new RuntimeException("fail"));
        assertNull(userServiceImpl.saveUser(user));
    }

    @Test
    void testDeleteUser() {
        when(userDao.deleteById(1L)).thenReturn(1);
        assertTrue(userServiceImpl.deleteUser(1L));
    }

    @Test
    void testDeleteUser_Exception() {
        when(userDao.deleteById(1L)).thenThrow(new RuntimeException("fail"));
        assertFalse(userServiceImpl.deleteUser(1L));
    }

    @Test
    void testExistsByUsername() {
        when(userDao.selectCount(any(QueryWrapper.class))).thenReturn(1L);
        assertTrue(userServiceImpl.existsByUsername("test"));
    }

    @Test
    void testDeleteByUsername() {
        when(userDao.delete(any(QueryWrapper.class))).thenReturn(1);
        userServiceImpl.deleteByUsername("test");
        verify(userDao).delete(any(QueryWrapper.class));
    }

    @Test
    void testFindByEmail() {
        User user = new User();
        when(userDao.selectOne(any(LambdaQueryWrapper.class))).thenReturn(user);
        assertEquals(user, userServiceImpl.findByEmail("a@b.com"));
    }

    @Test
    void testFindByEmail_Exception() {
        when(userDao.selectOne(any(LambdaQueryWrapper.class))).thenThrow(new RuntimeException("fail"));
        assertNull(userServiceImpl.findByEmail("a@b.com"));
    }

    @Test
    void testFindByPhone() {
        User user = new User();
        when(userDao.selectOne(any(LambdaQueryWrapper.class))).thenReturn(user);
        assertEquals(user, userServiceImpl.findByPhone("123"));
    }

    @Test
    void testFindByPhone_Exception() {
        when(userDao.selectOne(any(LambdaQueryWrapper.class))).thenThrow(new RuntimeException("fail"));
        assertNull(userServiceImpl.findByPhone("123"));
    }

    @Test
    void testFindByEnterpriseId() {
        when(userDao.selectList(any(LambdaQueryWrapper.class))).thenReturn(Collections.singletonList(new User()));
        List<User> result = userServiceImpl.findByEnterpriseId("eid");
        assertEquals(1, result.size());
    }

    @Test
    void testFindByEnterpriseId_Exception() {
        when(userDao.selectList(any(LambdaQueryWrapper.class))).thenThrow(new RuntimeException("fail"));
        List<User> result = userServiceImpl.findByEnterpriseId("eid");
        assertTrue(result.isEmpty());
    }

    @Test
    void testFindEnabledUsers() {
        when(userDao.selectList(any(LambdaQueryWrapper.class))).thenReturn(Collections.singletonList(new User()));
        List<User> result = userServiceImpl.findEnabledUsers();
        assertEquals(1, result.size());
    }

    @Test
    void testFindEnabledUsers_Exception() {
        when(userDao.selectList(any(LambdaQueryWrapper.class))).thenThrow(new RuntimeException("fail"));
        List<User> result = userServiceImpl.findEnabledUsers();
        assertTrue(result.isEmpty());
    }

    @Test
    void testFindDisabledUsers() {
        when(userDao.selectList(any(LambdaQueryWrapper.class))).thenReturn(Collections.singletonList(new User()));
        List<User> result = userServiceImpl.findDisabledUsers();
        assertEquals(1, result.size());
    }

    @Test
    void testFindDisabledUsers_Exception() {
        when(userDao.selectList(any(LambdaQueryWrapper.class))).thenThrow(new RuntimeException("fail"));
        List<User> result = userServiceImpl.findDisabledUsers();
        assertTrue(result.isEmpty());
    }

    @Test
    void testCreateUser() {
        User user = new User();
        when(userDao.insert(user)).thenReturn(1);
        assertEquals(user, userServiceImpl.createUser(user));
    }

    @Test
    void testEnableUser() {
        User user = new User();
        when(userDao.selectById(1L)).thenReturn(user);
        when(userDao.updateById(any(User.class))).thenReturn(1);
        assertTrue(userServiceImpl.enableUser(1L));
    }

    @Test
    void testEnableUser_NotFound() {
        when(userDao.selectById(1L)).thenReturn(null);
        assertFalse(userServiceImpl.enableUser(1L));
    }

    @Test
    void testEnableUser_Exception() {
        when(userDao.selectById(1L)).thenThrow(new RuntimeException("fail"));
        assertFalse(userServiceImpl.enableUser(1L));
    }

    @Test
    void testDisableUser() {
        User user = new User();
        when(userDao.selectById(1L)).thenReturn(user);
        when(userDao.updateById(any(User.class))).thenReturn(1);
        assertTrue(userServiceImpl.disableUser(1L));
    }

    @Test
    void testDisableUser_NotFound() {
        when(userDao.selectById(1L)).thenReturn(null);
        assertFalse(userServiceImpl.disableUser(1L));
    }

    @Test
    void testDisableUser_Exception() {
        when(userDao.selectById(1L)).thenThrow(new RuntimeException("fail"));
        assertFalse(userServiceImpl.disableUser(1L));
    }

    @Test
    void testValidateLogin() {
        User user = new User();
        user.setUsername("test");
        user.setPassword("123");
        when(userDao.selectOne(any(LambdaQueryWrapper.class))).thenReturn(user);
        assertNotNull(userServiceImpl.validateLogin("test", "123"));
    }

    @Test
    void testValidateLogin_WrongPassword() {
        User user = new User();
        user.setUsername("test");
        user.setPassword("123");
        when(userDao.selectOne(any(LambdaQueryWrapper.class))).thenReturn(user);
        assertNull(userServiceImpl.validateLogin("test", "wrong"));
    }

    @Test
    void testValidateLogin_Exception() {
        when(userDao.selectOne(any(LambdaQueryWrapper.class))).thenThrow(new RuntimeException("fail"));
        assertNull(userServiceImpl.validateLogin("test", "123"));
    }

    @Test
    void testGetUsersByPage() {
        User user = new User();
        when(userDao.selectList(null)).thenReturn(Collections.singletonList(user));
        List<User> result = userServiceImpl.getUsersByPage(0, 1);
        assertEquals(1, result.size());
    }

    @Test
    void testGetUsersByPage_Empty() {
        when(userDao.selectList(null)).thenReturn(Collections.emptyList());
        List<User> result = userServiceImpl.getUsersByPage(1, 10);
        assertTrue(result.isEmpty());
    }
    
    @Test
    void testOnUserLogin() throws Exception {
        // 通过反射调用protected方法
        java.lang.reflect.Method method = UserServiceImpl.class.getDeclaredMethod("onUserLogin", Long.class);
        method.setAccessible(true);
        method.invoke(userServiceImpl, 1L);
    }
} 