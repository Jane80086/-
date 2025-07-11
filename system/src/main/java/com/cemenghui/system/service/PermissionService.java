package com.cemenghui.system.service;

import com.cemenghui.system.entity.Permission;
import java.util.List;

public interface PermissionService {
    List<Permission> getAllPermissions();
    void createPermission(Permission permission);
    void updatePermission(Permission permission);
    void deletePermission(Long id);
} 