package com.cemenghui.system.controller;

import com.cemenghui.system.entity.Role;
import com.cemenghui.system.entity.Permission;
import com.cemenghui.system.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/roles")
public class RoleController {
    @Autowired
    private RoleService roleService;

    @GetMapping
    public List<Role> getAllRoles() {
        return roleService.getAllRoles();
    }

    @PostMapping
    public void createRole(@RequestBody Role role) {
        roleService.createRole(role);
    }

    @PutMapping("/{id}")
    public void updateRole(@PathVariable Long id, @RequestBody Role role) {
        role.setId(id);
        roleService.updateRole(role);
    }

    @DeleteMapping("/{id}")
    public void deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
    }

    @GetMapping("/{id}/permissions")
    public List<Permission> getRolePermissions(@PathVariable Long id) {
        return roleService.getRolePermissions(id);
    }

    @PostMapping("/{id}/permissions")
    public void assignPermissions(@PathVariable Long id, @RequestBody List<Long> permissionIds) {
        roleService.assignPermissions(id, permissionIds);
    }
} 