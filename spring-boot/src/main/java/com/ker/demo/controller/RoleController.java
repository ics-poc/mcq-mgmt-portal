
package com.ker.demo.controller;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ker.demo.domin.RoleRequest;
import com.ker.demo.entity.RoleEntity;
import com.ker.demo.service.RoleService;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/roles")
@Tag(name = "Role Controller", description = "Operations related to roles and user-role mapping")
public class RoleController {

	@Autowired
	private RoleService roleService;

	@GetMapping
	public List<RoleEntity> getAllRoles() {
		return roleService.getAllRoles();
	}

//	@GetMapping("/{code}")
//	public ResponseEntity<?> getRoleByCode(@PathVariable String code) {
//		return roleService.getRoleByCode(code).<ResponseEntity<?>>map(ResponseEntity::ok)
//				.orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Role not found"));
//	}

	@PostMapping
	public ResponseEntity<?> createRole(@RequestBody RoleRequest roleRequest) {
		
		try {
			// Basic validation
			if (roleRequest.getCode() == null || roleRequest.getCode().isBlank())
				return ResponseEntity.badRequest().body("Code is required");
			if (roleRequest.getName() == null || roleRequest.getName().isBlank())
				return ResponseEntity.badRequest().body("Name is required");
			if (roleRequest.getType() == null || roleRequest.getType().isBlank())
				return ResponseEntity.badRequest().body("Type is required");
//			if (roleRequest.getPermissionCodes() == null || roleRequest.getPermissionCodes().isEmpty())
//				return ResponseEntity.badRequest().body("At least one permission is required");

			RoleEntity role = new RoleEntity();
			role.setCode(roleRequest.getCode());
			role.setName(roleRequest.getName());
			role.setType(roleRequest.getType());
			role.setCreatedUserId("ADMIN");
			Timestamp timestamp = Timestamp.from(Instant.now());
			role.setCreatedDate(timestamp);

			// map permission codes to entities
//			java.util.Set<PermissionEntity> perms = new java.util.HashSet<>();
//			for (String code : roleRequest.getPermissionCodes()) {
//				PermissionEntity pe = permissionRepo.findById(code).orElse(null);
//				if (pe == null)
//					return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Permission not found: " + code);
//				perms.add(pe);
//			}
//			role.setPermissions(perms);
			RoleEntity created = roleService.createRole(role);
			return ResponseEntity.status(HttpStatus.CREATED).body(created);
		
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating role");
		}
	}

//	@PutMapping("/{code}")
//	public ResponseEntity<?> updateRole(@PathVariable String code, @RequestBody RoleRequest roleRequest) {
//		try {
//			if (roleRequest.getName() == null || roleRequest.getName().isBlank())
//				return ResponseEntity.badRequest().body("Name is required");
//			if (roleRequest.getType() == null || roleRequest.getType().isBlank())
//				return ResponseEntity.badRequest().body("Type is required");
//			if (roleRequest.getPermissionCodes() == null || roleRequest.getPermissionCodes().isEmpty())
//				return ResponseEntity.badRequest().body("At least one permission is required");
//
//			RoleEntity role = new RoleEntity();
//			role.setName(roleRequest.getName());
//			role.setType(roleRequest.getType());
//			java.util.Set<PermissionEntity> perms = new java.util.HashSet<>();
//			for (String pcode : roleRequest.getPermissionCodes()) {
//				PermissionEntity pe = permissionRepo.findById(pcode).orElse(null);
//				if (pe == null)
//					return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Permission not found: " + pcode);
//				perms.add(pe);
//			}
//			role.setPermissions(perms);
//			RoleEntity updated = roleService.updateRole(code, role);
//			return ResponseEntity.ok(updated);
//		} catch (IllegalArgumentException e) {
//			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
//		} catch (Exception e) {
//			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating role");
//		}
//	}
//
//	@DeleteMapping("/{code}")
//	@PreAuthorize(PermissionConstant.PERMISSION_ADMIN_MANAGE)
//	public ResponseEntity<?> deleteRole(@PathVariable String code) {
//		try {
//			roleService.deleteRole(code);
//			return ResponseEntity.noContent().build();
//		} catch (IllegalArgumentException e) {
//			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
//		} catch (Exception e) {
//			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting role");
//		}
//	}
//
//	@Transactional
//	@PostMapping("/user/{userId}/role/{roleCode}")
//	@Operation(summary = "Assign role to user", description = "Assigns a role to a user by userId and roleCode")
//	public ResponseEntity<?> assignRoleToUser(@PathVariable Long userId, @PathVariable String roleCode) {
//
//		Optional<UserEntity> userOpt = userRepo.findById(userId);
//		Optional<RoleEntity> roleOpt = roleService.getRoleByCode(roleCode);
//
//		if (userOpt.isEmpty()) {
//			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
//		}
//		if (roleOpt.isEmpty()) {
//			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Role not found");
//		}
//
//		UserEntity user = userOpt.get();
//		RoleEntity role = roleOpt.get();
//
//		if (user.getUserRoleId() == null) {
//			user.setUserRoleId(role.getId().toString());
//			if (user.getRoles() == null) {
//				user.setRoles(new HashSet<>());
//			}
//
//			user.getRoles().add(role);
//			userRepo.save(user);
//
//			return ResponseEntity.ok("Role assigned to user");
//		}
//
//		throw new RoleAlreadyExistsException("Already role exists for this user with id " + userId);
//	}
//
//	// Get a user's role by userId
//	@GetMapping("/user/{userId}/role")
//	@Operation(summary = "Get user's role", description = "Returns the role assigned to a user by userId")
//	public ResponseEntity<?> getUserRole(@PathVariable Long userId) {
//		Optional<UserEntity> userOpt = userRepo.findById(userId);
//		if (userOpt.isEmpty()) {
//			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
//		}
//		String roleCode = userOpt.get().getUserRoleId();
//		if (roleCode == null) {
//			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User has no role assigned");
//		}
//		Optional<RoleEntity> roleOpt = roleService.getRoleByCode(roleCode);
//		if (roleOpt.isEmpty()) {
//			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Role not found");
//		}
//		return ResponseEntity.ok(roleOpt.get());
//	}
//
//	// Remove a user's role (set usergroupId to null)
//	@DeleteMapping("/user/{userId}/role")
//	@Operation(summary = "Remove user's role", description = "Removes the role assignment from a user by userId")
//	public ResponseEntity<?> removeUserRole(@PathVariable Long userId) {
//
//		Optional<UserEntity> userOpt = userRepo.findById(userId);
//
//		if (userOpt.isEmpty()) {
//			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
//		}
//		UserEntity user = userOpt.get();
//		if (user.getUserRoleId() == "CANDIDATE") {
//			throw new RuntimeErrorException(null, "Invalid user");
//		}
//		user.setUserRoleId(null);
//		userRepo.save(user);
//		return ResponseEntity.ok("Role removed from user");
//
//	}

}
