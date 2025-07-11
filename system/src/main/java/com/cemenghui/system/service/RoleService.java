package com.cemenghui.system.service;

import com.cemenghui.system.entity.Role;
import com.cemenghui.system.entity.Permission;
import java.util.List;

public interface RoleService {
    List<Role> getAllRoles();
    void createRole(Role role);
    void updateRole(Role role);
    void deleteRole(Long id);
    List<Permission> getRolePermissions(Long roleId);
    void assignPermissions(Long roleId, List<Long> permissionIds);
} 