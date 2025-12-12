package com.ker.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ker.demo.domin.Permission;
import com.ker.demo.service.PermissionService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
// @Tag(name = "Permission Controller", description = "Operations related to permissions")
public class PermissionController {

	@Autowired
	private PermissionService permissionService;

	@GetMapping("/permissions")
	@Operation(summary = "Get all permissions", description = "Returns a list of all permissions")
	public List<Permission> getAllPermissions() {
		return permissionService.getAllPermissions();
	}


//	@GetMapping("/permissions/{code}")
//	public ResponseEntity<?> getPermissionByCode(@PathVariable String code) {
//		var opt = permissionService.getPermissionByCode(code);
//		if (opt.isPresent()) {
//			return ResponseEntity.ok(opt.get());
//		}
//		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Permission not found");
//	}
//
	@PostMapping("/permissions")
	public ResponseEntity<?> createPermission(@RequestBody Permission permission) {
		try {
			Permission created = permissionService.createPermission(permission);
			return ResponseEntity.status(HttpStatus.CREATED).body(created);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}}
	}
	
//
//	@PutMapping("/permissions/{code}")
//	public ResponseEntity<?> updatePermission(@PathVariable String code, @RequestBody Permission permission) {
//		try {
//			Permission updated = permissionService.updatePermission(code, permission);
//			return ResponseEntity.ok(updated);
//		} catch (IllegalArgumentException e) {
//			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
//		}
//	}
//
//	@DeleteMapping("/permissions/{code}")
//	public ResponseEntity<?> deletePermission(@PathVariable String code) {
//		try {
//			permissionService.deletePermission(code);
//			return ResponseEntity.noContent().build();
//		} catch (IllegalArgumentException e) {
//			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
//		}
//	}
//}