package com.cemenghui.system.repository;

import com.cemenghui.system.entity.Permission;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface PermissionMapper {
    List<Permission> getAllPermissions();
    int insertPermission(Permission permission);
    int updatePermission(Permission permission);
    int deletePermission(Long id);
} 