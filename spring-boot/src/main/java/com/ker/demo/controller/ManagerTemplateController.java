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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ker.demo.domin.ManagerTemplate;
import com.ker.demo.service.ManagerTemplateService;

@RestController
@RequestMapping("/managerTemplate")
public class ManagerTemplateController {

	@Autowired
	private ManagerTemplateService managerTemplateService;

	@PostMapping("/create")
	public ResponseEntity<ManagerTemplate> createAdminTemplate(@RequestBody ManagerTemplate template) {
		ManagerTemplate saved = managerTemplateService.createManagerTemplate(template);
		return new ResponseEntity<>(saved, HttpStatus.CREATED);
	}
	
	@GetMapping("/getAllManagerTemplates")
	public ResponseEntity<List<ManagerTemplate>> getAllTemplates() {
		List<ManagerTemplate> templates = managerTemplateService.getAllManagerTemplates();
		return ResponseEntity.ok(templates);
	}

	@GetMapping("/getManagerTemplateById/{id}")
	public ResponseEntity<ManagerTemplate> getAdminTemplate(@PathVariable Long id) {
		ManagerTemplate template = managerTemplateService.getManagerTemplateWithCategories(id);
		return ResponseEntity.ok(template);
	}

	@PutMapping("/updateManagerTemplates/{id}")
	public ResponseEntity<ManagerTemplate> updateAdminTemplate(@PathVariable Long id,
			@RequestBody ManagerTemplate updatedTemplate) {
		ManagerTemplate updated = managerTemplateService.updateManagerTemplate(id, updatedTemplate);
		return ResponseEntity.ok(updated);
	}

	@DeleteMapping("/deleteManagerTemplateById/{id}")
	public ResponseEntity<Void> deleteAdminTemplate(@PathVariable Long id) {
		managerTemplateService.deleteManagerTemplate(id);
		return ResponseEntity.noContent().build();
	}

}
