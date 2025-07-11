package com.cemenghui.system.controller;

import com.cemenghui.system.entity.Permission;
import com.cemenghui.system.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/permissions")
public class PermissionController {
    @Autowired
    private PermissionService permissionService;

    @GetMapping
    public List<Permission> getAllPermissions() {
        return permissionService.getAllPermissions();
    }

    @PostMapping
    public void createPermission(@RequestBody Permission permission) {
        permissionService.createPermission(permission);
    }

    @PutMapping("/{id}")
    public void updatePermission(@PathVariable Long id, @RequestBody Permission permission) {
        permission.setId(id);
        permissionService.updatePermission(permission);
    }

    @DeleteMapping("/{id}")
    public void deletePermission(@PathVariable Long id) {
        permissionService.deletePermission(id);
    }
} 