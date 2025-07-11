package com.cemenghui.system.service.impl;

import com.cemenghui.system.entity.Role;
import com.cemenghui.system.entity.Permission;
import com.cemenghui.system.repository.RoleMapper;
import com.cemenghui.system.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {
    @Autowired
    private RoleMapper roleMapper;

    @Override
    public List<Role> getAllRoles() {
        return roleMapper.getAllRoles();
    }

    @Override
    public void createRole(Role role) {
        roleMapper.insertRole(role);
    }

    @Override
    public void updateRole(Role role) {
        roleMapper.updateRole(role);
    }

    @Override
    public void deleteRole(Long id) {
        roleMapper.deleteRole(id);
        roleMapper.deleteRolePermissions(id);
    }

    @Override
    public List<Permission> getRolePermissions(Long roleId) {
        return roleMapper.getRolePermissions(roleId);
    }

    @Override
    @Transactional
    public void assignPermissions(Long roleId, List<Long> permissionIds) {
        roleMapper.deleteRolePermissions(roleId);
        if (permissionIds != null) {
            for (Long pid : permissionIds) {
                roleMapper.insertRolePermission(roleId, pid);
            }
        }
    }
} 