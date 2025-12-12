package com.ker.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ker.demo.domin.Assessment;
import com.ker.demo.service.AssessmentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/assessment-hub")
@Tag(name = "Assessment Controller", description = "Operations related to assessment scheduling")
public class AssessmentController {

    @Autowired
    private AssessmentService assessmentService;

    @PostMapping("/schedule-exam")
    @Operation(summary = "Schedule an exam", description = "Creates a new assessment schedule by copying admin template to manager template and mapping candidates")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Assessment scheduled successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
        @ApiResponse(responseCode = "404", description = "Admin template not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> scheduleExam(@RequestBody Assessment assessment) {
        try {
            Assessment created = assessmentService.createAssessment(assessment);
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Internal server error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
