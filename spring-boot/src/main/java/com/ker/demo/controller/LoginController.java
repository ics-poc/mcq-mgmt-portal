package com.ker.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ker.demo.domin.User;
import com.ker.demo.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Login Controller", description = "Operations related to User Login")
public class LoginController {
	
	@Autowired
	UserService userService;
	
	@PostMapping("/login")
	@Operation(summary = "User login", description = "Authenticates a user with email and password and returns user details")
	public User login(@RequestBody User request) {
		User user = userService.login(request.getEmail(), request.getPassword());
		//user.setPassword(null);
		return user;
	}

}
