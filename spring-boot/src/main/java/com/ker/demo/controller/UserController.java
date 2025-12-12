package com.ker.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ker.demo.domin.User;
import com.ker.demo.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "User Controller", description = "Operations related to users")
public class UserController {

	@Autowired
	UserService userService;

	@PostMapping("/create/user")
	@Operation(summary = "Create a new user", description = "Accepts user details and creates a new user")
	public ResponseEntity<User> createUser(@RequestBody User user) {
		User createdUser = userService.createUser(user);
		return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
	}

	@PutMapping("/update/user/{id}")
	@Operation(summary = "Update an existing user", description = "Updates user details based on user ID")
	public User updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
		return userService.updateUser(id, updatedUser);
	}

	// Get user by ID
	@GetMapping("/user/{id}")
	@Operation(summary = "Get user by ID", description = "Returns the user matching the given ID")
	public ResponseEntity<User> getUserById(@PathVariable Long id) {
		User user = userService.getUserById(id);
		if (user != null) {
			return ResponseEntity.ok(user);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	// which permissions
	// Get all users
	@GetMapping("/users")
	@Operation(summary = "Get all users", description = "Returns a list of all users")
	public List<User> getAllUsers() {
		return userService.getAllUsers();
	}
	
	@GetMapping("users/managers")
	public List<User> getAllManagers() {
		return userService.getAllManagerDetails();
	}
	
	@GetMapping("users/managers/{managerId}")
	public List<User> getAllUsersByManagerId(@PathVariable Long managerId) {
		return userService.getAllUsersByManagerId(managerId);
	}

	// Delete A User
	@DeleteMapping("/user/{id}")
	@Operation(summary = "Delete user by ID", description = "Deletes the user matching the given ID")
	public int deleteUser(@PathVariable Long id) {
		userService.deleteUserById(id);
		return 1;
	}
}
