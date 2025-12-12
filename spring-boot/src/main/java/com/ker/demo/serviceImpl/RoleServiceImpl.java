package com.ker.demo.serviceImpl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ker.demo.entity.RoleEntity;
import com.ker.demo.exception.ValidationException;
import com.ker.demo.repository.RoleRepo;
import com.ker.demo.service.RoleService;

@Service
public class RoleServiceImpl implements RoleService {
	
    @Autowired
    private RoleRepo roleRepo;

//    @Autowired
//    private PermissionRepo permissionRepo;

    @Override
    public List<RoleEntity> getAllRoles() {
        return roleRepo.findAll();
    }

    @Override
    public Optional<RoleEntity> getRoleByCode(String code) {
        if (code == null) {
            throw new ValidationException("Role code must not be null.");
        }

        RoleEntity role = roleRepo.findByCode(code.toUpperCase())
            .orElseThrow(() -> new ValidationException("Invalid role code: " + code));

        return Optional.of(role);
    }

    @Override
    @Transactional
    public RoleEntity createRole(RoleEntity role) {
        if (roleRepo.existsByCode(role.getCode())) {
            throw new IllegalArgumentException("Role code already exists");
        }
        // validate permissions exist
//        if (role.getPermissions() != null) {
//            role.getPermissions().forEach(p -> {
//                if (p == null || p.getCode() == null || !permissionRepo.existsById(p.getCode())) {
//                    throw new IllegalArgumentException("Permission not found: " + (p == null ? "null" : p.getCode()));
//                }
//            });
//        }
        return roleRepo.save(role);
    }

    @Override
    @Transactional
    public RoleEntity updateRole(String code, RoleEntity role) {
        RoleEntity existing = roleRepo.findByCode(code).orElseThrow(() -> new IllegalArgumentException("Role not found"));
        existing.setName(role.getName());
        existing.setType(role.getType());
//        if (role.getPermissions() != null) {
//            role.getPermissions().forEach(p -> {
//                if (p == null || p.getCode() == null || !permissionRepo.existsById(p.getCode())) {
//                    throw new IllegalArgumentException("Permission not found: " + (p == null ? "null" : p.getCode()));
//                }
//            });
//            existing.setPermissions(role.getPermissions());
//        }
        return roleRepo.save(existing);
    }

	@Override
	@Transactional
	public void deleteRole(String code) {
		RoleEntity existing = roleRepo.findByCode(code)
				.orElseThrow(() -> new IllegalArgumentException("Role not found"));
		roleRepo.delete(existing);
	}
}