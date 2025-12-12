package com.ker.demo.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ker.demo.domin.QuestionAnswer;
import com.ker.demo.service.QuestionAnswerService;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Question Answer Controller", description = "Operations related to Question Answer")
@RequestMapping("/questionAnswer")
public class QuestionAnswerController {

	@Autowired
	private QuestionAnswerService service;

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<QuestionAnswer>> getQuestionsByCategory(@PathVariable Long categoryId) {
        List<QuestionAnswer> questions = service.getQuestionsByCategoryId(categoryId);
        return ResponseEntity.ok(questions);
    }
    
    @PutMapping("/status")
    public ResponseEntity<Map<String, String>> updateStatuses(
            @RequestBody Map<String, Map<Long, String>> request) {

        Map<Long, String> questions = request.get("questions");
        service.updateStatusFlags(questions);
        return ResponseEntity.ok(Map.of("message", "Status flags updated successfully"));
    }

    
}
