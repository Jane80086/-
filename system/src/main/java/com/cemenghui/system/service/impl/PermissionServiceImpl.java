package com.cemenghui.system.service.impl;

import com.cemenghui.system.entity.Permission;
import com.cemenghui.system.repository.PermissionMapper;
import com.cemenghui.system.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PermissionServiceImpl implements PermissionService {
    @Autowired
    private PermissionMapper permissionMapper;

    @Override
    public List<Permission> getAllPermissions() {
        return permissionMapper.getAllPermissions();
    }

    @Override
    public void createPermission(Permission permission) {
        permissionMapper.insertPermission(permission);
    }

    @Override
    public void updatePermission(Permission permission) {
        permissionMapper.updatePermission(permission);
    }

    @Override
    public void deletePermission(Long id) {
        permissionMapper.deletePermission(id);
    }
} 