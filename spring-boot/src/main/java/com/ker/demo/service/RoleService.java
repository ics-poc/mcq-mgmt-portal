package com.ker.demo.service;

import java.util.List;
import java.util.Optional;

import com.ker.demo.entity.RoleEntity;

public interface RoleService {
	
    List<RoleEntity> getAllRoles();
    
    Optional<RoleEntity> getRoleByCode(String code);
    
    RoleEntity createRole(RoleEntity role);
    
    RoleEntity updateRole(String code, RoleEntity role);
    
    void deleteRole(String code);
}