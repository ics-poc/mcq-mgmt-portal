package com.ker.demo.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ker.demo.domin.Status;
import com.ker.demo.service.UserAssessmentService;

@RestController
@RequestMapping("/assessments")
public class UserAssessmentController {

	@Autowired
    private UserAssessmentService userAssessmentService;

	@GetMapping("/user/{userId}")
    public ResponseEntity<List<Map<String, Object>>> getAllAssessments(@PathVariable Long userId) {
		List<Map<String, Object>> assessments = userAssessmentService.getAllAssessmentsForUser(userId);
	    return ResponseEntity.ok(assessments);
	}
	
	@GetMapping("/manager/{managerId}/status-count")
	public ResponseEntity<Map<String, Object>> getAllUserAssessmentCountsByManager(@PathVariable Long managerId) {
	    Map<String, Object> result = userAssessmentService.getAllUserAssessmentCountsByManager(managerId);
	    return ResponseEntity.ok(result);
	}

}
