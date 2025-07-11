package com.cemenghui.system.repository;

import com.cemenghui.system.entity.Role;
import com.cemenghui.system.entity.Permission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface RoleMapper {
    List<Role> getAllRoles();
    int insertRole(Role role);
    int updateRole(Role role);
    int deleteRole(Long id);
    List<Permission> getRolePermissions(Long roleId);
    int deleteRolePermissions(Long roleId);
    int insertRolePermission(@Param("roleId") Long roleId, @Param("permissionId") Long permissionId);
} 