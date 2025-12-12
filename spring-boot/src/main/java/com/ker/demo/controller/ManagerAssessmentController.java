package com.ker.demo.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ker.demo.service.ManagerAssessmentService;

@RestController
@RequestMapping("/assessment-dashboard")
public class ManagerAssessmentController {

	@Autowired
    private ManagerAssessmentService managerAssessmentService;

    @GetMapping("/{managerId}")
    public List<Map<String, Object>> getDashboardByManager(@PathVariable String managerId) {
        return managerAssessmentService.getDashboardByManager(managerId);
    }
}