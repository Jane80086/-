package com.cemenghui.news.service.impl;

import com.cemenghui.news.constants.NewsConstants;
import com.cemenghui.news.exception.UnauthorizedException;
import com.cemenghui.news.mapper.NewsMapper;
import com.cemenghui.news.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.mockito.stubbing.Answer;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorizationServiceImplTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private NewsMapper newsMapper;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthorizationServiceImpl authorizationService;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);
    }

    // 测试方法将在这里添加
    @Test
    void getCurrentUserId_WithValidJwt_ReturnsUserId() {
        // 准备JWT
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaim("userId")).thenReturn(123L);

        // 模拟认证
        when(authentication.getPrincipal()).thenReturn(jwt);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        // 执行测试
        Long userId = authorizationService.getCurrentUserId();

        // 验证
        assertEquals(123L, userId);
    }

    @Test
    void getCurrentUserId_WithUnAuthenticatedUser_ThrowsUnauthorizedException() {
        when(securityContext.getAuthentication()).thenReturn(null);

        assertThrows(UnauthorizedException.class, () -> authorizationService.getCurrentUserId());
    }

    @Test
    void getCurrentUserId_WithAnonymousUser_ThrowsUnauthorizedException() {
        when(authentication.isAuthenticated()).thenReturn(false);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        assertThrows(UnauthorizedException.class, () -> authorizationService.getCurrentUserId());
    }

    @Test
    void getCurrentUserId_WithInvalidJwtClaim_ThrowsUnauthorizedException() {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaim("userId")).thenReturn("invalid");
        when(authentication.getPrincipal()).thenReturn(jwt);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        assertThrows(UnauthorizedException.class, () -> authorizationService.getCurrentUserId());
    }

    @Test
    void getCurrentUserId_WithNonJwtPrincipal_ThrowsUnauthorizedException() {
        when(authentication.getPrincipal()).thenReturn("non-jwt-principal");
        when(authentication.isAuthenticated()).thenReturn(true);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        assertThrows(UnauthorizedException.class, () -> authorizationService.getCurrentUserId());
    }

    @Test
    void getCurrentUserId_WithJwtClaimAsNonNumber_ThrowsUnauthorizedException() {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaim("userId")).thenReturn(Map.of("invalid", "data")); // 非数字类型
        when(authentication.getPrincipal()).thenReturn(jwt);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        assertThrows(UnauthorizedException.class, () -> authorizationService.getCurrentUserId());
    }

    @Test
    void isOwner_WithValidOwner_ReturnsTrue() {
        when(newsMapper.existsByIdAndUserId(1L, 123L)).thenReturn(true);

        assertTrue(authorizationService.isOwner(123L, 1L));
    }

    @Test
    void isOwner_WithNonOwner_ReturnsFalse() {
        when(newsMapper.existsByIdAndUserId(1L, 123L)).thenReturn(false);

        assertFalse(authorizationService.isOwner(123L, 1L));
    }

    @Test
    void isOwner_WithNullUserId_ReturnsFalse() {
        assertFalse(authorizationService.isOwner(null, 1L));
    }

    @Test
    void isOwner_WithNullNewsId_ReturnsFalse() {
        assertFalse(authorizationService.isOwner(123L, null));
    }

    @Test
    void isAdmin_WithAdminRole_ReturnsTrue() {
        // 1. 模拟权限
        GrantedAuthority authority = new SimpleGrantedAuthority("ADMIN");

        // 2. 使用 Answer 返回权限列表
        when(authentication.getAuthorities())
                .thenAnswer(invocation -> Collections.singletonList(authority));

        // 3. 模拟已认证状态
        when(authentication.isAuthenticated()).thenReturn(true);

        // 4. 绑定 SecurityContext
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext); // 关键步骤！

        // 5. 调用并验证
        assertTrue(authorizationService.isAdmin(123L));
    }

    @Test
    void isAdmin_WithNonAdminRole_ReturnsFalse() {
        GrantedAuthority authority = new SimpleGrantedAuthority("USER");
        when(authentication.getAuthorities())
                .thenAnswer(invocation -> Collections.singletonList(authority));
        when(authentication.isAuthenticated()).thenReturn(true);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        assertFalse(authorizationService.isAdmin(123L));
    }

    @Test
    void isAdmin_WithNullUserId_ReturnsFalse() {
        assertFalse(authorizationService.isAdmin(null));
    }

    @Test
    void isAdmin_WithNoAuthentication_ReturnsFalse() {
        when(securityContext.getAuthentication()).thenReturn(null);

        assertFalse(authorizationService.isAdmin(123L));
    }

    @Test
    void isEnterprise_WithEnterpriseRole_ReturnsTrue() {
        GrantedAuthority authority = new SimpleGrantedAuthority("ENTERPRISE");
        when(authentication.getAuthorities())
                .thenAnswer(invocation -> Collections.singletonList(authority));
        when(authentication.isAuthenticated()).thenReturn(true);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        assertTrue(authorizationService.isEnterprise(123L));
    }

    @Test
    void isEnterprise_WithNonEnterpriseRole_ReturnsFalse() {
        GrantedAuthority authority = new SimpleGrantedAuthority("USER");
        when(authentication.getAuthorities())
                .thenAnswer(invocation -> Collections.singletonList(authority));
        when(authentication.isAuthenticated()).thenReturn(true);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        assertFalse(authorizationService.isEnterprise(123L));
    }

    @Test
    void isEnterprise_WithNullUserId_ReturnsFalse() {
        assertFalse(authorizationService.isEnterprise(null));
    }

    @Test
    void isEnterprise_WithEmptyAuthorities_ReturnsFalse() {
        when(authentication.getAuthorities()).thenReturn(Collections.emptyList());
        when(authentication.isAuthenticated()).thenReturn(true);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        assertFalse(authorizationService.isEnterprise(123L));
    }

    @Test
    void checkAdminPermission_WithAdminRole_DoesNotThrow() {
        GrantedAuthority authority = new SimpleGrantedAuthority("ADMIN");
        when(authentication.getAuthorities())
                .thenAnswer(invocation -> Collections.singletonList(authority));
        when(authentication.isAuthenticated()).thenReturn(true);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        assertDoesNotThrow(() -> authorizationService.checkAdminPermission(123L));
    }

    @Test
    void checkAdminPermission_WithNonAdminRole_ThrowsUnauthorizedException() {
        GrantedAuthority authority = new SimpleGrantedAuthority("USER");
        when(authentication.getAuthorities())
                .thenAnswer(invocation -> Collections.singletonList(authority));
        when(authentication.isAuthenticated()).thenReturn(true);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        assertThrows(UnauthorizedException.class, () -> authorizationService.checkAdminPermission(123L));
    }

    @Test
    void checkAdminPermission_WithNullUserId_ThrowsUnauthorizedException() {
        assertThrows(UnauthorizedException.class, () -> authorizationService.checkAdminPermission(null));
    }

    @Test
    void checkEnterprisePermission_WithEnterpriseRole_DoesNotThrow() {
        GrantedAuthority authority = new SimpleGrantedAuthority("ENTERPRISE");
        when(authentication.getAuthorities())
                .thenAnswer(invocation -> Collections.singletonList(authority));
        when(authentication.isAuthenticated()).thenReturn(true);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        assertDoesNotThrow(() -> authorizationService.checkEnterprisePermission(123L));
    }

    @Test
    void checkEnterprisePermission_WithAdminRole_DoesNotThrow() {
        GrantedAuthority authority = new SimpleGrantedAuthority("ADMIN");
        when(authentication.getAuthorities())
                .thenAnswer(invocation -> Collections.singletonList(authority));
        when(authentication.isAuthenticated()).thenReturn(true);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        assertDoesNotThrow(() -> authorizationService.checkEnterprisePermission(123L));
    }

    @Test
    void checkEnterprisePermission_WithNonPrivilegedRole_ThrowsUnauthorizedException() {
        GrantedAuthority authority = new SimpleGrantedAuthority("USER");
        when(authentication.getAuthorities())
                .thenAnswer(invocation -> Collections.singletonList(authority));
        when(authentication.isAuthenticated()).thenReturn(true);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        assertThrows(UnauthorizedException.class, () -> authorizationService.checkEnterprisePermission(123L));
    }

    @Test
    void checkEnterprisePermission_WithNullUserId_ThrowsUnauthorizedException() {
        assertThrows(UnauthorizedException.class, () -> authorizationService.checkEnterprisePermission(null));
    }

    @Test
    void canEditNews_WithAdmin_ReturnsTrue() {
        GrantedAuthority authority = new SimpleGrantedAuthority("ADMIN");
        when(authentication.getAuthorities())
                .thenAnswer(invocation -> Collections.singletonList(authority));
        when(authentication.isAuthenticated()).thenReturn(true);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        assertTrue(authorizationService.canEditNews(123L, 1L));
    }

    @Test
    void canEditNews_WithOwner_ReturnsTrue() {
        when(newsMapper.existsByIdAndUserId(1L, 123L)).thenReturn(true);

        assertTrue(authorizationService.canEditNews(123L, 1L));
    }

    @Test
    void canEditNews_WithNonOwnerNonAdmin_ReturnsFalse() {
        assertFalse(authorizationService.canEditNews(123L, 1L));
    }

    @Test
    void canDeleteNews_WithAdmin_ReturnsTrue() {
        GrantedAuthority authority = new SimpleGrantedAuthority("ADMIN");
        when(authentication.getAuthorities())
                .thenAnswer(invocation -> Collections.singletonList(authority));
        when(authentication.isAuthenticated()).thenReturn(true);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        assertTrue(authorizationService.canDeleteNews(123L, 1L));
    }

    @Test
    void canDeleteNews_WithOwner_ReturnsTrue() {
        when(newsMapper.existsByIdAndUserId(1L, 123L)).thenReturn(true);

        assertTrue(authorizationService.canDeleteNews(123L, 1L));
    }

    @Test
    void hasPermission_WithAdmin_ReturnsTrueForAllOperations() {
        GrantedAuthority authority = new SimpleGrantedAuthority("ADMIN");
        when(authentication.getAuthorities())
                .thenAnswer(invocation -> Collections.singletonList(authority));
        when(authentication.isAuthenticated()).thenReturn(true);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        assertTrue(authorizationService.hasPermission(123L, "EDIT", 1L));
        assertTrue(authorizationService.hasPermission(123L, "DELETE", 1L));
        assertTrue(authorizationService.hasPermission(123L, "PUBLISH", 1L));
    }

    @Test
    void hasPermission_WithEnterprise_ReturnsTrueForPublish() {
        GrantedAuthority authority = new SimpleGrantedAuthority("ENTERPRISE");
        when(authentication.getAuthorities())
                .thenAnswer(invocation -> Collections.singletonList(authority));
        when(authentication.isAuthenticated()).thenReturn(true);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        assertTrue(authorizationService.hasPermission(123L, "PUBLISH", null));
    }

    @Test
    void hasPermission_WithOwner_ReturnsTrueForEditAndDelete() {
        // 1. 模拟用户是资源所有者
        when(newsMapper.existsByIdAndUserId(1L, 123L)).thenReturn(true);

        // 2. 模拟用户不是管理员（避免干扰测试逻辑）
        when(authentication.getAuthorities()).thenReturn(Collections.emptyList());
        when(authentication.isAuthenticated()).thenReturn(true);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        // 3. 验证 EDIT 和 DELETE 权限
        assertTrue(authorizationService.hasPermission(123L, NewsConstants.PERMISSION_EDIT, 1L));
        assertTrue(authorizationService.hasPermission(123L, NewsConstants.PERMISSION_DELETE, 1L));
    }

    @Test
    void hasPermission_WithInvalidOperation_ReturnsFalse() {
        assertFalse(authorizationService.hasPermission(123L, "INVALID_OPERATION", 1L));
    }

    @Test
    void hasPermission_WithNullUserId_ReturnsFalse() {
        assertFalse(authorizationService.hasPermission(null, "EDIT", 1L));
    }

    @Test
    void hasPermission_WithNullOperation_ReturnsFalse() {
        assertFalse(authorizationService.hasPermission(123L, null, 1L));
    }

    @Test
    void hasPermission_WithAuditPermission_ReturnsFalseForNonAdmin() {
        assertFalse(authorizationService.hasPermission(123L, NewsConstants.PERMISSION_AUDIT, 1L));
    }

    @Test
    void hasPermission_WithSpecialCharacterOperation_ReturnsFalse() {
        assertFalse(authorizationService.hasPermission(123L, "EDIT@", 1L));
    }

    @Test
    void canPublishNews_WithAdmin_ReturnsTrue() {
        GrantedAuthority authority = new SimpleGrantedAuthority("ADMIN");
        when(authentication.getAuthorities())
                .thenAnswer(invocation -> Collections.singletonList(authority));
        when(authentication.isAuthenticated()).thenReturn(true);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        assertTrue(authorizationService.canPublishNews(123L));
    }

    @Test
    void canPublishNews_WithEnterprise_ReturnsTrue() {
        GrantedAuthority authority = new SimpleGrantedAuthority("ENTERPRISE");
        when(authentication.getAuthorities())
                .thenAnswer(invocation -> Collections.singletonList(authority));
        when(authentication.isAuthenticated()).thenReturn(true);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        assertTrue(authorizationService.canPublishNews(123L));
    }

    @Test
    void canPublishNews_WithNormalUser_ReturnsFalse() {
        GrantedAuthority authority = new SimpleGrantedAuthority("USER");
        when(authentication.getAuthorities())
                .thenAnswer(invocation -> Collections.singletonList(authority));
        when(authentication.isAuthenticated()).thenReturn(true);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        assertFalse(authorizationService.canPublishNews(123L));
    }

    @Test
    void getUserType_WithAdminRole_ReturnsAdmin() {
        GrantedAuthority authority = new SimpleGrantedAuthority("ADMIN");
        when(authentication.getAuthorities())
                .thenAnswer(invocation -> Collections.singletonList(authority));
        when(authentication.isAuthenticated()).thenReturn(true);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        assertEquals("ADMIN", authorizationService.getUserType(123L));
    }

    @Test
    void getUserType_WithEnterpriseRole_ReturnsEnterprise() {
        GrantedAuthority authority = new SimpleGrantedAuthority("ENTERPRISE");
        when(authentication.getAuthorities())
                .thenAnswer(invocation -> Collections.singletonList(authority));
        when(authentication.isAuthenticated()).thenReturn(true);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        assertEquals("ENTERPRISE", authorizationService.getUserType(123L));
    }

    @Test
    void getUserType_WithNoSpecialRole_ReturnsNormal() {
        GrantedAuthority authority = new SimpleGrantedAuthority("USER");
        when(authentication.getAuthorities())
                .thenAnswer(invocation -> Collections.singletonList(authority));
        when(authentication.isAuthenticated()).thenReturn(true);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        assertEquals("NORMAL", authorizationService.getUserType(123L));
    }

    @Test
    void getUserType_WithUnAuthenticatedUser_ReturnsNull() {
        when(securityContext.getAuthentication()).thenReturn(null);
        assertNull(authorizationService.getUserType(123L));
    }

    @Test
    void getUserType_WithMultipleRoles_ReturnsHighestPriorityRole() {
        GrantedAuthority adminAuth = new SimpleGrantedAuthority("ADMIN");
        GrantedAuthority userAuth = new SimpleGrantedAuthority("USER");
        when(authentication.getAuthorities())
                .thenAnswer(invocation -> List.of(userAuth, adminAuth));
        when(authentication.isAuthenticated()).thenReturn(true);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        assertEquals("ADMIN", authorizationService.getUserType(123L));
    }

    @Test
    void canAuditNews_WithAdmin_ReturnsTrue() {
        GrantedAuthority authority = new SimpleGrantedAuthority("ADMIN");
        when(authentication.getAuthorities())
                .thenAnswer(invocation -> Collections.singletonList(authority));
        when(authentication.isAuthenticated()).thenReturn(true);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        assertTrue(authorizationService.canAuditNews(123L));
    }

    @Test
    void canAuditNews_WithNonAdmin_ReturnsFalse() {
        assertFalse(authorizationService.canAuditNews(123L));
    }

    @Test
    void canAuditNews_WithNullUserId_ReturnsFalse() {
        assertFalse(authorizationService.canAuditNews(null));
    }

    @Test
    void anyMethod_WithNullSecurityContext_HandlesGracefully() {
        SecurityContextHolder.clearContext();
        assertFalse(authorizationService.isAdmin(123L));
        assertFalse(authorizationService.isEnterprise(123L));
        assertThrows(UnauthorizedException.class, () -> authorizationService.getCurrentUserId());
    }
}