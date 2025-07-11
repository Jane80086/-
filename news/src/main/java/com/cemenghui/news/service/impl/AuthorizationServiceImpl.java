package com.cemenghui.news.service.impl;

import com.cemenghui.entity.User; // 引入 system 模块的 User 实体，如果需要在 news 模块中使用的话
import com.cemenghui.news.constants.NewsConstants;
import com.cemenghui.news.exception.UnauthorizedException;
import com.cemenghui.news.mapper.NewsMapper; // 用于 isOwner 检查
import com.cemenghui.news.mapper.UserMapper; // 假设 News 模块也需要 UserMapper，用于根据 username 查 id
import com.cemenghui.news.service.AuthorizationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // 引入Slf4j用于日志
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt; // 引入 Jwt
import org.springframework.security.core.GrantedAuthority; // 引入 GrantedAuthority
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collection; // 引入 Collection
import java.util.Optional; // 引入 Optional
import java.util.stream.Collectors; // 引入 Collectors

@Service
@RequiredArgsConstructor
@Slf4j // 添加 Slf4j 注解
public class AuthorizationServiceImpl implements AuthorizationService {

    private final UserMapper userMapper; // 假设此 Mapper 在 news 模块中，用于根据 username 查 ID
    private final NewsMapper newsMapper;

    /**
     * 从 Spring Security 上下文获取当前认证用户的 ID。
     * 这是最核心的修改。
     */
    @Override
    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() ||
                "anonymousUser".equals(authentication.getPrincipal())) {
            // 对于未认证用户，直接返回 null 或抛出异常，取决于业务需求
            // 在有 @PreAuthorize 的情况下，这部分代码通常不会被触发，除非是公共接口
            // 但作为安全第一层面的防御，可以保留
            log.debug("No authenticated user found in SecurityContext.");
            throw new UnauthorizedException("用户未登录或无法获取到认证信息"); // 明确指出未登录
        }

        if (authentication.getPrincipal() instanceof Jwt) {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            // 从 JWT claims 中获取 userId
            // 假设你在 system 模块的 JWTUtil 中将 userId 存入了一个名为 "userId" 的 claim
            // 确保 system 模块的 JWTUtil 中有 claims.put("userId", user.getId());
            Object userIdClaim = jwt.getClaim("userId"); // **关键：获取 JWT 中的 userId claim**
            if (userIdClaim instanceof Number) {
                return ((Number) userIdClaim).longValue();
            } else if (userIdClaim != null) {
                // 如果 userIdClaim 是字符串形式的ID，尝试转换
                try {
                    return Long.valueOf(userIdClaim.toString());
                } catch (NumberFormatException e) {
                    log.error("JWT 'userId' claim is not a valid number: {}", userIdClaim, e);
                }
            }
            // 如果 JWT 中没有 userId 或格式不正确，则无法获取
            log.warn("JWT principal does not contain a valid 'userId' claim or is not a number.");
            throw new UnauthorizedException("无法从JWT中获取用户ID");
        } else {
            // Fallback for other principal types (e.g., if you have other auth mechanisms)
            // 你可以根据你的实际情况添加其他 Principal 类型的处理
            log.warn("Unexpected principal type in SecurityContext: {}", authentication.getPrincipal().getClass().getName());
            throw new UnauthorizedException("不支持的认证主体类型");
        }
    }

    /**
     * 检查当前用户是否是某个新闻的所有者。
     * 此方法应该依赖于从 SecurityContext 获取的当前用户ID。
     */
    @Override
    public boolean isOwner(Long userId, Long newsId) {
        // userId 参数现在是来自调用方（通常是getCurrentUserId()）
        if (userId == null || newsId == null) {
            return false;
        }
        // 直接使用 newsMapper 检查，避免额外的用户查询
        return newsMapper.existsByIdAndUserId(newsId, userId);
    }

    /**
     * 检查当前用户是否是管理员。
     * 直接从 SecurityContext 中的权限列表检查，避免数据库查询。
     */
    @Override
    public boolean isAdmin(Long userId) {
        // userId 参数理论上可以忽略，因为权限基于当前认证用户
        // 但为了兼容旧方法签名，可以保留，但其值应来自 getCurrentUserId()
        if (userId == null) return false; // 同样，如果 userId 为 null，则不是管理员

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        // 检查认证主体是否有 ROLE_ADMIN 权限
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals(NewsConstants.ROLE_ADMIN)); // 确保 ROLE_ADMIN 定义正确
    }

    /**
     * 检查当前用户是否是企业用户。
     * 直接从 SecurityContext 中的权限列表检查，避免数据库查询。
     */
    @Override
    public boolean isEnterprise(Long userId) {
        if (userId == null) return false;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        // 检查认证主体是否有 ROLE_ENTERPRISE 权限
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals(NewsConstants.ROLE_ENTERPRISE)); // 确保 ROLE_ENTERPRISE 定义正确
    }

    /**
     * 强制检查管理员权限，如果不是则抛出 UnauthorizedException。
     * 这个方法通常在 @PreAuthorize 无法完全覆盖的业务逻辑深处使用。
     * 但在有 @PreAuthorize("hasRole('ADMIN')") 的情况下，此方法可以简化。
     */
    @Override
    public void checkAdminPermission(Long userId) {
        // userId 参数现在应该通过 getCurrentUserId() 获取
        // 确保 isAdmin 方法正确使用了 SecurityContextHolder
        if (!isAdmin(userId)) { // 调用我们修改后的 isAdmin 方法
            throw new UnauthorizedException("需要管理员权限");
        }
    }

    /**
     * 强制检查企业用户或管理员权限。
     */
    @Override
    public void checkEnterprisePermission(Long userId) {
        if (!isEnterprise(userId) && !isAdmin(userId)) { // 调用我们修改后的 isEnterprise 和 isAdmin
            throw new UnauthorizedException("需要企业用户或管理员权限");
        }
    }

    // --- 以下方法也应基于 SecurityContext 的权限信息进行优化 ---

    @Override
    public boolean canEditNews(Long userId, Long newsId) {
        if (userId == null || newsId == null) return false;
        // 如果当前用户是管理员，直接返回 true
        if (isAdmin(userId)) return true;
        // 否则，检查是否是新闻所有者
        return isOwner(userId, newsId);
    }

    @Override
    public boolean canDeleteNews(Long userId, Long newsId) {
        if (userId == null || newsId == null) return false;
        // 如果当前用户是管理员，直接返回 true
        if (isAdmin(userId)) return true;
        // 否则，检查是否是新闻所有者
        return isOwner(userId, newsId);
    }

    @Override
    public boolean hasPermission(Long userId, String operation, Long resourceId) {
        if (userId == null) {
            return false;
        }

        // 管理员拥有所有权限，直接从 SecurityContext 获取权限判断
        if (isAdmin(userId)) { // 调用 isAdmin 方法
            return true;
        }

        // 根据具体操作和用户类型判断权限
        // 注意：这里的用户类型也应该从 SecurityContext 的权限中获取，而不是查 userMapper
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        boolean isEnterpriseUser = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals(NewsConstants.ROLE_ENTERPRISE));


        switch (operation) {
            case NewsConstants.PERMISSION_EDIT:
            case NewsConstants.PERMISSION_DELETE:
                return canEditNews(userId, resourceId);
            case NewsConstants.PERMISSION_CREATE:
            case NewsConstants.PERMISSION_PUBLISH:
                return isEnterpriseUser; // 直接判断是否企业用户
            case NewsConstants.PERMISSION_AUDIT:
                // 审核权限已由 isAdmin(userId) 处理，这里可以简化
                return false; // 如果前面isAdmin已经返回false，说明不是管理员，就没有审核权限
            case NewsConstants.PERMISSION_VIEW:
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean canPublishNews(Long userId) {
        if (userId == null) return false;
        return isAdmin(userId) || isEnterprise(userId); // 直接调用 isAdmin 和 isEnterprise
    }

    /**
     * 检查用户是否可以审核新闻
     */
    public boolean canAuditNews(Long userId) {
        if (userId == null) {
            return false;
        }
        return isAdmin(userId); // 直接调用 isAdmin
    }

    /**
     * 获取用户类型（这个方法现在变得多余，或者需要从 SecurityContext 提取角色）
     * 如果你仍然需要在其他地方获取 "ADMIN" / "ENTERPRISE" 这样的字符串，
     * 应该从 GrantedAuthority 中提取，而不是从数据库查询。
     *
     * @Deprecated // 推荐弃用，因为可以通过 SecurityContext 直接获取权限
     */
    @Override
    public String getUserType(Long userId) {
        // userId 参数现在应直接从 getCurrentUserId() 获得
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            Optional<? extends GrantedAuthority> adminRole = authorities.stream()
                    .filter(a -> a.getAuthority().equals(NewsConstants.ROLE_ADMIN))
                    .findFirst();
            if (adminRole.isPresent()) {
                return "ADMIN"; // 返回字符串形式
            }

            Optional<? extends GrantedAuthority> enterpriseRole = authorities.stream()
                    .filter(a -> a.getAuthority().equals(NewsConstants.ROLE_ENTERPRISE))
                    .findFirst();
            if (enterpriseRole.isPresent()) {
                return "ENTERPRISE"; // 返回字符串形式
            }
            // 如果你还有其他角色，可以继续添加判断
            return "NORMAL"; // 默认或其他角色
        }
        return null;
    }

    private boolean isNewsOwner(User user, Long newsId) {
        // 这个方法不应该存在于 AuthorizationService 中，因为 AuthorizationService
        // 应该只关注权限校验，而不直接操作 User 实体或 News 实体。
        // isOwner(Long userId, Long newsId) 已经提供了你所需的功能。
        return false;
    }
}