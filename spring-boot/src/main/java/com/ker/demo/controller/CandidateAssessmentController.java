package com.ker.demo.controller;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ker.demo.service.CandidateAssessmentService;
import com.ker.demo.service.CandidateAssessmentService.SubmitRequest;

@RestController
@RequestMapping("/{userId}/assessment")
public class CandidateAssessmentController {

	@Autowired
	private CandidateAssessmentService service;

	@GetMapping("/{managerTemplateId}")
	public ResponseEntity<?> getExamDetails(@PathVariable("userId") Long userId,
			@PathVariable("managerTemplateId") Long managerTemplateId) {
		try {
			Map<String, Object> details = service.getExamDetails(userId, managerTemplateId);
			return ResponseEntity.ok(details);
		} catch (NoSuchElementException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
		}
	}

	@PostMapping("/{managerTemplateId}/submit")
	public ResponseEntity<?> submitExam(@PathVariable("userId") Long userId,
			@PathVariable("managerTemplateId") Long managerTemplateId, @RequestBody SubmitRequest request) {
		try {
			Map<String, Object> response = service.submitExam(userId, managerTemplateId, request);
			return ResponseEntity.status(HttpStatus.CREATED).body(response);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
		} catch (NoSuchElementException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
		}
	}
	
	@GetMapping("/results/{managerTemplateId}")
	public ResponseEntity<?> getAllResults(@PathVariable("userId") Long userId, @PathVariable(required = false) Long managerTemplateId) {
		try {
			List<Map<String, Object>> results = service.getAllResults(userId, managerTemplateId);
			return ResponseEntity.ok(results);
		} catch (NoSuchElementException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
		}
	}

}