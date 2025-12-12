package com.ker.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ker.demo.entity.ReportingMangerEntity;
import com.ker.demo.entity.UserEntity;
import com.ker.demo.service.ReportingManagerService;

@RestController
public class MangerReportController {
	@Autowired
	private ReportingManagerService reportService;
	
    @PostMapping("/create")
    public ResponseEntity<ReportingMangerEntity> createManager(@RequestParam String managerId) {
        ReportingMangerEntity manager = reportService.createManager(managerId);
        return ResponseEntity.ok(manager);
    }

    // Assign a user to a manager
    @PostMapping("/{managerId}/assign-user/{userId}")
    public ResponseEntity<ReportingMangerEntity> assignUserToManager(
            @PathVariable Long managerId,
            @PathVariable Long userId) {
        ReportingMangerEntity updatedManager = reportService.assignUserToManager(managerId, userId);
        return ResponseEntity.ok(updatedManager);
    }

    // Get all users under a manager
    @GetMapping("/{managerId}/users")
    public ResponseEntity<List<UserEntity>> getUsersByManager(@PathVariable Long managerId) {
        List<UserEntity> users = reportService.getUsersByManager(managerId);
        return ResponseEntity.ok(users);
    }
}