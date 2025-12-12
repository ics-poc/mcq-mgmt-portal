package com.ker.demo.exception;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(RoleAlreadyExistsException.class)
	public ResponseEntity<Object> handleRoleAlreadyExists(RoleAlreadyExistsException ex) {
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("timestamp", LocalDateTime.now());
		body.put("status", HttpStatus.NOT_ACCEPTABLE.value());
		body.put("error", "Conflict");
		body.put("message", ex.getMessage());
		body.put("path", "/api/roles/user/{userId}/role/{role}");

		return new ResponseEntity<>(body, HttpStatus.CONFLICT);
	}
	
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<Map<String, Object>> handleResourceNotFound(ResourceNotFoundException ex) {
		Map<String, Object> error = new HashMap<>();
		error.put("timestamp", ZonedDateTime.now());
		error.put("status", HttpStatus.NOT_FOUND.value());
		error.put("error", "Not Found");
		error.put("message", ex.getMessage());

		return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(ValidationException.class)
	public ResponseEntity<Map<String, Object>> handleValidationException(ValidationException ex) {
	    Map<String, Object> error = new HashMap<>();
	    error.put("timestamp", ZonedDateTime.now());
	    error.put("status", HttpStatus.BAD_REQUEST.value());
	    error.put("error", "Validation Error");
	    error.put("message", ex.getMessage());

	    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}	
	
}
