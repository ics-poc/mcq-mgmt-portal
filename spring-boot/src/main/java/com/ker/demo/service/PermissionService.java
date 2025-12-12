package com.ker.demo.service;

import java.util.List;
import java.util.Optional;

import com.ker.demo.domin.Permission;

public interface PermissionService {
    List<Permission> getAllPermissions();
    Optional<Permission> getPermissionByCode(String code);
    Permission createPermission(Permission permission);
    Permission updatePermission(String code, Permission permission);
    void deletePermission(String code);
}