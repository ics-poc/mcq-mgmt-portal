package com.ker.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {	

	@GetMapping("/health/check")
	public String hello() {
		return "Hello, Employee Evaluation Management Portal is up and running!";
	}
}
