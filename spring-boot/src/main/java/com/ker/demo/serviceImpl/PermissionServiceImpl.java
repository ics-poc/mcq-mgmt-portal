package com.ker.demo.serviceImpl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ker.demo.domin.Permission;
import com.ker.demo.entity.PermissionEntity;
import com.ker.demo.repository.PermissionRepo;
import com.ker.demo.service.PermissionService;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class PermissionServiceImpl implements PermissionService {

	@Autowired
	private PermissionRepo permissionRepo;

	@Override
	public List<Permission> getAllPermissions() {
		List<Permission> permissions = permissionRepo.findAll().stream()
				.map(this::convertEntityToData)
				.collect(Collectors.toList());

		if (permissions.isEmpty()) {
			return List.of();
		}
		return permissions;
	}

	private Permission convertEntityToData(PermissionEntity entity) {
		Permission permission = new Permission();
		permission.setCode(entity.getCode());
		permission.setDescription(entity.getDescription());
		permission.setCreatedUserId(entity.getCreatedUserId());
		permission.setModifiedUserId(entity.getModifiedUserId());
		permission.setCreatedDate(entity.getCreatedDate());
		permission.setModifiedDate(entity.getModifiedDate());
		return permission;
	}

	private PermissionEntity convertDataToEntity(Permission permission) {
		PermissionEntity entity = new PermissionEntity();
		entity.setCode(permission.getCode());
		entity.setDescription(permission.getDescription());
		entity.setCreatedUserId(permission.getCreatedUserId());
		entity.setModifiedUserId(permission.getModifiedUserId());
		entity.setCreatedDate(permission.getCreatedDate());
		entity.setModifiedDate(permission.getModifiedDate());
		return entity;
	}

	@Override
	public java.util.Optional<Permission> getPermissionByCode(String code) {
		return permissionRepo.findById(code).map(this::convertEntityToData);
	}

	@Override
	public Permission createPermission(Permission permission) {
		if (permission == null) throw new IllegalArgumentException("Permission is null");
		PermissionEntity entity = convertDataToEntity(permission);
		PermissionEntity saved = permissionRepo.save(entity);
		return convertEntityToData(saved);
	}

	@Override
	public Permission updatePermission(String code, Permission permission) {
		PermissionEntity existing = permissionRepo.findById(code)
				.orElseThrow(() -> new IllegalArgumentException("Permission not found"));
		if (permission.getDescription() != null) existing.setDescription(permission.getDescription());
		if (permission.getModifiedUserId() != null) existing.setModifiedUserId(permission.getModifiedUserId());
		if (permission.getModifiedDate() != null) existing.setModifiedDate(permission.getModifiedDate());
		PermissionEntity saved = permissionRepo.save(existing);
		return convertEntityToData(saved);
	}

	@Override
	public void deletePermission(String code) {
		PermissionEntity existing = permissionRepo.findById(code).orElseThrow(() -> new IllegalArgumentException("Permission not found"));
		permissionRepo.delete(existing);
	}
}