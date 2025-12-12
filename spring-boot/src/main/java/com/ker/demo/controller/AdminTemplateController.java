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

import com.ker.demo.domin.AdminTemplate;
import com.ker.demo.service.AdminTemplateService;

@RestController
@RequestMapping("/adminTemplate")
public class AdminTemplateController {

    @Autowired
    private AdminTemplateService adminTemplateService;

    @PostMapping("/create")
    public ResponseEntity<AdminTemplate> createAdminTemplate(@RequestBody AdminTemplate template) {
        AdminTemplate saved = adminTemplateService.createAdminTemplate(template);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @GetMapping("/getAdminTemplateById/{id}")
    public ResponseEntity<AdminTemplate> getAdminTemplate(@PathVariable Long id) {
        AdminTemplate template = adminTemplateService.getAdminTemplateWithCategories(id);
        return ResponseEntity.ok(template);
    }
    
    @DeleteMapping("/deleteAdminTemplateById/{id}")
    public ResponseEntity<Void> deleteAdminTemplate(@PathVariable Long id) {
        adminTemplateService.deleteAdminTemplate(id);
        return ResponseEntity.noContent().build();
    }
    
    @PutMapping("/updateAdminTemplates/{id}")
    public ResponseEntity<AdminTemplate> updateAdminTemplate(
            @PathVariable Long id,
            @RequestBody AdminTemplate updatedTemplate) {
        AdminTemplate updated = adminTemplateService.updateAdminTemplate(id, updatedTemplate);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/getAllAdminTemplates")
    public ResponseEntity<List<AdminTemplate>> getAllTemplates() {
        List<AdminTemplate> templates = adminTemplateService.getAllAdminTemplates();
        return ResponseEntity.ok(templates);
    }


}


