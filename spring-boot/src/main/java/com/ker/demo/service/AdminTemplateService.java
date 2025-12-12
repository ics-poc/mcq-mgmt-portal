package com.ker.demo.service;

import java.util.List;

import com.ker.demo.domin.AdminTemplate;

public interface AdminTemplateService {

	AdminTemplate createAdminTemplate(AdminTemplate template);

	AdminTemplate getAdminTemplateWithCategories(Long id);

	void deleteAdminTemplate(Long id);

	AdminTemplate updateAdminTemplate(Long id, AdminTemplate updatedTemplate);

	List<AdminTemplate> getAllAdminTemplates();
}
